package com.lmsapp.lms.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.lmsapp.lms.dto.ResponseDto;
import com.lmsapp.lms.model.Material;
import com.lmsapp.lms.model.Response;
import com.lmsapp.lms.model.StudentInfo;
import com.lmsapp.lms.service.MaterialRepository;
import com.lmsapp.lms.service.ResponseRepository;
import com.lmsapp.lms.service.StudentInfoRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController {
	
	@Autowired
	StudentInfoRepository srepo;
	@Autowired
	ResponseRepository rrepo;
	@Autowired
	MaterialRepository mrepo;
	
	@GetMapping("/studenthome")
	public String showStudentHome(HttpSession session, HttpServletResponse response) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentid") != null) {
				return "/student/studenthome";
			} else {
				return "redirect:/stulogin";
			}

		} catch (Exception e) {
			return "redirect:/stulogin";
		}
	}
	@GetMapping("stulogout")
	public String stuLogout(HttpSession session) {
		session.invalidate();
		return "redirect:/stulogin";
	}
	
	@GetMapping("/changepassword")
	public String showChangePassword(HttpSession session, HttpServletResponse response) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentid") != null) {
				return "/student/changepassword";
			} else {
				return "redirect:/stulogin";
			}

		} catch (Exception e) {
			return "redirect:/stulogin";
		}
	}
	
	@PostMapping("/changepassword")
	public String changePassword(HttpSession session, HttpServletResponse response, HttpServletRequest request, RedirectAttributes attrib) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentid") != null) {
				String oldpassword=request.getParameter("oldpassword");
				String newpassword=request.getParameter("newpassword");
				String confirmpassword=request.getParameter("confirmpassword");
				if(!newpassword.equals(confirmpassword))
				{
					attrib.addFlashAttribute("msg", "Newpassword and confirmpassword are not matched");
					return "redirect:/student/changepassword";
				}
				StudentInfo s=srepo.getById(Long.parseLong(session.getAttribute("studentid").toString()));
				if(!oldpassword.equals(s.getPassword()))
				{
					attrib.addFlashAttribute("msg", "Oldpassword is not matched");
					return "redirect:/student/changepassword";
				}
				else {					
					s.setPassword(newpassword);
					srepo.save(s);
					return "redirect:/student/stulogout";
				}
				
			} else {
				return "redirect:/stulogin";
			}

		} catch (Exception e) {
			return "redirect:/stulogin";
		}
	}
	
	@GetMapping("/giveresponse")
	public String showGiveResponse(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentid") != null) {
				ResponseDto dto=new ResponseDto();
				model.addAttribute("dto", dto);
				return "/student/giveresponse";
			} else {
				return "redirect:/stulogin";
			}

		} catch (Exception e) {
			return "redirect:/stulogin";
		}
	}
	
	@PostMapping("/giveresponse")
	public String giveResponse(HttpSession session, HttpServletResponse response, @ModelAttribute ResponseDto dto, RedirectAttributes attrib) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentid") != null) {
				StudentInfo s=srepo.getById(Long.parseLong(session.getAttribute("studentid").toString()));
				Response res=new Response();
				res.setEnrollmentno(s.getEnrollmentno());
				res.setName(s.getName());
				res.setProgram(s.getProgram());
				res.setBranch(s.getBranch());
				res.setYear(s.getYear());
				res.setResponsetype(dto.getResponsetype());
				res.setResponsetext(dto.getResponsetext());
				Date dt=new Date();
				SimpleDateFormat df=new SimpleDateFormat("dd/MM/yyyy");
				String posteddate=df.format(dt);
				res.setPosteddate(posteddate);
				rrepo.save(res);
				attrib.addFlashAttribute("msg", "Response is submitted");
				return "redirect:/student/giveresponse";
			} else {
				return "redirect:/stulogin";
			}

		} catch (Exception e) {
			return "redirect:/stulogin";
		}
	}
	@GetMapping("/viewstudymaterial")
	public String showStudyMaterial(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentid") != null) {
				StudentInfo s=srepo.findById(Long.parseLong(session.getAttribute("studentid").toString())).get();
				List<Material> smat=mrepo.getMaterial(s.getProgram(),s.getBranch(),s.getYear(),"smat");
				model.addAttribute("smat", smat);
				return "/student/viewstudymaterial";
			} else {
				return "redirect:/stulogin";
			}

		} catch (Exception e) {
			return "redirect:/stulogin";
		}
	}
	@GetMapping("/viewassignment")
	public String showAssignment(HttpSession session, HttpServletResponse response, Model model) {
		try {
			response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
			if (session.getAttribute("studentid") != null) {
				StudentInfo s=srepo.findById(Long.parseLong(session.getAttribute("studentid").toString())).get();
				List<Material> assign=mrepo.getMaterial(s.getProgram(), s.getBranch(), s.getYear(), "assign");
				model.addAttribute("assign", assign);
				return "/student/viewassignment";
			} else {
				return "redirect:/stulogin";
			}

		} catch (Exception e) {
			return "redirect:/stulogin";
		}
	}
}
