package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.smart.dao.ContactRepository;
import com.smart.dao.MyOrderRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrders;
import com.smart.entities.User;
import com.smart.helper.Message;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.razorpay.*;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private MyOrderRepository myOrderRepository;
	
	@ModelAttribute
	public void commonData(Model m, Principal principal) {
		String username = principal.getName();
		System.out.println(username);
		User user = userRepository.getUserByUserName(username);	
		System.out.println("USER" + user);
		m.addAttribute("user" ,user);
	}
	
	
	//User DashBoard
	@RequestMapping("/index")
	public String dashboard(Model m) {
		m.addAttribute("title","User-DashBoard");
		return "normal/user_dashboard";
	}
	
	//add contact handler
	@RequestMapping("/add-contact")
	public String addContact(Model m) {
		m.addAttribute("title","Add-Contact");
		m.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	
	//Handling the form Adding the form
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact,BindingResult result,
								@RequestParam("Profileimage") MultipartFile file,
								Principal principal,
								Model m,
								HttpSession session) {
		
			
		   try {   
			   
			   
			   
		   String name = principal.getName();
		   User user = userRepository.getUserByUserName(name);
		   
		   if(result.hasErrors()) {
			   System.out.println("Error " + result.toString());
			   m.addAttribute("contact",contact);
		   }
		   
			/*
			 * if(3>2) { throw new Exception(); }
			 */
		   if(file.isEmpty()) {
			   System.out.println("File is mandotary..please choose..!");
			   contact.setImage("contact.png");
			  
		   }else {
			   
			  File saveFile = new ClassPathResource("static/img").getFile();
			  
			  Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			  
			  Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			  
			  System.out.println("file uploaded succesfulyy");

			   System.out.println(name);
			   contact.setImage(file.getOriginalFilename());
		   }
		  
		   contact.setUser(user); 
		   user.getContacts().add(contact);
		   userRepository.save(user);
		   
		  System.out.println("DATA " + contact);
		  System.out.println("Added to Data Base");
		
		  session.setAttribute("message", new Message("contact Added Successfully!!Add More..","success"));
		  
		  
		   }catch (Exception e) {
			System.out.println("ERROR " + e.getMessage());
			 session.setAttribute("message", new Message("Something went wrong..!!Try Again..","danger"));
		}

		return "normal/add_contact_form";
	}
	
	//Show All the contact
	@GetMapping("/show-contact/{page}")
	public String  showContacts(@PathVariable("page") Integer page ,Model m,Principal principal) {
		
		m.addAttribute("title", "Show-Contacts");
		
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		
		//currentPage-page
		//contact Per page - 5
		Pageable pageable = PageRequest.of(page, 5);
		
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		
		m.addAttribute("contacts",contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages",contacts.getTotalPages());
		
		return "normal/show-contact";
	}
	
	//specific show contact Handler 
	@RequestMapping("/{cId}/contact")
	public String ShowContactDeails(@PathVariable("cId") Integer cId,Model m,Principal principal) {
		System.out.println("cId " + cId);
		m.addAttribute("title","Show Contact Details");
		
		Optional<Contact> Id = contactRepository.findById(cId);
		System.out.println(Id);
		Contact contact = Id.get();
		
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		
		
		if(user.getId() == contact.getUser().getId()){
		m.addAttribute("contact", contact);
		m.addAttribute("title", contact.getName());
		}
		
		System.out.println(contact.getImage());
		return "normal/contact-details";
	}
	
	
	// delete contact handler
	@GetMapping("/delete/{cId}")
	public String DeleteContact(@PathVariable("cId") Integer cId,Model m,Principal principal,HttpSession session) {
		
		String name = principal.getName();
		User user = this.userRepository.getUserByUserName(name);
		
		Contact contact = this.contactRepository.findById(cId).get();
		

		user.getContacts().remove(contact);
		this.userRepository.save(user);
		
		
		session.setAttribute("message", new Message("Contact Delete Successfully", "success"));
		
		System.out.println("sucesssFuly Deleted");
		return "redirect:/user/show-contact/0";
	}
	
	//open update the Handler form
	@PostMapping("/update-contact/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId,Principal principal,Model m) {
		
		String name = principal.getName();
		User user = userRepository.getUserByUserName(name);
		
		Contact contact = contactRepository.findById(cId).get();
		
		m.addAttribute("contact", contact);
		
		return "normal/update_form";
	}
	
	@PostMapping("/procces-update")
	public String processUpdate(@ModelAttribute Contact contact,
								@RequestParam("Profileimage") MultipartFile file,
								Principal principal,
								Model m,
								HttpSession session) {
		
		try {
			
			// oldContactImage
			 Contact oldContactDetail = contactRepository.findById(contact.getcId()).get();
			 
			
			if(!file.isEmpty()) {
				
				//delete old photo
				
					File deleteFile = new ClassPathResource("static/img").getFile(); 
					File file1 = new File(deleteFile, oldContactDetail.getImage());
					file1.delete();
				
				//update new photo
				
				  
				  File saveFile = new ClassPathResource("static/img").getFile();
				  
				  Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				  
				  Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				  
				  contact.setImage(file.getOriginalFilename());
				  
				
			}else {
				contact.setImage(oldContactDetail.getImage());
			}
			
			User user = userRepository.getUserByUserName(principal.getName());
			
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Update SucceFully..!!", "success") );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	
	@GetMapping("/profile")
	public String profile(Model m) {
		m.addAttribute("title","Profile");
		return "normal/profile";
	}
	
	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, 
								@RequestParam("newPassword") String newPassword,
								Principal principal,
								HttpSession session) {
		
		System.out.println("OLD PASSWORD " + oldPassword );
		System.out.println("NEW PASSWORD " + newPassword);
		
		String name = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(name);
		System.out.println(currentUser);
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			
			//change the password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);	
			session.setAttribute("message", new Message("Your password is successfully changed.	.!!", "success"));
		}else {
			session.setAttribute("message", new Message("Please Enter Correct old Password..!!", "danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
	
	
	//creating order for Payment
	
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String, Object> data,Principal principal) throws Exception {
	
		System.out.println(data);
		int amt = Integer.parseInt(data.get("amount").toString());
		
		var client = new RazorpayClient("rzp_test_cgD4xVgWl9DHlT", "rBeIm2ogxdbnvlataJ1rPwUX");
		
		JSONObject ob = new JSONObject();
		ob.put("amount",amt*100);
		ob.put("currency","INR");
		ob.put("receipt", "txn_235425");
		
		//creating new Order
		Order order = client.orders.create(ob);
		System.out.println(order);
		
		//save the order to the database
		MyOrders myOrders = new MyOrders();
		
		myOrders.setAmount(order.get("amount")+"");
		myOrders.setOrderId(order.get("id"));
		myOrders.setPaymentId(null);
		myOrders.setStatus("created");
		myOrders.setReceipt(order.get("receipt"));
		myOrders.setUser(this.userRepository.getUserByUserName(principal.getName()));
		
		this.myOrderRepository.save(myOrders);
		
		//if you want you can save this to your  data...
		return order.toString();
	}
	
	@PostMapping("/update_order")
	public ResponseEntity<?> UpdateOrder(@RequestBody Map<String, Object> data) {
		
		MyOrders myorder = this.myOrderRepository.findByOrderId(data.get("order_id").toString());
		
		myorder.setPaymentId(data.get("payment_id").toString());
		myorder.setStatus(data.get("status").toString());
		
		this.myOrderRepository.save(myorder);
		
		System.out.println(data);
		
		return ResponseEntity.ok(Map.of("msg","updated"));
	}

}
