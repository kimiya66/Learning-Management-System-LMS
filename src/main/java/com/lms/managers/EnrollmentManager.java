package com.lms.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.db.DatabaseConnection;
import com.lms.exceptions.LMSException;
import com.lms.models.Course;
import com.lms.models.Enrollment;
import com.lms.models.Student;

public class EnrollmentManager {
	
	private static Connection connection = null;
	
	private static final Logger logger = Logger.getLogger(EnrollmentManager.class.getName());
	
	private static EnrollmentManager enrollmentInstance;
	
	// change list to set in order to have unique values
	private Set<Enrollment> enrollmentList;
	
	private EnrollmentManager() {
		enrollmentList = new HashSet<>();
		
		try {
			connection = DatabaseConnection.getConnection();
		} catch (LMSException e) {
			e.printStackTrace();
		}
	}
	
	public static EnrollmentManager getInstance() {
		if (enrollmentInstance == null) {
			enrollmentInstance = new EnrollmentManager();
		}
		return enrollmentInstance;
	}
	
	// --- create enrollment ---
	public void enrollStudentInCourse(int studentId, int courseId) {
		
		if (studentId != 0 && courseId != 0) {
			
			String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			
			String enrollmentQuery = "INSERT INTO Enrollment(enrollment_dateTime, student_id, course_id) VALUES(?, ?, ?)";
			
			try {
		        if (connection == null || connection.isClosed()) {
		            throw new SQLException("Connection is null or already closed.");
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return;  // Stop the method execution if the connection is invalid
		    }
			
			try {
				PreparedStatement pstmt = connection.prepareStatement(enrollmentQuery);
				pstmt.setString(1, currentDateTime);
				pstmt.setInt(2, studentId);
				pstmt.setInt(3, courseId);
				
				int affectedRows = pstmt.executeUpdate();
				if (affectedRows == 0) {
					throw new SQLException("Enrollment failed. No rows affected.");
				} else {
					logger.log(Level.INFO, "Student with id {0}, enrolled to the course with id {1} successfully!", 
							new Object[]{studentId, courseId});
				}
			} catch (SQLException e) {
				System.out.println("Error enrolling student: " + e.getMessage());
			}
			
			
		} else {
			logger.log(Level.WARNING, "Course or Student information is not valid");
		}
		
	}
	
	// display all enrollments
	public void displayEnrollments() {
		String query = "SELECT * FROM Enrollment";
		try {
			if (connection == null || connection.isClosed()) {
		        throw new SQLException("Connection is null or already closed.");
			}
			
			PreparedStatement pstmt = connection.prepareStatement(query);
			
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				System.out.println("enrollment_id " + rs.getInt("enrollment_id") + 
						", enrollment_dateTime: " + rs.getString("enrollment_dateTime") +
						", student_id: " + rs.getString("student_id") + 
						", course_id: " + rs.getString("course_id") + 
						", rating: " + rs.getInt("rating") + "*");
				System.out.println("------");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// --- display enrolled courses by a student ---
	public void getEnrolledCoursesByStudent(int student_id) {	
		String query = "SELECT e.enrollment_id, c.course_id, c.serial_number, c.course_title, c.course_description, e.enrollment_dateTime "
				+ "FROM Enrollment e "
				+ "JOIN Course c ON e.course_id = c.course_id "
				+ "WHERE e.student_id = ?";
		
		try {
	        if (connection == null || connection.isClosed()) {
	            throw new SQLException("Connection is null or already closed.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return;  // Stop the method execution if the connection is invalid
	    }
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setInt(1, student_id);
			ResultSet rs = pstmt.executeQuery();
			
			System.out.println("Courses enrolled by student ID " + student_id + ":");
		
			while (rs.next()) {
				System.out.println("EnrollmentID " + rs.getInt("enrollment_id") + 
						" | course_id: " + rs.getInt("course_id") +
						" | course_serial: " + rs.getString("serial_number") +
						" | title: " + rs.getString("course_title") +
						" | description: " + rs.getString("course_description") + 
						" | enrollment date: " + rs.getString("enrollment_dateTime"));
			}
			
		} catch (SQLException e) {
			System.out.println("Error fetching courses for student: " + e.getMessage());
		}
	}
	
	// --- display enrolled students by a course ---
	public void getEnrolledStudentsByCourse(int courseId) {	
		String query = "SELECT e.enrollment_id, s.student_id, u.first_name, u.last_name, s.study_subject, e.enrollment_dateTime "
				+ "FROM Enrollment e "
				+ "JOIN Student s ON s.student_id = e.student_id "
				+ "JOIN User u ON s.user_id = u.user_id "
				+ "WHERE e.course_id = ?";
		
		try {
	        if (connection == null || connection.isClosed()) {
	            throw new SQLException("Connection is null or already closed.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return;  // Stop the method execution if the connection is invalid
	    }
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setInt(1, courseId);
			ResultSet rs = pstmt.executeQuery();
			
			System.out.println("Students enrolled to the course by id: " + courseId + ":");
		
			while (rs.next()) {
				System.out.println("EnrollmentID " + rs.getInt("enrollment_id") +
						" | studentID: " + rs.getInt("student_id") +
						" | first name: " + rs.getString("first_name") +
						" | last name: " + rs.getString("last_name") + 
						" | study subject: " + rs.getString("study_subject") + 
						" | enrollment date: " + rs.getString("enrollment_dateTime"));
			}
			
		} catch (SQLException e) {
			System.out.println("Error fetching students for a course: " + e.getMessage());
		}
	}
	// --- delete an enrollment ---
	public void deleteEnrollmentById(int enrollmentId) {
	    String sql = "DELETE FROM Enrollment WHERE enrollment_id = ?";
	    
	    try {
	        if (connection == null || connection.isClosed()) {
	            throw new SQLException("Connection is null or already closed.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return;  // Stop the method execution if the connection is invalid
	    }

	    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
	        pstmt.setInt(1, enrollmentId);
	        int affectedRows = pstmt.executeUpdate();
	        
	        if (affectedRows > 0) {
	           logger.log(Level.WARNING, "Enrollment id {0} deleted successfully!", enrollmentId);
	        } else {
	            System.out.println("No enrollment found with the given ID.");
	        }
	    } catch (SQLException e) {
	        System.out.println("Error deleting enrollment: " + e.getMessage());
	    }
	}
	
	// ------------------------------------ rating process ------------------------------------------------------------------
	// --- record a rating for a course ---
		public void updateCourseRating(int course_id, int student_id, double rate) {
			String query = "UPDATE Enrollment SET rating = ? WHERE course_id = ? AND student_id = ?";
			
			try {
		        if (connection == null || connection.isClosed()) {
		            throw new SQLException("Connection is null or already closed.");
		        }
		
			} catch (SQLException e) {
		        e.printStackTrace();
		        return;  // Stop the method execution if the connection is invalid
		    }
			
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setDouble(1, rate);
				pstmt.setInt(2, course_id);
				pstmt.setInt(3, student_id);
				
				int rowsUpdated = pstmt.executeUpdate();
		        if (rowsUpdated > 0) {
		            System.out.println("Rating updated successfully!");
		        } else {
		            System.out.println("Failed to update rating.");
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	// --- get average rating of a course ---
	public double getCourseAverageRating(int coure_id) throws SQLException {
		String getRating = "SELECT AVG(rating) AS avg_rating FROM Enrollment WHERE course_id = ? AND rating IS NOT NULL";
		
		if (connection == null || connection.isClosed()) {
			throw new SQLException("connection to database is not established.");
		}
		
		try (PreparedStatement pstmt = connection.prepareStatement(getRating)) {
			pstmt.setInt(1, coure_id);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getDouble("avg_rating");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0.0; // default if no rating exist
		
	}
	// --- display course rating ---
		public void displayCourseAvgRating() throws SQLException {
			String allCourses = "SELECT course_id, course_title From Course";
			
			if (connection == null || connection.isClosed()) {
				throw new SQLException("connection to database is not established.");
			}
			
			try (Statement stmt = connection.createStatement()) {
				ResultSet rs = stmt.executeQuery(allCourses);
				
				if (!rs.next()) {
					logger.log(Level.WARNING, "there is no course in db.");
					return;
				}
				
				while(rs.next()) {
					int id = rs.getInt("course_id");
					String title = rs.getString("course_title");
					double avg_rating = getCourseAverageRating(id);
					
					System.out.printf("Course: %s | Average Rating: %.2f ⭐\n", title, avg_rating);
				}
				
			} catch (SQLException e) {
				// TODO: handle exception
			}
			
		}
		
	
	// --- mark lesson as completed --- better to store in student table as a list in hibernste....
	public void markLessonCompleted(int enrollment_id, int lesson_id) throws SQLException, LMSException {
		// -- we use here insert or ignore to ignore adding repeated completed lessons in this enrollment --
		String query = "INSERT OR IGNORE INTO CompletedLessons(enrollment_id, lesson_id) VALUES(?, ?)";
		
		if (connection == null || connection.isClosed()) {
			throw new SQLException("connection is null or already closed.");
		}
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setInt(1, enrollment_id);
			pstmt.setInt(2, lesson_id);
			
			int affectedrows = pstmt.executeUpdate();
			if (affectedrows == 0) {
				System.out.println("This lesson already marked as completed. No rows affected.");
				return;
			} else {
				logger.log(Level.INFO, "Lesson marked as completed successfully!");
			}
			
		} catch (Exception e) {
			throw new LMSException("data base error on adding lesson as completed. ", e);
		}
	}
	
	// -- get/calculate student progress --
	public double getStudentProgress(int enrollment_id, int course_id) throws LMSException, SQLException {
		String completedLessonQuery  = "SELECT COUNT(*) FROM CompletedLessons WHERE enrollment_id = ?";
		int completed = 0;
		int total = 0;
		
		if (connection == null || connection.isClosed()) {
			throw new SQLException("No connection to db.");
		}
		
		try (PreparedStatement pstmt = connection.prepareStatement(completedLessonQuery)) {
			pstmt.setInt(1, enrollment_id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) 
				completed = rs.getInt(1);
			
		} catch (SQLException e) {
			throw new LMSException("Problem to catch completed lessons.", e);
		}
		
		
		String totalLessons = "SELECT COUNT(*) AS totalCourses FROM Lesson WHERE course_id = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(totalLessons)) {
			pstmt.setInt(1, course_id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				total = rs.getInt("totalCourses");
			}
			
		} catch (SQLException e) {
			throw new LMSException("Problem to catch the total lessons for this course.", e);
		}
		
		if (total > 0) {
	        double progress = ((double) completed / total) * 100;
	        // Debugging: Print the progress before returning
	        System.out.println("Calculated progress: " + progress + "%");
	        return progress;
	    } else {
	        // In case there are no lessons, return 0% progress
	        return 0.0;
	    }
		
	}
	
	
	
	// --- list of all enrolled courses for a student ---
	public void removeStudentFromCourse(Student st, Course course) {
		enrollmentList.removeIf(enrollment -> 
							enrollment.getStudent().equals(st) && enrollment.getCourse().equals(course));
		logger.log(Level.WARNING, "Student {0}, {1} removed from the course {3}",
				new Object[] {st.getFirst_name(), st.getLast_name(), course.getCourse_title()});
	}
		public void displayEnrolledCourses(Student student) {
			List<Course> courseList = new ArrayList<>();
			
			for (Enrollment en : enrollmentList) {
				if (en.getStudent().equals(student)) {
					courseList.add(en.getCourse());
				}
			}
			
			courseList.forEach(System.out::println);
		}
		
		// --- list of all enrolled students for a course ---
		public void displayEnrolledStudents(Course course) {
			List<Student> studentList = new ArrayList<>();
			
			for (Enrollment en : enrollmentList) {
				if (en.getCourse().equals(course)) {
					studentList.add(en.getStudent());
				}
			}
			studentList.forEach(System.out::println);
		}
}
