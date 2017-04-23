package edu.sjsu.cmpe275.lab2.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionHandlingController {
	
	@RequestMapping("/applicationError")
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public  Map<String, Object> returnError(@RequestParam(value="code", defaultValue="400") String code,@RequestParam(value="msg", defaultValue="msg") String msg, HttpServletResponse httpServletResponse) throws JSONException{
		
		JSONObject outputJsonObj = new JSONObject();
		JSONObject tempObj = new JSONObject();
		
		tempObj.put("code", code);
		tempObj.put("msg", msg);
		
		outputJsonObj.put("BadRequest", tempObj);
		return outputJsonObj.toMap();
	}
}
