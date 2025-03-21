package com.lms.models;


public class Student extends User {
	
	private int student_id;
	private String study_subject;	

	public Student(String fname, String lname, String email, String subject) {
		super(fname, lname, email);
		this.study_subject = subject;
	}

	public int getStudent_id() {
		return student_id;
	}

	public void setStudent_id(int student_id) {
		this.student_id = student_id;
	}

	public String getStudy_subject() {
		return study_subject;
	}

	public void setStudy_subject(String study_subject) {
		this.study_subject = study_subject;
	}
}
