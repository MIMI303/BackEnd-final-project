package com.iktpreobuka.elektronskidnevnik.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iktpreobuka.elektronskidnevnik.entities.StudentTeacherCourseEntity;
import com.iktpreobuka.elektronskidnevnik.repositories.StudentTeacherCourseRepository;

@Service
public class StudentTeacherCourseServiceImpl implements StudentTeacherCourseService {
	
	@Autowired
	private StudentTeacherCourseRepository studentTeacherCourseRepository;
	
	@Override
	public Boolean isActive(Integer id) {
		if(studentTeacherCourseRepository.existsById(id)) {
			StudentTeacherCourseEntity stce = studentTeacherCourseRepository.findById(id).get();
			if(stce.getDeleted().equals(true) 
					|| stce.getTeacherCourse().getCourse().getDeleted().equals(true)
					|| stce.getTeacherCourse().getTeacher().getDeleted().equals(true)
					|| stce.getStudent().getDeleted().equals(true)) {
				return false;
			}
			return true;
		}
		return false;
	}
}
