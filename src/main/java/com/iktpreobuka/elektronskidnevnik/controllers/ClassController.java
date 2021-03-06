package com.iktpreobuka.elektronskidnevnik.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iktpreobuka.elektronskidnevnik.entities.ClassEntity;
import com.iktpreobuka.elektronskidnevnik.entities.CourseEntity;
import com.iktpreobuka.elektronskidnevnik.entities.StudentEntity;
import com.iktpreobuka.elektronskidnevnik.entities.TeacherEntity;
import com.iktpreobuka.elektronskidnevnik.entities.dto.ClassDto;
import com.iktpreobuka.elektronskidnevnik.entities.util.RESTError;
import com.iktpreobuka.elektronskidnevnik.repositories.ClassRepository;
import com.iktpreobuka.elektronskidnevnik.repositories.CourseRepository;
import com.iktpreobuka.elektronskidnevnik.repositories.StudentRepository;
import com.iktpreobuka.elektronskidnevnik.repositories.TeacherCourseRepository;
import com.iktpreobuka.elektronskidnevnik.repositories.TeacherRepository;
import com.iktpreobuka.elektronskidnevnik.services.ClassService;
import com.iktpreobuka.elektronskidnevnik.services.CourseService;
import com.iktpreobuka.elektronskidnevnik.services.TeacherService;
import com.iktpreobuka.elektronskidnevnik.utils.ClassCustomValidator;

@RestController
@RequestMapping(value = "/api/v1/classes")
public class ClassController {

	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ClassRepository classRepository;

	@Autowired
	private TeacherRepository teacherRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private ClassService classService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private TeacherCourseRepository teacherCourseRepository;

	@Autowired
	private ClassCustomValidator classValidator;

	@InitBinder
	protected void initBinder(final WebDataBinder binder) {
		binder.addValidators(classValidator);
	}

	// dodaj novi razred

	@Secured("ROLE_ADMIN")
	@PostMapping(value = "/")
	public ResponseEntity<?> createNew(@Valid @RequestBody ClassDto newClass, BindingResult result) {
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
		} else {
			classValidator.validate(newClass, result);
		}
		ClassEntity classEntity = new ClassEntity();
		classEntity.setDeleted(false);
		classEntity.setClassNumber(newClass.getClassNumber());
		classEntity.setYear(newClass.getYear());
		classRepository.save(classEntity);
		logger.info("Added new class: " + newClass.toString());
		return new ResponseEntity<ClassEntity>(classEntity, HttpStatus.OK);
	}

	// vrati sve razrede
	
	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/")
	public ResponseEntity<?> getAll() {
		return new ResponseEntity<List<ClassEntity>>(((List<ClassEntity>) classRepository.findAll())
				.stream().filter(clazz -> !clazz.getDeleted().equals(true))
				.collect(Collectors.toList()), HttpStatus.OK);
	}
	
	
	//	izmeni razred
	
	@Secured("ROLE_ADMIN")
	@PutMapping(value = "/{classId}")
	public ResponseEntity<?> updateClass(@PathVariable Integer classId, @Valid @RequestBody ClassDto uClass,
			BindingResult result) {
		if (classRepository.existsById(classId) && classService.isActive(classId)) {
			if(result.hasErrors()) {
				return new ResponseEntity<>(createErrorMessage(result), HttpStatus.BAD_REQUEST);
			}
			ClassEntity clazz = classRepository.findById(classId).get();
			clazz.setYear(uClass.getYear());
			clazz.setClassNumber(uClass.getClassNumber());
			classRepository.save(clazz);
			logger.info("Updated class with ID:" + classId.toString());
			return new ResponseEntity<ClassEntity>(clazz, HttpStatus.OK);
		}
		return new ResponseEntity<RESTError>(new RESTError(1, "Class not found."), HttpStatus.NOT_FOUND);
	}
	
	// obrisi razred
	
	@Secured("ROLE_ADMIN")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deleteClass(@PathVariable Integer id) {
		if (classRepository.existsById(id) && classService.isActive(id)) {
			ClassEntity temp = classRepository.findById(id).get();
			temp.setDeleted(true);
			classRepository.save(temp);
			logger.info("Deleted class with ID: " + id.toString());
			return new ResponseEntity<ClassEntity>(temp, HttpStatus.OK);
		}
		return new ResponseEntity<RESTError>(new RESTError(1, "Class not found."), HttpStatus.NOT_FOUND);
	}
	
	
	
	//dodaj nastavnika razredu
	
	@Secured("ROLE_ADMIN")
	@PostMapping(value = "/{classId}/supervisor/{teacherId}")
	public ResponseEntity<?> addSupervisorTeacher(@PathVariable Integer classId, @PathVariable Integer teacherId) {
		if(classRepository.existsById(classId) && classService.isActive(classId)) {
			if(teacherRepository.existsById(teacherId) && teacherService.isActive(teacherId)) {
				if(!classRepository.existsBySupervisorTeacher(teacherRepository.findById(teacherId).get())) {
					ClassEntity classEntity = classRepository.findById(classId).get();
					TeacherEntity teacherEntity = teacherRepository.findById(teacherId).get();
					classEntity.setSupervisorTeacher(teacherEntity);
					classRepository.save(classEntity);
					logger.info("For class with ID " + classId.toString() + " added supervisor teacher with ID " + teacherId.toString());
					return new ResponseEntity<ClassEntity>(classEntity, HttpStatus.OK);
				}
				return new ResponseEntity<RESTError>(new RESTError(8, "Teacher already supervises one class."), HttpStatus.NOT_FOUND);	
			}
			return new ResponseEntity<RESTError>(new RESTError(6, "Teacher not found."), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<RESTError>(new RESTError(1, "Class not found."), HttpStatus.NOT_FOUND);
	}

	//	promeni nastavnika za razred.....isti kod samo druga metoda mappinga
	
	@Secured("ROLE_ADMIN")
	@PutMapping(value = "/{classId}/supervisor/{teacherId}")
	public ResponseEntity<?> updateSupervisorTeacher(@PathVariable Integer classId, @PathVariable Integer teacherId) {
		if(classRepository.existsById(classId) && classService.isActive(classId)) {
			if(teacherRepository.existsById(teacherId) && teacherService.isActive(teacherId)) {
				if(!classRepository.existsBySupervisorTeacher(teacherRepository.findById(teacherId).get())) {
					ClassEntity classEntity = classRepository.findById(classId).get();
					TeacherEntity teacherEntity = teacherRepository.findById(teacherId).get();
					classEntity.setSupervisorTeacher(teacherEntity);
					classRepository.save(classEntity);
					logger.info("For class with ID " + classId.toString() + " changed supervisor teacher with ID " + teacherId.toString());
					return new ResponseEntity<ClassEntity>(classEntity, HttpStatus.OK);
				}
				return new ResponseEntity<RESTError>(new RESTError(8, "Teacher already supervises one class."), HttpStatus.NOT_FOUND);	
			}
			return new ResponseEntity<RESTError>(new RESTError(6, "Teacher not found."), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<RESTError>(new RESTError(1, "Class not found."), HttpStatus.NOT_FOUND);
	}
	
    //	dodaj predmet ucenicima
	
	@Secured("ROLE_ADMIN")
	@PostMapping(value = "/{classId}/courses/{courseId}/teachers/{teacherId}")
	public ResponseEntity<?> addCoursesForEntireClass(@PathVariable Integer classId, @PathVariable Integer courseId, @PathVariable Integer teacherId) {
		if(classRepository.existsById(classId) && classService.isActive(classId)) {
			if(courseRepository.existsById(courseId) && courseService.isActive(courseId)) {
				if(teacherRepository.existsById(teacherId) && teacherService.isActive(teacherId)) {
					TeacherEntity teacher = teacherRepository.findById(teacherId).get();
					CourseEntity course = courseRepository.findById(courseId).get();
					if (teacherCourseRepository.existsByTeacherAndCourse(teacher, course)) {
						if (studentRepository.existsByAttendingClass(classRepository.findById(classId).get())) {
							List<StudentEntity> modifiedStudents = classService.addCoursesForEntireClass(classId,courseId, teacherId);
							logger.info("For class with ID " + classId.toString() + " added course with ID " + courseId.toString());
							return new ResponseEntity<List<StudentEntity>>(modifiedStudents, HttpStatus.OK);
						}
						return new ResponseEntity<RESTError>(new RESTError(9, "Class has no students."), HttpStatus.BAD_REQUEST);
					}
					return new ResponseEntity<RESTError>(new RESTError(11, "Teacher course combination not found."), HttpStatus.NOT_FOUND);
				}
				return new ResponseEntity<RESTError>(new RESTError(6, "Teacher not found."), HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<RESTError>(new RESTError(2, "Course not found."), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<RESTError>(new RESTError(1, "Class not found."), HttpStatus.NOT_FOUND);
	}
	
	public String createErrorMessage(BindingResult result) {
		String errors = "";
		for (ObjectError error : result.getAllErrors()) {
			errors += error.getDefaultMessage();
			errors += "\n";
		}
		return errors;
	}
}
