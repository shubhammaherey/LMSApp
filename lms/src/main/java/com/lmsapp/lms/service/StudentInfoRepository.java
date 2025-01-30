package com.lmsapp.lms.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lmsapp.lms.model.StudentInfo;

public interface StudentInfoRepository extends JpaRepository<StudentInfo, Long> {

}
