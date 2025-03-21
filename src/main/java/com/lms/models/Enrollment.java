package com.lms.models;


// -- this class is a connection between courses and students --
public class Enrollment {
	private int enrollment_id;
	private Student student;
	private Course course;
	
	public Enrollment(Student stu, Course crs) {
		this.student = stu;
		this.course = crs;
	}
	
	public int getEnrollment_id() {
		return enrollment_id;
	}
	public void setEnrollment_id(int enrollment_id) {
		this.enrollment_id = enrollment_id;
	}
	
	public Student getStudent() {
		return student;
	}
	
	public Course getCourse() {
		return course;
	}
}
