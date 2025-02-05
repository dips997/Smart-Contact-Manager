package com.smart.controller;

import jakarta.validation.Valid;

import org.attoparser.config.ParseConfiguration.UniqueRootElementPresence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@RequestMapping("/")
	public String Home(Model m) {	
		m.addAttribute("title", "Home - Smart Conatct Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String About(Model m) {
		m.addAttribute("title", "About - Smart Conatct Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String SignUp(Model m) {
		m.addAttribute("title", "Register - Smart Conatct Manager");
		m.addAttribute("user",new User());
		return "signup";
	}
	
	@RequestMapping(value= "/do_register", method = RequestMethod.POST)
	public String DoRegister(@Valid @ModelAttribute("user") User user, BindingResult result1, @RequestParam(value="agreement", defaultValue = "false") boolean agreement,Model m,HttpSession session) {
		
		
		try {

			if(!agreement) {
				System.out.println("you have not agreed terms and condition");
				throw new Exception("you have not agreed terms and condition");
			}
			
			if(result1.hasErrors()) {
				System.out.println("Error " + result1.toString());
				m.addAttribute("user",user);
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement " + agreement);
			System.out.println("User " + user );
			
			User result = this.userRepository.save(user);
			m.addAttribute("user", result);
			System.out.println(result);
			System.out.println(result.getAbout());
			session.setAttribute("message", new Message("Successfully Registered..!!","alert-success"));
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong..!!" + e.getMessage(),"alert-danger"));
			return "signup";
		}		
	}

	@GetMapping("/signin")
	public String LoginPage(Model m) {
		m.addAttribute("title","Login Page");
		return "login";
	}
	
}
