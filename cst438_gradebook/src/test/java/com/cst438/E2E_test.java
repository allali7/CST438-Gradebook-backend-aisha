package com.cst438;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentRepository;



@SpringBootTest
public class E2E_test {
	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/admin/Desktop/chromedriver";
	public static final String URL = "http://localhost:3000";
	public static final String ALIAS_NAME = "test";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final int course_ID_TEST = 7077;
	public static final String ASSIG_NAME_TEST= "E2ETest";
	public static final String ASSIG_DUE_DATE_TEST = "2022-10-15";
	public static final String course_ID = "123456";

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;
	
	
	@Test
	public void AddNewAssignmentTest() throws Exception{
		
		 
		//TODO update the property name for your browser 
		System.setProperty("webdriver.chrome.driver",
                     CHROME_DRIVER_FILE_LOCATION);
		//TODO update the class ChromeDriver()  for your browser
		WebDriver driver = new ChromeDriver();
		
		try {
//			WebElement we = 
//			List<WebElement> we;
			
			driver.get(URL);
			// must have a short wait to allow time for the page to download 
			Thread.sleep(SLEEP_DURATION);
			
//			driver.findElement(By.xpath("//a[last()]")).click();
//			Thread.sleep(SLEEP_DURATION);
			
			
			WebElement we = driver.findElement(By.id("viewBtn"));
			we.click();
			Thread.sleep(SLEEP_DURATION);
			
			we = driver.findElement(By.id("addBtn"));
			we.click();
			Thread.sleep(SLEEP_DURATION);
			
			we = driver.findElement(By.name("assignmentName"));
			we.sendKeys(ASSIG_NAME_TEST);
			driver.findElement(By.name("dueDate")).sendKeys(ASSIG_DUE_DATE_TEST);
			driver.findElement(By.name("courseId")).sendKeys(course_ID);
			
	        driver.findElement(By.id("Add")).click();
			Thread.sleep(SLEEP_DURATION);
			
			com.cst438.domain.Assignment a = assignmentRepository.findBy_Id_Name(course_ID_TEST, ASSIG_NAME_TEST);
			assertNotNull(a); 
			
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
			
		} finally {
			driver.quit();
		}
	}

}

