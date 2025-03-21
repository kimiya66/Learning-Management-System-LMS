package com.lms.filterCourses;

import com.lms.models.Course;

public class Difficulty_levelFilter implements CourseFilterStrategy {

	private String difficulty_level;
	
	public Difficulty_levelFilter(String difficulty_level) {
		this.difficulty_level = difficulty_level;
	}

	@Override
	public boolean filter(Course course) {
		return course.getDifficulty_level().equals(difficulty_level);
	}

}
