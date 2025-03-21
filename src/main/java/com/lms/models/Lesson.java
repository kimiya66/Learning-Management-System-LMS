package com.lms.models;

public class Lesson {

	private int lesson_id;
	private String lesson_title;
	private String lesson_content;
	
	public Lesson(String title, String content) {
		this.lesson_title = title;
		this.lesson_content = content;
	}
	
	public int getLesson_id() {
		return lesson_id;
	}

	public void setLesson_id(int lesson_id) {
		this.lesson_id = lesson_id;
	}
	
	public String getLesson_title() {
		return lesson_title;
	}

	public void setLesson_title(String lesson_title) {
		this.lesson_title = lesson_title;
	}

	public String getLesson_content() {
		return lesson_content;
	}

	public void setLesson_content(String lesson_content) {
		this.lesson_content = lesson_content;
	}
}
