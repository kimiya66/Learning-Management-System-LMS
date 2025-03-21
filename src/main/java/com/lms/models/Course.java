package com.lms.models;

public class Course {
	private int course_id;
	private String course_serial, course_title, category, difficulty_level, course_description;
	private double price;
	
	public Course(String serial, String course_title, String course_description, String category, String difficulty_level, double price) {
		this.course_serial = serial; 
		this.course_title = course_title;
		this.course_description = course_description;
		this.category = category;
		this.difficulty_level = difficulty_level;
		this.price = price;
	}
	
	public String getCourse_serial() {
		return course_serial;
	}

	public void setCourse_serial(String course_serial) {
		this.course_serial = course_serial;
	}

	public int getCourse_id() {
		return course_id;
	}
	public void setCourse_id(int course_id) {
		this.course_id = course_id;
	}
	public String getCourse_title() {
		return course_title;
	}
	public void setCourse_title(String course_title) {
		this.course_title = course_title;
	}
	public String getCourse_description() {
		return course_description;
	}

	public void setCourse_description(String course_description) {
		this.course_description = course_description;
	}

	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDifficulty_level() {
		return difficulty_level;
	}
	public void setDifficulty_level(String difficulty_level) {
		this.difficulty_level = difficulty_level;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	@Override
	public String toString() {
		return getCourse_serial() + " | " +
	           getCourse_title() + " | " +
			   getCourse_description() + " | " +
	           getCategory() + " | " +
			   getDifficulty_level() + " | " +
	           getPrice();
	}
}
