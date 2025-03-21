package com.lms.managers;

import java.util.logging.*;

import com.lms.db.DatabaseConnection;
import com.lms.exceptions.LMSException;
import com.lms.models.Student;
import com.lms.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
	
	private static Connection connection = null;
	
	// implement looging for user registeration
	private static final Logger logger = Logger.getLogger(UserManager.class.getName());
	
	// define class as Singleton
	//-- the single instance of userManager
	private static UserManager instance;
		
	// -- define constructor as private for singleton pattern
	private UserManager() {
		
		try {
			connection = DatabaseConnection.getConnection();
		} catch (LMSException e) {
			e.printStackTrace();
		}
	}
	
	//-- public method to get the instance of userManager --
	public static UserManager getInstance() {
		if (instance == null) {
			instance = new UserManager();
		}
		return instance;
	}
	
	// --- register a student ---
	public void addStudent(Student stu) {
			// in order to add the user as student, it should first entered as a user
			String userQuery = "INSERT INTO User(first_name, last_name, user_email) VALUES(?, ?, ?)";
			String studentQuery = "INSERT INTO Student(user_id, study_subject) VALUES(?, ?)";
			
			
			try {
				  
				if (connection == null || connection.isClosed()) {
				        throw new SQLException("Connection is null or already closed.");
				}
				
				// start transaction (turn off auto-save) --- because there are 2 inserts which connected together
				connection.setAutoCommit(false);
				
				// insert user
				try (PreparedStatement userPstmt = connection.prepareStatement(userQuery)) {
					userPstmt.setString(1, stu.getFirst_name());
					userPstmt.setString(2, stu.getLast_name());
					userPstmt.setString(3, stu.getEmail_address());
					
					userPstmt.executeUpdate();
				} 
				
				// retrieve generated user_id
				int user_id = getUserByEmail(stu.getEmail_address());
				
				 // insert into student table
				try (PreparedStatement studentPstmt = connection.prepareStatement(studentQuery)) {
					studentPstmt.setInt(1, user_id);
					studentPstmt.setString(2, stu.getStudy_subject());
					
					studentPstmt.executeUpdate();
				} 
				 // if both inserts succeed, save them
				 connection.commit();
				 System.out.println("Student registered successfully!");
				 
			} catch (SQLException e) {
				
				try {
					if (connection != null) {
						connection.rollback(); // if any step fails, undo everything
						System.out.println("Transaction rolled back because of an error.");
					}
					
				} catch (SQLException rollbackEx) {
					rollbackEx.printStackTrace();
				}
				e.printStackTrace();
			} finally {
				try {
					// reset auto-commit to normal
					  if (connection != null) {
			                connection.setAutoCommit(true);
			            }
 				} catch (SQLException commitEx) {
					commitEx.printStackTrace();
				}
		}
		
	}
	
	// --- get user_id based on email address ---
	public static int getUserByEmail(String email) {
		String query = "SELECT * FROM User WHERE user_email = ?";
		try {
			if (connection == null || connection.isClosed()) {
		        throw new SQLException("Connection is null or already closed.");
		    }
			
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return rs.getInt("user_id");
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	
	// --- delete a student based on his email --- 
	public void removeStudent(String student_email) {
		String query = "DELETE FROM Student "
				+ "WHERE user_id = ? ";
		
		try {
			if (connection == null || connection.isClosed()) {
			    throw new SQLException("Connection is null or already closed.");
			}
			
			// retrieve user_id
			int user_id = getUserByEmail(student_email);
			try (PreparedStatement pstmt = connection.prepareStatement(query)) {
				pstmt.setInt(1, user_id);
				
				int affectedRows = pstmt.executeUpdate();
				
				if (affectedRows > 0) {
					System.out.println("Student record deleted successfully!");
				} else {
					System.out.println("no student with the email, {0} found." + student_email);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	// --- update student ---
	public void updateStudent(String email, String firstName, String lastName, String study_subject) {
		 try {
		        if (connection == null || connection.isClosed()) {
		            throw new SQLException("Connection is null or already closed.");
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return;  // Stop the method execution if the connection is invalid
		    }
		if (email.isEmpty()) {
			System.out.println("please enter the email address.");
			return;
		}
		int user_id = getUserByEmail(email);
		if (user_id == -1) {
		        System.out.println("No user found with the given email.");
		        return;  // Stop further execution if the user does not exist
		 }
		
		StringBuilder updateUserQuery = new StringBuilder("UPDATE User SET ");
		StringBuilder updateStudentQuery = new StringBuilder("UPDATE Student SET ");
		
		boolean isUserUpdate = false;
		
		if (!firstName.isEmpty()) {
			updateUserQuery.append("first_name = ?, ");
			isUserUpdate = true;
			
		}
		if (!lastName.isEmpty()) {
			updateUserQuery.append("last_name = ?, ");
			isUserUpdate = true;
		}
		
		// update user table if necessary
		if (isUserUpdate) {
			// set the query for user_table update
			updateUserQuery.setLength(updateUserQuery.length() - 2); // delete the last comma and space
			updateUserQuery.append(" WHERE user_email = ?");
			
			try (PreparedStatement userPstmt = connection.prepareStatement(updateUserQuery.toString())) {
				int index = 1;
				if (!firstName.isEmpty()) userPstmt.setString(index++, firstName);
				if (!lastName.isEmpty()) userPstmt.setString(index++, lastName);
				userPstmt.setString(index++, email);
				
				int rowsAffected = userPstmt.executeUpdate();
				if (rowsAffected <= 0) {
					logger.log(Level.WARNING, "no rows affected.");
				}
			} catch (SQLException e) {
				 logger.log(Level.SEVERE, "Error updating User table", e);
			}
			
		}
		
		// update student table
		if (!study_subject.isEmpty()) {
			// set the query to update student_table
			updateStudentQuery.append("study_subject = ? WHERE user_id = ?");
			try (PreparedStatement studentPstmt = connection.prepareStatement(updateStudentQuery.toString())){
				studentPstmt.setString(1, study_subject);
				studentPstmt.setInt(2, user_id);
				
				int rowsAffected = studentPstmt.executeUpdate();
				if (rowsAffected > 0) {
					System.out.println("Information updated successfully!");
				} else {
					logger.log(Level.WARNING, "no rows affected.");
				}
				
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Error updating Student table", e);
			}
		}
		
	}
	
	// --- display all students ---
	public void getAllStudents() {
		
		String query = "SELECT User.user_id, User.first_name, User.last_name, User.user_email, Student.student_id, Student.study_subject "
				+ "From Student "
				+ "JOIN User ON Student.user_id = User.user_id";
		
		try {
			if (connection == null || connection.isClosed()) {
		        throw new SQLException("Connection is null or already closed.");
			}
			
			PreparedStatement pstmt = connection.prepareStatement(query);
			
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				System.out.println("user_id " + rs.getInt("user_id") + 
						", First name: " + rs.getString("first_name") +
						", Last name: " + rs.getString("last_name") + 
						", email_address: " + rs.getString("user_email") + 
						", student_id: " + rs.getInt("student_id") +
						", study subject: " + rs.getString("study_subject"));
				System.out.println("------");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
