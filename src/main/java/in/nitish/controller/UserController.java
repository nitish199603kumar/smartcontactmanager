package in.nitish.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.result.HeaderResultMatchers;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	   	
//	   	this.userService.getUserByEmail(userName);
	   	
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
			contact.setImage("contact.png");
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
		Pageable pageable = PageRequest.of(page, 2);
//		List<Contact> contacts=this.contactRepository.getContactsByUserId(userDetails.getId());
		Page<Contact> contacts=this.contactRepository.getContactsByUserId(userDetails.getId(),pageable);
		LOGGER.info("Get All Contacts {}  by userId {}",contacts,userDetails.getId());
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}
	
//	Show particular Contact 
	@RequestMapping("/{cId}/contact")
	public String showParticularContact(@PathVariable("cId") Integer cId,Model model,Principal principal ) {
		LOGGER.info("Contact Id is : {}",cId);
		
		Optional<Contact> contactDetails = this.contactRepository.findById(cId);
		Contact contact = contactDetails.get();
		String userName = principal.getName();
		User userByUserName = this.userRepository.getUserByUserName(userName);
		if(userByUserName.getId()==contact.getUser().getId()) //If having only one line statement,we can leave{} bracket
		{	
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		
		return "normal/show_Particular_Contact";
	}
	
	@GetMapping("/deleteUser/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId,Principal principal,HttpSession session) {
		System.out.println("Deletion process started...");
//		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
//		Contact contact = contactOptional.get();
		Contact contact = this.contactRepository.findById(cId).get();
		System.out.println("Contact Details - "+ contact + "contact Id" +contact.getCId() + "Inside Contact user id - " +contact.getUser().getId());
//		Sometimes contact will be deleted,But if not deleted due to connectivity of user ,In this case 
//		we will unlink the connectivity of both the table
//		contact.setUser(null);
		
		String userName = principal.getName();
		User userDetails = this.userRepository.getUserByUserName(userName);
		System.out.println("UserDeails - " +userDetails + " user id - " +userDetails.getId());
//		System.out.println("User id - " +userDetails.getId() + "contact id - " + contact.getUser().getId() );
//		int userId = userDetails.getId();
//		System.out.println("userId - " +userId);
//		int contactUserId = contact.getUser().getId();
//		System.out.println("contactUserId - " +userId);
//		System.out.println("user id - " +userId +" contactUserId - " +contactUserId);
//		if (userId==contactUserId) {
//			System.out.println("inside if");
//		}
		if (userDetails.getId()==contact.getUser().getId()) {
//			contact.setUser(null);
//			this.contactRepository.delete(contact);  //Here data will be deleted from user-interface,But not deleted from db
			this.contactRepository.deleteContactById(cId);   //Here data will be deleted from both(UI-DB)
			System.out.println("User Deleted Successfully");
			session.setAttribute("message", new Message("Contact Deleted Successfully...", "success"));
		}
		
		
		return "redirect:/user/showcontacts/0";
	}
	
//	open update from handler    // this url called while clicking update button
	@PostMapping("/updateContact/{cId}")
	public String updateForm(@PathVariable("cId") Integer cId, Model model) {
		
		model.addAttribute("title", "update contact page");
		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);
		return "/normal/update_form";
	}
	
	@RequestMapping(value="/process_update_form" ,method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,
			HttpSession session,Model model,Principal principal ) {
		
		System.out.println("Updated Contact Name - " + contact.getName());
		System.out.println("ID - " + contact.getCId());   //here contact id is coming from front-end side
		// old contact details
		Contact oldContactDetails = this.contactRepository.findById(contact.getCId()).get();
		
		try {
			
				if(!file.isEmpty()) {
					
//					delete old photo
					File deleteFilePath = new ClassPathResource("static/image").getFile();
					File file2=new File(deleteFilePath, oldContactDetails.getImage());
					file2.delete();
					
//					update new photo
					File saveFilePath = new ClassPathResource("static/image").getFile();
					
					java.nio.file.Path path = Paths.get(saveFilePath.getAbsolutePath()+File.separator+file.getOriginalFilename());
//					static/image/screenshot.png   ===path
					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					contact.setImage(file.getOriginalFilename());
					LOGGER.info("Image Uploaded Successfully");
					
					
				}else {
					contact.setImage(oldContactDetails.getImage());
				}
				
			User userData= this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(userData);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Your contact is updated successfully !!", "success"));
			System.out.println("contact updated successfully !!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/user/"+contact.getCId()+"/contact";
	}
	
}
