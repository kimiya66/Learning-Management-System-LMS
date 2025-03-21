package com.lms.managers.test;

import org.junit.jupiter.api.Test;

import com.lms.managers.UserManager;
import com.lms.models.Student;
import com.lms.models.User;

public class UserManagerTest {

	@Test
	public void test() {
		UserManager testUserManager = UserManager.getInstance();
		User user = new Student("kim", "bw", "k@g.com", "Math");
	}
}
