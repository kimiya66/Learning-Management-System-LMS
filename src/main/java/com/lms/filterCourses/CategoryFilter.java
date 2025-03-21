package com.lms.filterCourses;

import com.lms.models.Course;

public class CategoryFilter implements CourseFilterStrategy {
	private String category;
	
	public CategoryFilter(String category) {
		this.category = category;
	}

	@Override
	public boolean filter(Course course) {
		return course.getCategory().equals(category);
	}

	
}
