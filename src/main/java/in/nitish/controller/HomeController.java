package in.nitish.controller;

import javax.naming.Binding;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import in.nitish.entity.User;
import in.nitish.helper.Message;
import in.nitish.repository.UserRepository;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder PasswordEncoder;
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		User user=new User();
		user.setName("Nitish");
		user.setEmail("nitish321@gmail.com");
		userRepository.save(user);
		return "Working";
	}
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home-SmartContactManager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About-SmartContactManager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Signup-SmartContactManager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
			@RequestParam(value="agreement",defaultValue="false") boolean agreement,
			Model model, HttpSession session) {
		
		try {
			
			if(!agreement)
			{
				System.out.println("You have not agreed T&C");
				throw new Exception("You have not agreed Terms and Condition ");
			}
			
			if (bindingResult.hasErrors()) {
				
				System.out.println("Error " +bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setRole("ROLE_USER");
			user.setPassword(PasswordEncoder.encode(user.getPassword()));
			
			User result = userRepository.save(user);
			model.addAttribute("user",new User());
			System.out.println("Agreement : " +agreement);
			System.out.println("User : " +user);
			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong !!"+e.getMessage(), "alert-danger"));
			return "signup";
		}	
	}	
          	// For Login 
			@GetMapping("/signin")
			public String customLogin( Model model) {
				
				model.addAttribute("title","Login page");
				return "login";
			}
}
