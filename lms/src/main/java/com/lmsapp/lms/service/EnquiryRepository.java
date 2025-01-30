package com.lmsapp.lms.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lmsapp.lms.model.Enquiry;

public interface EnquiryRepository extends JpaRepository<Enquiry, Integer> {

}
