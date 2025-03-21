package com.lms.managers;

import java.util.ArrayList;
import java.util.List;

import com.lms.models.Course;

public class CourseCache implements CourseObserver {
	
	private static CourseCache instance;
	private List<Course> courseList;
	
    private final CourseManager courseManager;
	
	private CourseCache(CourseManager courseManager) {
		 this.courseManager = courseManager;
		courseList = new ArrayList<Course>();
	}
	
	public static synchronized CourseCache getInstance(CourseManager courseManager) {
		if (instance == null) {
			instance = new CourseCache(courseManager);
		}
		return instance;
	}
	
	private void loadCoursesFromDatabase() {
        System.out.println("Loading courses into cache...");
        CourseManager course_manager = CourseManager.getInstance();
		List<Course> updatedCourses = course_manager.getAllCourses();
		setCourses(updatedCourses);
    }
	
	public List<Course> getCourses() {
		return new ArrayList<>(courseList); // ***this will send a copy of the list instead of the list itself to prevent changing the cache directly***
	}
	
	public void setCourses(List<Course> updatedCourses) {
		this.courseList = new ArrayList<>(updatedCourses);
	}
	
	public void addCourse(Course course) {
		courseList.add(course);
	}
	
	public void removeCourse(Course course) {
		courseList.remove(course);
	}
	

	@Override
	public void updateCourses() {
		System.out.println("updating the cache...");
		
		// clear the cache before refreshing
		this.courseList.clear();
		loadCoursesFromDatabase(); // Force reload when an update happens
		
	}
	
}
