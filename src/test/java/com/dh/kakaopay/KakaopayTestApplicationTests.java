package com.dh.kakaopay;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class KakaopayTestApplicationTests {
	
	@Autowired
    MockMvc mockMvc;

	//Test case 1
    @Test
    public void TestCase1() throws Exception {
    	
    	JSONObject obj = new JSONObject();
    	
    	obj.put("cardNo", "5388321411594220");
    	obj.put("exprYm", "0423");
    	obj.put("cvc", "536");
    	obj.put("loanYmd", 0);
    	obj.put("payAmt", 20000);
    	obj.put("vatAmt", 909);
    	
    	MvcResult result = mockMvc.perform(post(("/pay")).content(obj.toString())
                .contentType(MediaType.APPLICATION_JSON)
        		).andExpect(status().isOk()).andReturn();
    	
    	String content = result.getResponse().getContentAsString();
    	System.out.println(content);
    }
    

}
