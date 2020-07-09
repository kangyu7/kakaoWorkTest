package com.dh.kakaopay.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dh.kakaopay.domain.CardData;
import com.dh.kakaopay.domain.CardInputData;
import com.dh.kakaopay.service.KakaopayService;

@RestController
public class KakaopayTestController {
	
	@Autowired
	KakaopayService KakaopayService;
	
    @GetMapping("/")
    public String index() {
        return "Welcome Kakaopay Test";
    }
    
    //결제요청
    @PostMapping("/pay")
    public CardData pay(@RequestBody CardInputData cardInputData) {
    	return KakaopayService.save(cardInputData);
    }
    
//    //결제취소
//    @PostMapping("/cancel")
//    public CardData cancel(@RequestBody CardInputData cardInputData) {
//    	return KakaopayService.cancel(cardInputData);
//    }
//    
    //데이터조회
    @GetMapping("/search")
    public Map<String, String> search(@RequestBody CardInputData cardInputData) {
    	return KakaopayService.find(cardInputData);
    }
	
}
