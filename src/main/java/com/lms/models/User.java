package com.lms.models;

public abstract class User {
	private int user_id;
	private String first_name, last_name, email_address;
	
	
	public User(String first_name, String last_name, String email_address) {
		this.first_name = first_name;
		this.last_name = last_name;
		this.email_address = email_address;
	}
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getEmail_address() {
		return email_address;
	}
	public void setEmail_address(String email_address) {
		this.email_address = email_address;
	}

}
