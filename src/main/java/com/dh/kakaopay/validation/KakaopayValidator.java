package com.dh.kakaopay.validation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.dh.kakaopay.error.KakaopayException;

@Component
public class KakaopayValidator {
	
	public void validate(Errors errors) {
  	  if (errors.hasErrors()) {

    	  Map<String, String> validatorResult = new HashMap<>();

          for (FieldError error : errors.getFieldErrors()) {
              String validKeyName = String.format("valid_%s", error.getField());
              validatorResult.put(validKeyName, error.getDefaultMessage());
          }
          
          throw new KakaopayException(validatorResult.toString());

    }
	}

}
