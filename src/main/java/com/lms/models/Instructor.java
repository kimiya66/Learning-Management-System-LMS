package com.lms.models;

public class Instructor extends User {
	
	private String field;
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	
	public Instructor(String fname, String lname, String email, String field) {
		super(fname, lname, email);
		this.field = field;
	}

}
