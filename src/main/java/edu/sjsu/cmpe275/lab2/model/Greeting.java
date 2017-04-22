package edu.sjsu.cmpe275.lab2.model;

import com.fasterxml.jackson.annotation.JsonView;


public class Greeting {
	
	private final long id;
	
    private final String content;
	
	

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
