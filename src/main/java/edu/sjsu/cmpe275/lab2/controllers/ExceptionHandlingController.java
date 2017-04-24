package edu.sjsu.cmpe275.lab2.controllers;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

@RestController
public class ExceptionHandlingController<E> {
	
	@RequestMapping("/applicationError")
	@ResponseBody
	public  ResponseEntity<E> returnError(@RequestParam(value="code", defaultValue="400") String code,@RequestParam(value="msg", defaultValue="msg") String msg, HttpServletResponse response) throws JSONException{
		
		JSONObject outputJsonObj = new JSONObject();
		JSONObject tempObj = new JSONObject();
		
		tempObj.put("code", code);
		tempObj.put("msg", msg);
		
		if(code.startsWith("2")){
			outputJsonObj.put("Response", tempObj);
		}else{
			outputJsonObj.put("BadRequest", tempObj);
		}
		//response.setStatus(Integer.parseInt(code));
		
		//return outputJsonObj.toMap();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(outputJsonObj.toString());
		String prettyJsonString = gson.toJson(je);
		
		return new ResponseEntity(prettyJsonString,HttpStatus.OK);
	}
	
	@RequestMapping("/applicationErrorInXML")
	@ResponseBody
	public  ResponseEntity<E> returnErrorInXML(@RequestParam(value="code", defaultValue="400") String code,@RequestParam(value="msg", defaultValue="msg") String msg, HttpServletResponse response) throws JSONException, DocumentException, IOException{
		
		JSONObject outputJsonObj = new JSONObject();
		JSONObject tempObj = new JSONObject();
		
		tempObj.put("code", code);
		tempObj.put("msg", msg);
		
		if(code.startsWith("2")){
			outputJsonObj.put("Response", tempObj);
		}else{
			outputJsonObj.put("BadRequest", tempObj);
		}
		
		String xml = XML.toString(outputJsonObj);
		
		//converting XML to pretty print format
		Document document = DocumentHelper.parseText(xml);  
        StringWriter stringWriter = new StringWriter();  
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();  
        outputFormat.setIndent(true);
        outputFormat.setIndentSize(3); 
        outputFormat.setSuppressDeclaration(true);
        outputFormat.setNewLineAfterDeclaration(false);
        XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);  
        xmlWriter.write(document);  
        
		return (ResponseEntity<E>) new ResponseEntity<String>(stringWriter.toString(),HttpStatus.valueOf(Integer.parseInt(code)));
	}
}
