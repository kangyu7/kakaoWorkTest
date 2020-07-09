package com.dh.kakaopay.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.dh.kakaopay.cipher.AES256Util;
import com.dh.kakaopay.domain.CardData;
import com.dh.kakaopay.domain.CardInputData;
import com.dh.kakaopay.error.KakaopayException;
import com.dh.kakaopay.packet.CardPacket;
import com.dh.kakaopay.repository.CardDataRepository;
import com.dh.kakaopay.util.Tool;

@Service
public class KakaopayService {
	
	protected static final Logger logger = LoggerFactory.getLogger(KakaopayService.class);
	
	private static final String PAY = "PAYMENT";
	private static final String CANCEL = "CANCEL";
	private static final String SEPA = ",";
	
	@Autowired
	CardDataRepository cardDataRepository;
	
	@Transactional
	public CardData save(CardInputData cardInputData) {
		
		//TODO : validataion
		CardData cardData = new CardData();

		//관리번호 생성
		String inspNo = cardDataRepository.getInspNo();
		if(Tool.isNull(inspNo)) inspNo = "1";
		cardData.setInspNo(inspNo);
		
		//부가가치세 체크
		long vatAmt = cardInputData.getVatAmt();
		if(vatAmt == 0 && cardInputData.getPayAmt() > 1000) {
			vatAmt = Math.round(cardInputData.getPayAmt() / 11);
		} else {
			if (vatAmt > cardInputData.getPayAmt()) throw new KakaopayException("부가가치세가 결제금액보다 큽니다.");
		}
		
		//카드번호 , 유효기간, cvc 데이터 암복호화
		String encCardData = encCardString(cardInputData.getCardNo(), cardInputData.getExprYm(), cardInputData.getCvc());
		
		//card String 생성
		CardPacket cardPacket = new CardPacket();
		cardPacket.addNumber(CardPacket.PACKET_LENGTH-4, 4, "N");
		cardPacket.addString(PAY, 10);
		cardPacket.addString(inspNo, 20);
		cardPacket.addString(cardInputData.getCardNo(), 20);
		cardPacket.addNumber(cardInputData.getLoanYmd(), 2, "O");
		cardPacket.addString(cardInputData.getExprYm(), 4);
		cardPacket.addString(cardInputData.getCvc(), 3);
		cardPacket.addNumber(cardInputData.getPayAmt(), 10, "N");
		cardPacket.addNumber(vatAmt, 10, "O");
		cardPacket.addString("", 20);
		cardPacket.addString(encCardData, 300);
		cardPacket.addString("", 47);
		
		cardData.setCardString(cardPacket.outPacket());
		return cardDataRepository.save(cardData);
	}
	
//	@Transactional
//	public CardData cancel(CardInputData cardInputData) {
//		
//		List<CardData> list = cardDataRepository.findByinspNo(cardInputData.getInspNo());
//		
//		if (list.stream().filter(CardData -> CardData.getType() == PAY).count() == 0) {
//			throw new KakaopayException("결제이력이 없습니다.");
//		} else {
//			if (cardInputData.getCancelAmt() == 0) {
//				throw new KakaopayException("취소금액이 없습니다.");
//			}
//			
//			CardData payCardData = list.stream().filter(CardData -> CardData.getType() == PAY).findFirst().get();
//			
//			long payAmt = payCardData.getPayAmt();
//			long vatAmt = payCardData.getVatAmt();
//			
//			long cancelVatAmt = cardInputData.getCancelVatAmt();
//					
//			cancelVatAmt = Math.round(cardInputData.getCancelAmt() / 11);
//			
//			if(cardInputData.getCancelAmt() > payAmt || cancelVatAmt > vatAmt) {
//				throw new KakaopayException("결제취소 실패 : 취소 금액이 결제금액을 초과하였습니다. ");
//			}
//			
//			//남은 결제 금액 처리
//			payCardData.setPayAmt(payAmt - cardInputData.getCancelAmt());
//			payCardData.setVatAmt(vatAmt - cancelVatAmt);
//			cardDataRepository.save(payCardData);
//			
//			//card String 생성
//			CardPacket cardPacket = new CardPacket();
//			cardPacket.addNumber(CardPacket.PACKET_LENGTH-4, 4, "N");
//			cardPacket.addString(CANCEL, 10);
//			cardPacket.addString(payCardData.getInspNo(), 20);
//			cardPacket.addString(payCardData.getCardNo(), 20);
//			cardPacket.addNumber(payCardData.getLoanYmd(), 2, "O");
//			cardPacket.addString(payCardData.getExprYm(), 4);
//			cardPacket.addString(payCardData.getCvc(), 3);
//			cardPacket.addNumber(cardData.getCancelAmt(), 10, "N");
//			cardPacket.addNumber(cancelVatAmt, 10, "O");
//			cardPacket.addString(payCardData.getInspNo(), 20);
//			cardPacket.addString(payCardData.getCardString(), 300);
//			cardPacket.addString("", 47);
//			
//			//데이터설정
//			cardData.setType(CANCEL);
//			cardData.setCardString(cardPacket.outPacket());
//			cardDataRepository.save(cardData);
//			
//			inspNo = payCardData.getInspNo();
//			cardString = cardPacket.outPacket();
//		}
//		
//		//TODO : 요청결과
//		Map<String, String> map = new HashMap<>();
//		map.put("inspNo", inspNo);
//		map.put("cardString", cardString);
//		return map;
//	}
//
	public Map<String, String> find(CardInputData cardInputData) {
		
		Optional<CardData> data = cardDataRepository.findOneByinspNo(cardInputData.getInspNo());
		Map<String, String> rtnMap = new HashMap<String, String>();
		
		if(data.isPresent()) {
			
			CardData cardData = data.get();

			//카드정보
			String cardString = cardData.getCardString();
			
			Map<String, String> map = getPacketData(cardString);
			String encCardData = map.get("encCardData");
			
			List<String> list = Stream.of(encCardData.split(SEPA))
		      .map (elem -> new String(elem))
		      .collect(Collectors.toList());
			
			String cardNo = list.get(0);
			cardNo = decString(cardNo);
			String preCardNo = cardNo.substring(0,6);
			String lastCardNO = cardNo.substring(cardNo.length()-3,cardNo.length());
			
			String maskString = "";
			for(int i = 0; i < cardNo.length()-6-3; i++) {
				maskString += "*";
			}
			
			rtnMap.put("cardNo", preCardNo + maskString + lastCardNO);
			rtnMap.put("exprYm", decString(list.get(1)));
			rtnMap.put("cvc", decString(list.get(2)));
			
			rtnMap.put("inspNo", map.get("inspNo"));
			rtnMap.put("type", map.get("type"));
			rtnMap.put("payAmt", map.get("payAmt"));
			rtnMap.put("vatAmt", String.valueOf(Integer.parseInt(map.get("vatAmt"))));
			
		} else {
			throw new KakaopayException("이력이 없습니다.");
		}
		
		return rtnMap;
	}
	
	private String encCardString(String cardNo, String exprYm, String cvc) {
		
		String encCardData = "";
		AES256Util aes256Util;
		
		try {
			
			aes256Util = new AES256Util();
			String encCardNo = aes256Util.aesEncode(cardNo);
			String encExprYm = aes256Util.aesEncode(exprYm);
			String encCvc = aes256Util.aesEncode(cvc);
			encCardData = encCardNo + SEPA + encExprYm + SEPA + encCvc;
		} catch (Exception e) {
			throw new KakaopayException(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 처리중 오류가 발생하였습니다.");
		} 
		
		return encCardData;
	}
	
	private String decString(String value) {
		
		String decStr = "";
		
		try {
			decStr = new AES256Util().aesDecode(value);
		} catch (Exception e) {
			throw new KakaopayException(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 처리중 오류가 발생하였습니다.");
		} 
		
		return decStr;
	}
	
	private Map<String, String> getPacketData(String cardString) {
		Map<String, String> map = new HashMap<>();
		
		CardPacket cardPacket = new CardPacket();
		cardPacket.setIntPacketString(cardString);
		map.put("length", cardPacket.getString(4));
		map.put("type", cardPacket.getString(10));
		map.put("inspNo", cardPacket.getString(20));
		map.put("cardNo", cardPacket.getString(20));
		map.put("loanYmd", cardPacket.getString(2));
		map.put("exprYm", cardPacket.getString(4));
		map.put("cvc", cardPacket.getString(3));
		map.put("payAmt", cardPacket.getString(10));
		map.put("vatAmt", cardPacket.getString(10));
		map.put("orgInspNo", cardPacket.getString(20));
		map.put("encCardData", cardPacket.getString(300));
		return map;
	}
}
