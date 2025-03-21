package com.lms.managers.test;

import org.junit.jupiter.api.Test;

import com.lms.exceptions.LMSException;
import com.lms.managers.CourseManager;
import com.lms.managers.UserManager;
import com.lms.models.Course;

public class CourseManagerTest {
	
	@Test
	public void addAndGetCourseTest() throws LMSException {
		CourseManager testCourseManager = CourseManager.getInstance();
		Course testCourse = new Course("ik_2223_34", "java", "new course", "programming", "A3", 32.24);
		testCourseManager.addCourse(testCourse);
		testCourseManager.getCourseIdBySerial("ik_2223_34");
	}
}
