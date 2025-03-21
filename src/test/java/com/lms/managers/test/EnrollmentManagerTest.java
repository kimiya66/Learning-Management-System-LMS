package com.lms.managers.test;

import org.junit.jupiter.api.Test;
import com.lms.models.*;

public class EnrollmentManagerTest {
	
	@Test
	public void TestMarkLessonCompleted() {
		Student stu = new Student("kim", "be", "test@mail.com", "mathmatics");
		Course crs = new Course("sk_55", "dfs", "dgf", "dsf", "ds", 23.9);
		Enrollment enrollment = new Enrollment(stu, crs);
	}
}


