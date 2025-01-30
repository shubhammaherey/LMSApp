package com.lmsapp.lms.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lmsapp.lms.model.AdminLogin;

public interface AdminLoginRepository extends JpaRepository<AdminLogin, String> {

}
