package com.tka.sams.api.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tka.sams.api.entity.AttendanceRecord;
import com.tka.sams.api.entity.Student;
import com.tka.sams.api.entity.Subject;
import com.tka.sams.api.entity.User;
import com.tka.sams.api.model.AttendanceRecordRequest;
import com.tka.sams.api.service.AttendanceRecordService;
import com.tka.sams.api.service.StudentService;
import com.tka.sams.api.service.SubjectService;
import com.tka.sams.api.service.UserService;

@RestController
@RequestMapping("/attendance")
@CrossOrigin("http://localhost:4200")

public class AttendanceController {

	@Autowired
	private AttendanceRecordService attendanceRecordService;

	@Autowired
	private UserService userService;

	@Autowired
	private SubjectService subjectService;

	@Autowired
	private StudentService studentService;

	@GetMapping("/get-all-attendance-records")
	public List<AttendanceRecord> getAllAttendanceRecords() {
		return attendanceRecordService.getAllAttendanceRecords();
	}
	
	 @GetMapping("/get-attendance-by-faculty/{facultyUsername}")
	    public List<AttendanceRecord> getAttendanceByFaculty(@PathVariable String facultyUsername) {
	        return attendanceRecordService.getAttendanceByFaculty(facultyUsername);
	    }
	
	
	@GetMapping("/get-attendance-by-date-subjet/{date}/{subjectId}")
	public List<AttendanceRecord> getAllAttendanceRecords(@PathVariable String date,@PathVariable long subjectId){
		return attendanceRecordService.getAllAttendanceRecords(date,subjectId);
	}
	
	@GetMapping("/get-attendance/{faculty}/{subjectId}/{date}")
	public List<AttendanceRecord> getAttendanceByFacultySubjectDate(
	        @PathVariable String faculty, 
	        @PathVariable long subjectId, 
	        @PathVariable String date) {
	    return attendanceRecordService.getAttendanceByFacultySubjectDate(faculty, subjectId, date);
	}


	
	@PostMapping("/take-attendance")
	public ResponseEntity<?> createAttendanceRecord(@RequestBody AttendanceRecordRequest request) {
	    try {
	        // ✅ Debugging: Log incoming request
	        System.out.println("🔍 Received Attendance Request: " + request);

	        if (request.getSubjectId() == null) {
	            return ResponseEntity.badRequest().body("❌ ERROR: subjectId is missing in the request!");
	        }

	        if (request.getUsername() == null || request.getUsername().isEmpty()) {
	            return ResponseEntity.badRequest().body("❌ ERROR: username is missing in the request!");
	        }

	        // Fetch user
	        User user = userService.getUserByName(request.getUsername());
	        if (user == null) {
	            return ResponseEntity.badRequest().body("❌ ERROR: User not found: " + request.getUsername());
	        }

	        // Fetch subject
	        Subject subject = subjectService.getSubjectById(request.getSubjectId());
	        if (subject == null) {
	            return ResponseEntity.badRequest().body("❌ ERROR: Subject not found for ID: " + request.getSubjectId());
	        }

	        // ✅ Debugging: Log fetched subject
	        System.out.println("✅ Found Subject: " + subject);

	        // Create attendance record
	        AttendanceRecord attendanceRecord = new AttendanceRecord();
	        attendanceRecord.setUser(user);
	        attendanceRecord.setSubject(subject);
	        attendanceRecord.setDate(request.getDate());
	        attendanceRecord.setTime(request.getTime());

	        // Fetch students safely
	        Set<Student> students = (request.getStudentIds() != null) ?
	            request.getStudentIds().stream()
	                .map(studentId -> {
	                    Student student = studentService.getStudentById(studentId);
	                    if (student == null) {
	                        throw new RuntimeException("❌ ERROR: Student not found for ID: " + studentId);
	                    }
	                    return student;
	                })
	                .collect(Collectors.toSet())
	            : Collections.emptySet();

	        attendanceRecord.setStudents(students);
	        attendanceRecord.setNumberOfStudents(students.size());

	        // ✅ Debugging: Log final attendance record
	        System.out.println("✅ Saving Attendance Record: " + attendanceRecord);

	        AttendanceRecord savedRecord = attendanceRecordService.saveAttendance(attendanceRecord);
	        return ResponseEntity.ok(savedRecord);

	    } catch (Exception e) {
	        System.err.println("❌ Server error: " + e.getMessage());
	        return ResponseEntity.status(500).body("❌ Server error: " + e.getMessage());
	    }
	}


}
