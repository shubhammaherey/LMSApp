package com.lmsapp.lms.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lmsapp.lms.api.SmsSender;
import com.lmsapp.lms.dto.AdminLoginDto;
import com.lmsapp.lms.dto.EnquiryDto;

import com.lmsapp.lms.dto.StudentInfoDto;
import com.lmsapp.lms.model.AdminLogin;
import com.lmsapp.lms.model.Enquiry;
import com.lmsapp.lms.model.StudentInfo;
import com.lmsapp.lms.service.AdminLoginRepository;
import com.lmsapp.lms.service.EnquiryRepository;
import com.lmsapp.lms.service.StudentInfoRepository;

import java.io.InputStream;
import java.util.Iterator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {

	@Autowired
	EnquiryRepository erepo;
	@Autowired
	StudentInfoRepository srepo;
	@Autowired
	AdminLoginRepository adrepo;

	@GetMapping("/")
	public String showIndex() {
		return "index";
	}

	@GetMapping("/aboutus")
	public String showAboutUs() {

		return "aboutus";
	}

	@GetMapping("/registration")
	public String showRegistration(Model model) {
		StudentInfoDto dto = new StudentInfoDto();
		model.addAttribute("dto", dto);
		return "registration";
	}

	@GetMapping("/stulogin")
	public String showStuLogin(Model model) {
		StudentInfoDto dto = new StudentInfoDto();
		model.addAttribute("dto", dto);
		return "stulogin";
	}

	@GetMapping("/adminlogin")
	public String showAdminLogin(Model model) {
		AdminLoginDto dto=new AdminLoginDto();
		model.addAttribute("dto", dto);
		return "adminlogin";
	}

	@GetMapping("/contactus")
	public String showContactUs(Model model) {
		EnquiryDto dto = new EnquiryDto();
		model.addAttribute("dto", dto);
		return "contactus";
	}

	@PostMapping("/contactus")
	public String saveEnquiry(@ModelAttribute EnquiryDto dto, RedirectAttributes attrib) {
		Enquiry e = new Enquiry();
		e.setName(dto.getName());
		e.setContactno(dto.getContactno());
		e.setEmailaddress(dto.getEmailaddress());
		e.setEnquirytext(dto.getEnquirytext());
		Date dt = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String posteddate = df.format(dt);
		e.setPosteddate(posteddate);
		erepo.save(e);
		SmsSender ss=new SmsSender();
		ss.sendSms(dto.getContactno());
		attrib.addFlashAttribute("msg", "Enquiry is saved");
		return "redirect:/contactus";
	}

	@PostMapping("/registration")
	public String doRegistration(@ModelAttribute StudentInfoDto dto, RedirectAttributes attrib) {
		StudentInfo s = new StudentInfo();
		s.setEnrollmentno(dto.getEnrollmentno());
		s.setName(dto.getName());
		s.setFname(dto.getFname());
		s.setMname(dto.getMname());
		s.setGender(dto.getGender());
		s.setAddress(dto.getAddress());
		s.setProgram(dto.getProgram());
		s.setBranch(dto.getBranch());
		s.setYear(dto.getYear());
		s.setContactno(dto.getContactno());
		s.setEmailaddress(dto.getEmailaddress());
		s.setPassword(dto.getPassword());
		Date dt = new Date();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String regdate = df.format(dt);
		s.setRegdate(regdate);
		srepo.save(s);
		attrib.addFlashAttribute("msg", "Registration is done");
		return "redirect:/registration";
	}

	@PostMapping("/stulogin")
	public String validateStudent(@ModelAttribute StudentInfoDto dto, RedirectAttributes attrib,HttpSession session)
	{
		try { 
			StudentInfo s=srepo.getById(dto.getEnrollmentno());
			if(s.getPassword().equals(dto.getPassword())) {
				//attrib.addFlashAttribute("msg", "Valid User");
				session.setAttribute("studentid", s.getEnrollmentno());
				return "redirect:/student/studenthome";
			}
			else {
				attrib.addFlashAttribute("msg", "Invalid User");
				return "redirect:/stulogin";
			}
			
		}
		catch(EntityNotFoundException ex) {
			attrib.addFlashAttribute("msg","Student record does not exist");
			return "redirect:/stulogin";
		}
	}
	
	@PostMapping("/adminlogin")
	public String validateAdmin(@ModelAttribute AdminLoginDto dto, RedirectAttributes attrib, HttpSession session)
	{
		try {
			AdminLogin ad=adrepo.getById(dto.getUserid());
			if(ad.getPassword().equals(dto.getPassword()))
			{
               //attrib.addFlashAttribute("msg", "Valid User");
				session.setAttribute("adminid", dto.getUserid());
				return "redirect:/admin/adminhome";
			}
			else {
				attrib.addFlashAttribute("msg", "Invalid User");
				return "redirect:/adminlogin";
			}
		}
		catch(EntityNotFoundException ex) {
			attrib.addFlashAttribute("msg", "Adminid is not found");
			return "redirect:/adminlogin";
		}
	}

}
