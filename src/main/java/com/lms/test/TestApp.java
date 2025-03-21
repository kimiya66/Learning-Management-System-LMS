package com.lms.test;
import com.lms.filterCourses.*;
import com.lms.managers.*;
import com.lms.models.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lms.config.*;
import com.lms.db.DatabaseConnection;
import com.lms.db.TableCreator;
import com.lms.exceptions.LMSException;

public class TestApp {
	
	// Define managers and cache as static fields to use throughout the class
    private static Connection connection;
    private static CourseManager courseManager;
    private static UserManager userManager;
    private static EnrollmentManager enrollmentManager;
    private static CourseCache courseCache;
    private static final Logger logger = Logger.getLogger(TestApp.class.getName());
    
    public static void setup() {
		
    	try {
    		// --- set up logging - to see the logging for lower levels ---
    		LoggerConfig.setupLogging();
    		
    		// --- Establish DB connection ---
    		connection = DatabaseConnection.getConnection();
    		
    		// --- Create tables ---
    		TableCreator.createTables();
    		
    		// --- Initialize managers ---
    		userManager = UserManager.getInstance();
    		courseManager = CourseManager.getInstance();
            enrollmentManager = EnrollmentManager.getInstance();
            
            // --- Initialize the cached-courses ---
            courseCache = CourseCache.getInstance(courseManager);
            courseManager.setCache(courseCache);
            
            System.out.println("✅ Setup completed successfully!");
		} catch (Exception e) {
			e.printStackTrace();
	        throw new RuntimeException("Failed to initialize the application", e);
		}
	}
	
	public static void main(String[] args) throws LMSException, SQLException {
		
		setup();	
	
        TestApp testApp = new TestApp();
        
		
		System.out.println("Welcome to elearning platform, please enter your choice:");
		Scanner scannerUI = new Scanner(System.in);
		
        int choice;
		boolean exitRequested = false;

        while (!exitRequested) {
            System.out.println("\n=== Learning Management System (LMS) ===");
            System.out.println("1. Student management");
            System.out.println("2. Create a course");
            System.out.println("3. Display all available courses");
            System.out.println("4. Assign lesson to a course");
            System.out.println("5. Manage Enrollments");
            System.out.println("6. Find cources by title or description");
            System.out.println("7. filter courses");
            System.out.println("8. rate a course");
            System.out.println("9. Display ratings");
            System.out.println("10. Mark course as completed");
            System.out.println("11. Display progress for each student");
            System.out.println("12. Exit");
            System.out.print("Enter your choice: ");
			
            choice = scannerUI.nextInt();
            scannerUI.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                	testApp.studentManagement();
                    break;
                case 2:
                    System.out.println("Enter Course Information:");
                    
                    System.out.println("Serial_number:"); String serial = scannerUI.nextLine();
             
                    Course course = new Course(serial, "AI1", "this is for AI improvements", "A2", "B2", 17.25);
                    courseManager.addCourse(course);
                    break;
                    
                case 3:
                    List<Course> allCourses = courseManager.getAllCourses();
                    allCourses.forEach(System.out::println);
                    break;
                case 4:	
                	System.out.println("Enter the course serial number to which you want assign a new lesson:");
                    String serialNo = scannerUI.nextLine();

                    System.out.println("enter the lesson title:"); String lesson_title = scannerUI.nextLine();
                    System.out.println("enter contents or description..."); String content = scannerUI.nextLine();
                    
                    Lesson new_lesson = new Lesson(lesson_title, content);
                    courseManager.addLessonToCourse(serialNo, new_lesson);
                    
                    System.out.println("Available lessons to this course:");
                    courseManager.displayLessons(serialNo);
                    break;
                case 5:
                	testApp.enrollmentManagement();
                	break;
                case 6:
                    // search courses based on title or description
                	List<Course> courses = courseManager.getAllCourses();
                    courses.forEach(System.out::println);
	                System.out.println("enter your search");
	                String searchTerm = scannerUI.nextLine();
	                List<Course> foundCoursesResult = courseManager.searchByTitleOrDescription(searchTerm);
	                System.out.println("Founded courses: ");
	                foundCoursesResult.forEach(System.out::println);
                	break;
                case 7:
                	// filter by Price
                	System.out.println("enter the max price to filter the course:");
                	double maxPrice = scannerUI.nextDouble(); scannerUI.nextLine();
                	PriceFilter priceStrategy = new PriceFilter(maxPrice);
                	List<Course> Courses_filteredByPrice = courseManager.filterCourses(priceStrategy);
                	System.out.println("Courses under:" + maxPrice + "€");
                	Courses_filteredByPrice.forEach(System.out::println);
                	
                	// filter by category
                	System.out.println("enter the category to filter the course:");
                	CategoryFilter categoryStrategy = new CategoryFilter(scannerUI.nextLine());
                	List<Course> Crs_filteredByCategory = courseManager.filterCourses(categoryStrategy);
                	Crs_filteredByCategory.forEach(System.out::println);
                	
                	// filter by difficulty_level
                	System.out.println("enter the difficulty_level to filter the course:");
                	CategoryFilter difficultyStrategy = new CategoryFilter(scannerUI.nextLine());
                	List<Course> Crs_filteredByLevel = courseManager.filterCourses(difficultyStrategy);
                	Crs_filteredByLevel.forEach(System.out::println);
                	break;
                case 8:
                	// choose a student for rating 
        	        userManager.getAllStudents();
        	        System.out.println("Enter the student id");
        	        int student_id = scannerUI.nextInt();
        	        scannerUI.nextLine();
        	        
        	        System.out.println("This is the list of your enrolled courses:");
        	        enrollmentManager.getEnrolledCoursesByStudent(student_id);
        	        
        	        System.out.println("record a rating to an enrolled course: ");
        	        
        	        System.out.println("enter the course id for rating: ");
        	        int course_id = scannerUI.nextInt();
        	        scannerUI.nextLine();
        	        System.out.println("enter your rate between 1 and 5: ");
        	        double rate = scannerUI.nextDouble();
        	        scannerUI.nextLine();
        	        
        	        enrollmentManager.updateCourseRating(course_id, student_id, rate);
                	break;
                case 9: 
                	System.out.println("list of all ratings:");
                	enrollmentManager.displayCourseAvgRating();
                	break;
                case 10:
                    // --- record progress to a course ---     
                    System.out.println("list of all enrollments: ");
                    enrollmentManager.displayEnrollments();
                    
                    // mark a course as completed 
                    System.out.println("choose the lesson as completed, \nenrollment_id: ");
                    int enrollment_id = scannerUI.nextInt();
                    scannerUI.nextLine();
                    System.out.println("enter course id: ");
                    int courseID = scannerUI.nextInt();
                    scannerUI.nextLine();
                    System.out.println("List of all lessons for this course");
                    courseManager.displayLessons(courseID);
                    System.out.println("Enter the lessonID as completed: ");
                    int lesson_id = scannerUI.nextInt();
                    scannerUI.nextLine();
                    enrollmentManager.markLessonCompleted(enrollment_id, lesson_id);
                	break;
                case 11:
                	System.out.println("progress based on each enrollment, \nList of all enrollments:");
                	enrollmentManager.displayEnrollments();
                	System.out.println("Enter the enrollmentID for which you want to see the progress:");
                	int enrollmentID = scannerUI.nextInt(); scannerUI.nextLine();
                	System.out.println("Enter the courseID:");
                	int crsID = scannerUI.nextInt(); scannerUI.nextLine();
                    System.out.println(enrollmentManager.getStudentProgress(enrollmentID, crsID));
                	break;
                case 12:
                    System.out.println("Exiting LMS...");
                    exitRequested = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
          
        }  
        scannerUI.close();
       
	}
	
	// --- student management ---
		public void studentManagement() {
			
			System.out.println("Please select an option:");
			Scanner scanner = new Scanner(System.in);
			
	        int option;
			boolean exit = false;

	        while (!exit) {
	            System.out.println("1. display all students");
	            System.out.println("2. Add a Student");
	            System.out.println("3. Remove a student/user");
	            System.out.println("4. Update a student data");
	            System.out.println("5. Request exit");
	          
	            System.out.print("Enter your choice: ");
	          
	            option = scanner.nextInt();
	            scanner.nextLine(); // Consume newline
	            
	            switch (option) {
	                case 1:
	                	userManager.getAllStudents();
	                	break;
	                case 2:
	                	System.out.println("enter student email address");
	                	String email = scanner.nextLine();
	                	Student stu = new Student("testUsername", "testLastname", email, "Informatik");
	                	userManager.addStudent(stu);
	                	break;
	                case 3:
	                	System.out.println("enter the student email address to delete from db : ");
	        			userManager.removeStudent(scanner.nextLine());
	        			break;
	                case 4:
	                	System.out.println("Enter the student email address to update: ");
	                	String email_address = scanner.nextLine();
	                	System.out.println("new name: "); String firstname = scanner.nextLine();
	                	System.out.println("new family name: "); String lastname = scanner.nextLine();
	                	System.out.println("study subject: "); String subject = scanner.nextLine();
	                	
	                	userManager.updateStudent(email_address, firstname, lastname, subject);
	                	break;
	                case 5:
	                	System.out.println("exiting student/user management...");
	                	exit = true;
	                	break;
	                default:
	                	System.out.println("Invalid option. Please try again.");
	                    break;		
	            }
	        }
		}
		
		// --- Enrollment manager ---
		public void enrollmentManagement() {
			
			System.out.println("Please select an option to manage enrollment:");
			Scanner scanner = new Scanner(System.in);
			
	        int option;
			boolean exit = false;

	        while (!exit) {
	            System.out.println("1. Enroll a student to a course");
	            System.out.println("2. Display all enrolled courses");
	            System.out.println("3. Display all enrolled students to a course");
	            System.out.println("4. Delete an enrollment");
	            System.out.println("5. Request exit");
	          
	            System.out.print("Enter your option: ");
	            option = scanner.nextInt();
	            scanner.nextLine(); // Consume newline
	            
	            switch (option) {
				case 1:
					// choose a student
					System.out.println("list of all students:");
			        userManager.getAllStudents();
			        System.out.println("Enter the student ID: ");
		            int student_id = scanner.nextInt();
		            scanner.nextLine();
		            // choose a course
		     		System.out.print("Enter the Course ID: ");
			        int courseId = scanner.nextInt();
			        enrollmentManager.enrollStudentInCourse(student_id, courseId);
			        scanner.nextLine();
					break;
				case 2:
					 // get enrolled courses of a student	        
			        System.out.println("Enter the student id");
			        int id = scanner.nextInt();
			        enrollmentManager.getEnrolledCoursesByStudent(id);
					break;
				case 3:
			        System.out.println("Enter the course id");
			        int course_id = scanner.nextInt();
			        enrollmentManager.getEnrolledStudentsByCourse(course_id);
			        break;
				case 4: 
					System.out.println("Enter the enrollment id");
			        int enrollmentId = scanner.nextInt();
			        enrollmentManager.deleteEnrollmentById(enrollmentId);
			        break;
				case 5:
                	System.out.println("exiting student/user management...");
                	exit = true;
                	break;

				default:
					logger.log(Level.WARNING, "Invalid option. Please try again.");
					break;
				}
	        }
		}
}
	   
	                	
	     
