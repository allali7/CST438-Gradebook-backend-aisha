// I created this
package com.cst438.controllers;

import java.sql.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentListDTO.AssignmentDTO;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

import java.util.List;
import java.util.Optional;


//we want to create a new assignment

@RestController
// from AssignmentListDTO we need a class AssignmentDTO
public class AssignmentController {
	
	//we need these to look things up in the course table and assignment table 
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	
	//Step1: client sends data in post 
	//---------------------------------
	
	@PostMapping("/assignment")
	//{assignmentId}/{courseId}/{assignmentName}/{dueDate}/{courseTitle}
	@Transactional
	// to refer to an inner class you have to name the outerclass
	// outer.inner so this is in AssignmentDTo within AssignmentListDTO
	public AssignmentListDTO.AssignmentDTO addAssignment (@RequestBody AssignmentListDTO.AssignmentDTO assignmentDTO){
		
		//Step 2: see if course id is valid
		//----------------------------------
		
		// if it is there it will get it 
		Course c = courseRepository.findById(assignmentDTO.courseId).get();
		if(c==null) {
			//invalid assignment error
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course not found. " + assignmentDTO.courseId);
			
		}
		
		//Setep3: if valid, create new entity, set name, set due date, set the course, save the course
		//save will return an update PK, save that PK back into the assignmentDTO, then retur it to user so that he gets 
		// the same data back but this time with the PK 
		
		//create a new assignment entity
		Assignment assignment = new Assignment();
		
		//copy data from assignmentDTO to assignment entity we created now
		assignment.setName(assignmentDTO.assignmentName);
		// convert dueData String to java.sql.Date
		assignment.setDueDate(Date.valueOf(assignmentDTO.dueDate));
		
		// this assignment is related to the course we found 
		assignment.setCourse(c);
		
		// go to assignment repository and do a save of this entity, it will return a new assignment.
		// the new assignment is updated with the primary key.
		Assignment newAssignment = assignmentRepository.save(assignment);
		
		//we will need to return that new updated assignment to the user
		assignmentDTO.assignmentId = newAssignment.getId();
	   
		// we will return the updated assignmentDTO
		return assignmentDTO;
		
	}
	
	//////////////////?????????? how do I put the new name of the assignment 
	// Change the name of the assignment
		@PutMapping("/assignment/{assignmentId}")
		@Transactional
		// (@RequestParam("newName") String newName, @PathVariable int assignmentId, @RequestBody AssignmentDTO aDTO)
		public AssignmentDTO updateAssigName (@RequestBody AssignmentDTO aDTO, @PathVariable int assignmentId ) {
			
			

			Assignment assignment = assignmentRepository.findById(assignmentId);
					
			if (assignment==null) {
				System.out.println("Assignment not found "+assignmentId);
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Assignment not found " ) ;
			} else {

			assignment.setName(aDTO.assignmentName);
			assignmentRepository.save(assignment);
			
		    aDTO.assignmentName = assignment.getName();
		    aDTO.dueDate = assignment.getDueDate().toString();
		    aDTO.assignmentId = assignment.getId();
		    aDTO.courseTitle = assignment.getCourse().getTitle();
		    aDTO.courseId = assignment.getCourse().getCourse_id();
		    
			System.out.println("Assigment name update: "+aDTO.assignmentName);
		    
		    // return aDTO with updated name to client
		    return aDTO;
			}
		}
		// Delete an assignment, if there are no grades
	    @DeleteMapping("/assignment/{assignment_id}")
	    @Transactional
	    public void deleteAssignment(@PathVariable int assignment_id) {
	    	Assignment assignment = assignmentRepository.findById(assignment_id).get();  	
	    	if (assignment == null) {
	    		throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Assignment_id not valid");
	    	}
	    	List<AssignmentGrade> hasGrades = assignmentGradeRepository.findHasGrade(assignment_id);
			if (hasGrades.size() == 0) {
	    		assignmentRepository.delete(assignment);
	    		System.out.println("Assigment deleted: "+assignment_id);
	    	} else {
				throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Grades already exist");
			}
	    }
		
		

}
