package com.iktpreobuka.elektronskidnevnik.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.iktpreobuka.elektronskidnevnik.entities.enums.EnumSchoolYear;
import com.iktpreobuka.elektronskidnevnik.entities.enums.EnumSemester;

@Entity
@Table(name = "course")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class CourseEntity {
	
	@Id
	@GeneratedValue
	private Integer id;
	
	@Column
	@NotNull(message = "Course name must not be null.")
	@Size(min = 5, max = 30, message = "Course name must be between {min} and {max} characters.")
	private String name;
	
	@Column
	@NotNull(message = "Weekly hours must not be null.")
	@Min(value = 0, message = "Weekly hours cannot be less than zero.")
	@Max(value = 40, message = "Weekly hours cannot be above 40.")
	private Integer weeklyHours;
	
	//@Column
	@NotNull(message = "Year must not be null.")
	@Enumerated(EnumType.STRING)
	private EnumSchoolYear year;
	
	//@Column
	@Enumerated(EnumType.STRING)
	@NotNull(message = "Semester must not be null.")
	private EnumSemester semester;
	
	@OneToMany(mappedBy = "course", cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonBackReference
	private List<TeacherCourseEntity> teacherCourse;
	
	@Column
	private Boolean deleted;
	
	@Version
	private Integer version;

	public CourseEntity() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getWeeklyHours() {
		return weeklyHours;
	}

	public void setWeeklyHours(Integer weeklyHours) {
		this.weeklyHours = weeklyHours;
	}

	public EnumSchoolYear getYear() {
		return year;
	}

	public void setYear(EnumSchoolYear year) {
		this.year = year;
	}

	public EnumSemester getSemester() {
		return semester;
	}

	public void setSemester(EnumSemester semester) {
		this.semester = semester;
	}

	public List<TeacherCourseEntity> getTeacherCourse() {
		return teacherCourse;
	}

	public void setTeacherCourse(List<TeacherCourseEntity> teacherCourse) {
		this.teacherCourse = teacherCourse;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
