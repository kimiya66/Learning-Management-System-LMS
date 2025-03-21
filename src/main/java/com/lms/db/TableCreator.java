package com.lms.db;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.exceptions.LMSException;

public class TableCreator {
	
	private static Logger logger = Logger.getLogger(TableCreator.class.getName());
	
	public static void createTables() throws LMSException {
		try (Connection conn = DatabaseConnection.getConnection();
				Statement stmt = conn.createStatement()) {
			// user table
			String userTable = "CREATE TABLE IF NOT EXISTS User("
					+ "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "first_name VARCHAR(100) NOT NULL, "
					+ "last_name VARCHAR(100) NOT NULL, "
					+ "user_email VARCHAR(200) UNIQUE NOT NULL)";
			
			// student table
			String studentTable = "CREATE TABLE IF NOT EXISTS Student("
					+ "student_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "user_id INTEGER NOT NULL, "
					+ "study_subject TEXT NOT NULL, "
					+ "FOREIGN KEY (user_id) REFERENCES User(user_id) ON DELETE CASCADE)";
			
			// course table
			String courseTable = "CREATE TABLE IF NOT EXISTS Course("
					+ "course_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "serial_number TEXT UNIQUE NOT NULL, "
					+ "course_title VARCHAR(100) NOT NULL, "
					+ "course_description TEXT, "
					+ "category VARCHAR(100), "
					+ "difficulty_level VARCHAR(100), "
					+ "price REAL NOT NULL)";
			
			// lesson table
			String lessonTable = "CREATE TABLE IF NOT EXISTS Lesson("
					+ "lesson_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "course_id INTEGER NOT NULL, "
					+ "lesson_title TEXT NOT NULL, "
					+ "lesson_content TEXT, "
					+ "FOREIGN KEY (course_id) REFERENCES Course(course_id) ON DELETE CASCADE)";
			
			// enrollment table // rating should be floating value
			String entollmentTable = "CREATE TABLE IF NOT EXISTS Enrollment("
					+ "enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "enrollment_dateTime TEXT NOT NULL, "
					+ "student_id INTEGER NOT NULL, "
					+ "course_id INTEGER NOT NULL, "
					+ "rating DOUBLE CHECK (rating BETWEEN 1 AND 5), " 
					+ "FOREIGN KEY (student_id) REFERENCES Student(student_id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (course_id) REFERENCES Course(course_id) ON DELETE CASCADE, "
					+ "UNIQUE(student_id, course_id))"; // -- Prevents duplicate enrollments
			
			// create completedLesson table
			String completedLessonsTable = "CREATE TABLE IF NOT EXISTS CompletedLessons("
					+ "enrollment_id INTEGER NOT NULL, "
					+ "lesson_id INTEGER NOT NULL, "
					+ "PRIMARY KEY (enrollment_id, lesson_id), "
					+ "FOREIGN KEY (enrollment_id) REFERENCES Enrollment(enrollment_id) ON DELETE CASCADE, "
					+ "FOREIGN KEY (lesson_id) REFERENCES Lesson(lesson_id) ON DELETE CASCADE)";
			
			stmt.execute(userTable);
			stmt.execute(studentTable);
			stmt.execute(courseTable);
			stmt.execute(lessonTable);
			stmt.execute(entollmentTable);
			stmt.execute(completedLessonsTable);
		
			
			logger.log(Level.FINE, "tables created successfully!");
			System.out.println("tables created!");
			
		} catch (SQLException e) {
			System.out.println("tables could not be created." + e.getMessage());
		}
	}
}
