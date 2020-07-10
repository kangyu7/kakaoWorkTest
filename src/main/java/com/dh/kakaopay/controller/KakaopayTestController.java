package com.dh.kakaopay.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dh.kakaopay.domain.CardCancelInputDto;
import com.dh.kakaopay.domain.CardData;
import com.dh.kakaopay.domain.CardInputDto;
import com.dh.kakaopay.domain.CardSearchDto;
import com.dh.kakaopay.service.KakaopayService;
import com.dh.kakaopay.validation.KakaopayValidator;

@RestController
public class KakaopayTestController {
	
	@Autowired
	KakaopayService KakaopayService;
	
	@Autowired
	KakaopayValidator kakaopayValidator;
	
    @GetMapping("/")
    public String index() {
        return "Welcome Kakaopay Test";
    }
    
    //결제요청
    @PostMapping("/pay")
    public Map<String, String> pay(@Valid @RequestBody CardInputDto cardInputData, Errors errors) {

    	kakaopayValidator.validate(errors);
    	
    	return KakaopayService.save(cardInputData);
    }
    
    //결제취소
    @PutMapping("/cancel")
    public Map<String, String> cancel(@Valid @RequestBody CardCancelInputDto cardInputData, Errors errors) {

    	kakaopayValidator.validate(errors);
    	
    	return KakaopayService.cancel(cardInputData);
    }
//    
    //데이터조회
    @GetMapping("/search")
    public Map<String, String> search(@Valid @RequestBody CardSearchDto cardSearchDto, Errors errors) {
		
    	kakaopayValidator.validate(errors);
    	
    	return KakaopayService.find(cardSearchDto);
    }
	
}
