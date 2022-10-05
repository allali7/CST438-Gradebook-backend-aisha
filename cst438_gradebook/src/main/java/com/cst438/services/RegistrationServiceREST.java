package com.cst438.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import com.cst438.domain.CourseDTOG;
import com.cst438.domain.EnrollmentDTO;

public class RegistrationServiceREST extends RegistrationService {

	
	RestTemplate restTemplate = new RestTemplate();
	
	@Value("${registration.url}") 
	String registration_url;
	
	public RegistrationServiceREST() {
		System.out.println("REST registration service ");
	}
	
	@Override
	public void sendFinalGrades(int course_id , CourseDTOG courseDTO) { 
		
		//TODO  complete this method in homework 4
		// we got the courseDTO and we need to send it to registation backend
		
		System.out.println("Sending final grades " + course_id+ " "+courseDTO);
		restTemplate.put(registration_url + "/course/" + course_id, courseDTO);
		System.out.println("After sending final grades");
	}
}

/*
//TODO  complete this method in homework 4
EnrollmentDTO enrollment = new EnrollmentDTO();
enrollment.course_id = course_id;
enrollment.studentEmail=student_email;
enrollment.studentName= student_name;


System.out.println("Post to gradebook "+ enrollment);
EnrollmentDTO response= restTemplate.postForObject(gradebook_url + "/enrollment", enrollment, EnrollmentDTO.class);
System.out.println("Response from gradebook "+ response); */


/*
public class CourseDTOG {

public static class GradeDTO {
	public String student_email;
	public String student_name;
	public String grade;
*/