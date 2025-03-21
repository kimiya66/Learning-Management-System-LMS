package com.lms.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

import com.lms.db.DatabaseConnection;
import com.lms.exceptions.LMSException;
import com.lms.filterCourses.CourseFilterStrategy;
import com.lms.models.Course;
import com.lms.models.Lesson;

public class CourseManager {
	
	private static Connection connection = null;
	private static final Logger logger = Logger.getLogger(CourseManager.class.getName());

	private static CourseManager courseManager_instance;
	
	// --- define observers to apply observer pattern
	private List<CourseObserver> observers = new ArrayList<>();
	
	private CourseCache cache; // Cache is assigned later
	
	
	private CourseManager() {
		try {
			connection = DatabaseConnection.getConnection();
		} catch (LMSException e) {
			e.printStackTrace();
		}
		
		//observers.add(CourseCache.getInstance()); // Add cache as an observer
	}
	   public void setCache(CourseCache cache) {
	        this.cache = cache;
	        addObserver(cache); // Register cache as an observer after initializing it
	    }
	
	public void addObserver(CourseObserver observer) {
		observers.add(observer);
	}
	public void notifyObservers() {
		for (CourseObserver observer: observers) {
			observer.updateCourses();
		}
	}
	
	public static CourseManager getInstance() {
		if (courseManager_instance == null) {
			courseManager_instance = new CourseManager();
		}
		return courseManager_instance;
	}
	
	
	// insert new course to database
		public void addCourse(Course newCourse) {
			if (newCourse != null) {
				
				String courseQuery = "INSERT INTO Course(serial_number, course_title, course_description, category, difficulty_level, price) VALUES(?, ?, ?, ?, ?, ?)";
				
				try {
					if (connection == null || connection.isClosed()) {
						throw new SQLException("connection is null or already closed.");
					}
					PreparedStatement pstmt = connection.prepareStatement(courseQuery);
					pstmt.setString(1, newCourse.getCourse_serial()); 
					pstmt.setString(2, newCourse.getCourse_title());
					pstmt.setString(3, newCourse.getCourse_description());
					pstmt.setString(4, newCourse.getCategory());
					pstmt.setString(5, newCourse.getDifficulty_level());
					pstmt.setDouble(6, newCourse.getPrice());
					
					int affectedRows = pstmt.executeUpdate();
					if (affectedRows > 0) {
						logger.log(Level.INFO, "Course {0} added successfully!", newCourse.getCourse_serial());
						notifyObservers();
						
					} else {
						throw new SQLException("Creating course failed. No rows affected.");
					}
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
			   
			   
			} else {
				logger.log(Level.WARNING, "Enter the correct information to add the course");
			}
		}
		

		// get all courses
		public List<Course> getAllCourses() {
			//CourseCache cache = CourseCache.getInstance();
			
			if (!cache.getCourses().isEmpty()) {
				return cache.getCourses(); // return cached courses 
			}
			
			List<Course> availableCourses = new ArrayList<>();
			String query = "SELECT * FROM Course";
			
			try {
				if (connection == null || connection.isClosed()) {
			        throw new SQLException("Connection is null or already closed.");
				}
				
				PreparedStatement pstmt = connection.prepareStatement(query);
				
				ResultSet rs = pstmt.executeQuery();
				
				System.out.println("List of courses: ");
				System.out.println("SerialNo, title, description, category, difficulty_level, price");
				System.out.println("----------------------------------------------------------------");
				while (rs.next()) {
					String course_serial = rs.getString("serial_number");
					String title = rs.getString("course_title");
					String desc = rs.getString("course_description");
					String category = rs.getString("category");
					String difficulty_level = rs.getString("difficulty_level");
					double price = rs.getDouble("price");
					Course course = new Course(course_serial, title, desc, category, difficulty_level, price);
					
					availableCourses.add(course);
					
					cache.setCourses(availableCourses); // store courses in cache
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return availableCourses;
		}
	
	// remove course from database
   	public void removeCourse(String serial, String title) {
   		String query = "DELETE FROM Course WHERE serial_number = ? AND course_title = ?";
   		
   		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, serial);
			pstmt.setString(2, title);
		    int rowsAffected = pstmt.executeUpdate();
		    if (rowsAffected >  0) {
		    	logger.log(Level.WARNING, "Course removed: {0}, {1}",
						new Object[] {serial, title});
		    	notifyObservers();
		    } else {
		    	logger.log(Level.WARNING, "no course with these information exists.");
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
   	
   	// --- update a course ---
   	public void updateCourse(String serial_number, String course_title, String course_description, String category, String level, double price) {
		 try {
		        if (connection == null || connection.isClosed()) {
		            throw new SQLException("Connection is null or already closed.");
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return;  // Stop the method execution if the connection is invalid
		    }
		if (serial_number.isEmpty()) {
			System.out.println("please enter the correct serial number.");
			return;
		}
		
		String updateCourse = "UPDATE Course SET "
				+ "course_title = ?, course_description = ?, category = ?, difficulty_level = ?, price = ?"
				+ "WHERE serial_number = ?";
	
			try (PreparedStatement pstmt = connection.prepareStatement(updateCourse)){
				pstmt.setString(1, course_title);
				pstmt.setString(2, course_description);
				pstmt.setString(3, category);
				pstmt.setString(4, level);
				pstmt.setDouble(5, price);
				pstmt.setString(6, serial_number);
				
				int rowsAffected = pstmt.executeUpdate();
				if (rowsAffected > 0) {
					System.out.println("Course updated successfully!");
					notifyObservers();
				} else {
					logger.log(Level.WARNING, "no rows affected.");
				}
				
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Error updating Student table", e);
			}
	}
   	
   	
   	
 // get course_id 
 	public int getCourseIdBySerial(String c_serial) throws LMSException{  // -- here find the exact match
 		String query = "SELECT course_id FROM Course WHERE serial_number = ?";
 		int courseId = -1;
 		try {
 			PreparedStatement pstmt = connection.prepareStatement(query);
 			pstmt.setString(1, c_serial);
 			ResultSet rs = pstmt.executeQuery();
 			
 			if (rs.next()) {
 				courseId = rs.getInt("course_id");
 			} else {
 				throw new LMSException("Course not found with serial: " + c_serial, null);
 			}
 			
 		} catch (Exception e) {
 			throw new LMSException("Error catching course by this serial number, the course maybe removed from db.", e);
 		}
 		return courseId;
 	}
	
	// Add lesson to a course  
	public void addLessonToCourse(String course_serial, Lesson lesson) throws LMSException {
		int course_id = getCourseIdBySerial(course_serial);
		String query = "INSERT INTO Lesson(course_id, lesson_title, lesson_content) VALUES(?, ?, ?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setInt(1, course_id);
			pstmt.setString(2, lesson.getLesson_title());
			pstmt.setString(3, lesson.getLesson_content());
			
			int affectedrows = pstmt.executeUpdate();
			
			if (affectedrows == 0) {
				throw new SQLException("Creating lesson failed. No rows affected.");
			} else {
				logger.log(Level.INFO, "Lesson {0} added successfully!", lesson.getLesson_title());
			}
		} catch (SQLException e) {
			throw new LMSException("Error finding course by this serial number: ", e);
		}
	}
	
	// display lesson for each course --- course can be parsed by serial_number or course_id
	public void displayLessons(Object course_info) throws LMSException {
		int course_id;
		if (course_info instanceof String) {
			course_id = getCourseIdBySerial((String) course_info);
		} else if (course_info instanceof Integer) {
			course_id = (Integer) course_info;
		} else {
			logger.log(Level.SEVERE, "Invalid input type.");
			return;
		}
		
		String query = "SELECT * FROM Lesson WHERE course_id = ?";
		
		try {
			if (connection == null || connection.isClosed()) {
		        throw new SQLException("Connection is null or already closed.");
			}
			
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, course_id);
			
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				System.out.println("lesson_id " + rs.getInt("lesson_id") + 
						", course_id: " + rs.getString("course_id") +
						", lesson_title: " + rs.getString("lesson_title") + 
						", lesson_content: " + rs.getString("lesson_content"));
				System.out.println("------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	public List<Course> searchByTitleOrDescription(String searchTerm) {
		// catch courses from cache
		List<Course> courseList = cache.getCourses();
		List<Course> result = new ArrayList<>();
		
		// --- set a pattern to search by use of regular expressions ---
		String regx = ".*" + Pattern.quote(searchTerm)+ ".*"; // define regx to apply also wildcard on searching terms 
		Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
		
		for (Course crs : courseList) {
			Matcher titleMatcher = pattern.matcher(crs.getCourse_title());
			Matcher descMatcher = pattern.matcher(crs.getCourse_description());
			
			if (titleMatcher.find() || descMatcher.find()) {
				result.add(crs);
			}
		}
		if (result.isEmpty()) {
			logger.log(Level.WARNING, "No courses matched the search criteria: {0}", searchTerm);
		} else {
			logger.log(Level.INFO, "{0} courses found matching the search criteria", result.size());
		}
		
		return result;
	}

	
	// filter courses based on price, category or difficulty_level:
	public List<Course> filterCourses(CourseFilterStrategy strategy) {
		List<Course> courseList = cache.getCourses();
		List<Course> filteredCourses = new ArrayList<>();
		
		for (Course crs : courseList) {
			if (strategy.filter(crs)) {
				filteredCourses.add(crs);
			}
		}
		return filteredCourses;
	}
	
}
