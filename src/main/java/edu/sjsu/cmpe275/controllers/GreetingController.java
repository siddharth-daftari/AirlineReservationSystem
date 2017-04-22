package edu.sjsu.cmpe275.controllers;

import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import edu.sjsu.cmpe275.jsonViews.GreetingsView;
import edu.sjsu.cmpe275.lab2.Greeting;

@RestController
public class GreetingController {
	
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	
	@Autowired
    ObjectMapper mapper;
	
    @RequestMapping("/greeting")
    public @ResponseBody String greeting(@RequestParam(value="name", defaultValue="World") String name) {
    	boolean forInternal = false;
    	Greeting greeting = new Greeting(1,"myName");
    	
        ObjectWriter viewWriter;
        if (forInternal) {
            viewWriter = mapper.writerWithView(GreetingsView.View1.class);
        } else {
            viewWriter = mapper.writerWithView(GreetingsView.View2.class);
        }

        try {
			return viewWriter.writeValueAsString(greeting);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "";
		}
    }
    
}
