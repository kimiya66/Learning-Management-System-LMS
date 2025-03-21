package com.lms.filterCourses;

import com.lms.models.Course;

public class PriceFilter implements CourseFilterStrategy {

	private double max_price;
	
	public PriceFilter(double max_price) {
		this.max_price = max_price;
	}

	@Override
	public boolean filter(Course course) {
		return course.getPrice() <= max_price;
	}

}
