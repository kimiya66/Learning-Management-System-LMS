package com.lms.filterCourses;
import com.lms.models.*;

public interface CourseFilterStrategy {
	boolean filter(Course course);
}
