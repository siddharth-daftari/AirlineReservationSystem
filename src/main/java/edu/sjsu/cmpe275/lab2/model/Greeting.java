package edu.sjsu.cmpe275.lab2.model;

import com.fasterxml.jackson.annotation.JsonView;

import edu.sjsu.cmpe275.jsonViews.GreetingsView;

public class Greeting {
	
	@JsonView(GreetingsView.View1.class)
	private final long id;
	
	@JsonView(GreetingsView.View2.class)
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
