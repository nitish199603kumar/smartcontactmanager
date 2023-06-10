package in.nitish.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.jayway.jsonpath.internal.Path;

import in.nitish.entity.Contact;
import in.nitish.entity.User;
import in.nitish.helper.Message;
import in.nitish.repository.ContactRepository;
import in.nitish.repository.UserRepository;
import in.nitish.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private final static Logger LOGGER=LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	UserService userService;
	
	@RequestMapping("/normal_user")
	public String dashboard(Model model,Principal principal,HttpServletResponse servletResponse) throws IOException {
		
	   	String userName=principal.getName();
	   	System.out.println("UserName : " +userName);
	   	
	   	
	   	User user=userRepository.getUserByUserName(userName);
	   	System.out.println("user : " +user);
	   	
	   	userService.getUserByEmail(userName);
	   	
//	   	servletResponse.setContentType("text/csv");
//	   	servletResponse.addHeader("Content-Disposition", "attachment; filename=\"C:\\Users\\NKSK\\Downloads\\abc\\a.csv\""); 	
//	   	System.out.println("call csv method");
//	   	String a=userService.exportUserDataToCsv(servletResponse.getWriter());
//	   	System.out.println("End of CSVFILE" +a);
	   	
	   	model.addAttribute("title","User Dashboard");
	   	model.addAttribute("user",user);
		return "normal/user_dashboard";
	}
	
//	Add contact
	
	@GetMapping("/addcontact")
	public String addContact(Model model,Principal principal) {
		
		String userName=principal.getName();
	   	System.out.println("UserName : " +userName);
	   	
	   	
	   	User user=userRepository.getUserByUserName(userName);
	   	System.out.println("user : " +user);
	   	
//	   	userService.getUserByEmail(userName);
	   	
	   	model.addAttribute("user",user);
		
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		System.out.println("==================");
		return "normal/addcontact";
	}
	
	
	@PostMapping("/process-contact")
	public String processContact(
			
			@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file,
			Principal principal,
			HttpSession session
			) {		
		try {
		LOGGER.info("Contact Form Data : {}", contact);	
		String email = principal.getName();
		User user = userRepository.getUserByUserName(email);
		
		if(file.isEmpty()) {
			LOGGER.info("Image Not Found");
		}else {
			
			contact.setImage(file.getOriginalFilename());
			File saveFilePath = new ClassPathResource("static/image").getFile();
			
			java.nio.file.Path path = Paths.get(saveFilePath.getAbsolutePath()+File.separator+file.getOriginalFilename());
//			static/image/screenshot.png   ===path
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			LOGGER.info("Image Uploaded Successfully");
//			If you want to show any msg for short period of time you can use HttpSession 
			session.setAttribute("message", new Message("Your contact is added successfully !! Add more ...", "success"));
		}
		contact.setUser(user);
		user.getContact().add(contact);
		userRepository.save(user);
		LOGGER.info("Contact Added Successfully {}", user);
		}catch (Exception e) {
			LOGGER.error("exception in catch block ", e.getMessage() );
//			LOGGER.error("exception in catch block1 ", e.printStackTrace());
			session.setAttribute("message", new Message("Something went wrong !! try again...", "danger"));
		}
		return "normal/test";
	}
	
	//	show contact
	//  per Page=5[n]
	// current Page=0
	
	
	@GetMapping("/showcontacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal) {
		model.addAttribute("title","View Contact");
		
		String userName = principal.getName();
		User userDetails = this.userRepository.getUserByUserName(userName);
		List<Contact> contact = userDetails.getContact();     //get the contact
//		model.addAttribute("contact",contact);
		Pageable pageable = PageRequest.of(page, 1);
//		List<Contact> contacts=this.contactRepository.getContactsByUserId(userDetails.getId());
		Page<Contact> contacts=this.contactRepository.getContactsByUserId(userDetails.getId(),pageable);
		LOGGER.info("Get All Contacts {}  by userId {}",contacts,userDetails.getId());
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}
	
	
}
