package com.cst438.domain;

public class AssignmentDTO {
    public int assignmentId;
    public String assignmentName;
    public String dueDate;
    public int courseId;
    public String courseTitle;
    
    @Override
    public String toString() {
        return "AssignmentDTO [assignmentId=" + assignmentId + ", assignmentName=" + assignmentName
                + ", dueDate=" + dueDate + ", courseId=" + courseId + ", courseTitle=" + courseTitle + "]";
    }
    
}
