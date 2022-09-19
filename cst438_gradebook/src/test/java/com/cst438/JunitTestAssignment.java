package com.cst438;


import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.BDDMockito.*;


import static org.mockito.ArgumentMatchers.any;

import org.junit.Assert;

import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cst438.controllers.AssignmentController;
import com.cst438.controllers.EnrollmentController;
import com.cst438.controllers.GradeBookController;
import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentListDTO.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.GradebookDTO;
import com.cst438.services.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;




@ContextConfiguration(classes = { GradeBookController.class, AssignmentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest

public class JunitTestAssignment {
	

	// I need to test the course 40442 so I need a fake database to have that course in it 
	// so we will create the course but we don't need to give it any enrollments 
	
		static final String URL = "http://localhost:8080";
		public static final int TEST_COURSE_ID = 40442;
		public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
		public static final String TEST_STUDENT_NAME = "test";
		public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
		public static final int TEST_YEAR = 2021;
		public static final String TEST_SEMESTER = "Fall";
		public static final String TEST_DUEDATE = "2021-01-01";

		@MockBean
		AssignmentRepository assignmentRepository;

		@MockBean
		AssignmentGradeRepository assignmentGradeRepository;

		@MockBean
		CourseRepository courseRepository; // must have this to keep Spring test happy

		@MockBean
		RegistrationService registrationService; // must have this to keep Spring test happy

		@Autowired
		private MockMvc mvc;

		@Test
		public void addAssignment() throws Exception {

			MockHttpServletResponse response;

			// mock database data

			Course course = new Course();
			course.setCourse_id(TEST_COURSE_ID);
			
			// given -- stubs for database repositories that return test data
			given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
			
			// end of mock data

			// then do an http add an assignment 
			// we created a new assignment DTO with a no argument constructor
			AssignmentListDTO.AssignmentDTO aDTO = new AssignmentListDTO.AssignmentDTO();
			// set the fields here
			// we don't need to set the primary id since that is done automatically
			// we set the course ID of the assignment, course title douesn't need course title
			// it needs due date
			
			// this will be the body of the post we sent in .content()
			aDTO.assignmentName = "test assignment";
			aDTO.courseId = TEST_COURSE_ID;
			aDTO.dueDate = "2022-9-12";
			
			// make a post call to add the assignment 
			response = mvc.perform(MockMvcRequestBuilders.post("/assignment").
					accept(MediaType.APPLICATION_JSON)
					.content(asJsonString(aDTO))
					.contentType(MediaType.APPLICATION_JSON))
					.andReturn().getResponse();
			
			// verify return data with entry for one student without no score - was the response code 200? 
			assertEquals(200, response.getStatus());

			//get the response body and convert to a java object, we have to give it a body and what object it wiil carry 
			AssignmentListDTO.AssignmentDTO returnedDTO = fromJsonString(response.getContentAsString(), AssignmentListDTO.AssignmentDTO.class );

			// now what should we look for? 
			// check that the return assignment id is not 0, if everything worked well, it put it into db and 
			// returned assignment id. the 1st id is 1 not 0 so 0 is a flag
			assertNotEquals(0, returnedDTO.assignmentId);
			// verify that repository save was called on repository
			verify(assignmentRepository, times(1)).save(any()); // verify that assignment controller actually did a save to the database
		}
		
		@Test
		public void deleteAssignment () throws Exception {

			
			
			Course course = new Course();
			course.setCourse_id(TEST_COURSE_ID);
			course.setSemester(TEST_SEMESTER);
			course.setYear(TEST_YEAR);
			course.setInstructor(TEST_INSTRUCTOR_EMAIL);
			course.setEnrollments(new java.util.ArrayList<Enrollment>());
			course.setAssignments(new java.util.ArrayList<Assignment>());

			Enrollment enrollment = new Enrollment();
			enrollment.setCourse(course);
			course.getEnrollments().add(enrollment);
			enrollment.setId(TEST_COURSE_ID);
			enrollment.setStudentEmail(TEST_STUDENT_EMAIL);
			enrollment.setStudentName(TEST_STUDENT_NAME);

			Assignment assignment = new Assignment();
			assignment.setCourse(course);
			course.getAssignments().add(assignment);

			assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
			assignment.setId(1);
			assignment.setName("Assignment 1");
			assignment.setNeedsGrading(1);
			
			AssignmentGrade grade = new AssignmentGrade();
			grade.setAssignment(assignment);
			grade.setId(1);
			grade.setScore("");
			grade.setStudentEnrollment(enrollment);
			
			given(assignmentRepository.findById(1)).willReturn(assignment);
			given(assignmentGradeRepository.save(any())).willReturn(grade);
			
			MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.delete("/assignment/1").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

			assertEquals(200, response.getStatus());
			
		}
		
		
		
		@Test
		public void updateAssignmentName () throws Exception {

			MockHttpServletResponse response;

			Course course = new Course();
			course.setCourse_id(TEST_COURSE_ID);
			course.setSemester(TEST_SEMESTER);
			course.setYear(TEST_YEAR);
			course.setInstructor(TEST_INSTRUCTOR_EMAIL);
			course.setEnrollments(new java.util.ArrayList<Enrollment>());
			course.setAssignments(new java.util.ArrayList<Assignment>());

			Enrollment enrollment = new Enrollment();
			enrollment.setCourse(course);
			course.getEnrollments().add(enrollment);
			enrollment.setId(TEST_COURSE_ID);
			enrollment.setStudentEmail(TEST_STUDENT_EMAIL);
			enrollment.setStudentName(TEST_STUDENT_NAME);
			
	        Assignment assignment = new Assignment();
	        assignment.setCourse(course);
	        course.getAssignments().add(assignment);
	        assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
	        assignment.setId(1);
	        assignment.setName("Assignment 1");
	        assignment.setNeedsGrading(1);
	        
			AssignmentGrade ag = new AssignmentGrade();
			ag.setAssignment(assignment);
			ag.setId(1);
			ag.setScore("");
			ag.setStudentEnrollment(enrollment);

			given(assignmentGradeRepository.save(any())).willReturn(ag);
			given(assignmentRepository.findById(1)).willReturn(assignment);
			given(assignmentGradeRepository.findByAssignmentIdAndStudentEmail(1, TEST_STUDENT_EMAIL)).willReturn(null);

			response = mvc.perform(MockMvcRequestBuilders.get("/gradebook/1").accept(MediaType.APPLICATION_JSON))
					.andReturn().getResponse();

			assertEquals(200, response.getStatus());

			GradebookDTO result = fromJsonString(response.getContentAsString(), GradebookDTO.class);
			
			AssignmentDTO content = new AssignmentDTO();
			content.courseId = TEST_COURSE_ID;
			content.dueDate = TEST_DUEDATE;
			content.assignmentId = result.assignmentId;
			content.assignmentName = "New assignmnet name";

			response = mvc.perform(MockMvcRequestBuilders.put("/assignment/1").accept(MediaType.APPLICATION_JSON).content(asJsonString(content)).contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();
			
			AssignmentDTO newresult = fromJsonString(response.getContentAsString(), AssignmentDTO.class);
		
			assertEquals(200, response.getStatus());
			assertEquals("New assignmnet name", newresult.assignmentName);
		}
			
			private static String asJsonString(final Object obj) {
				try {

					return new ObjectMapper().writeValueAsString(obj);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			private static <T> T fromJsonString(String str, Class<T> valueType) {
				try {
					return new ObjectMapper().readValue(str, valueType);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

		}

