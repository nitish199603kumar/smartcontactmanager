package in.nitish.controller;

import javax.servlet.http.HttpSession;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.nitish.helper.RandomNumGenerate;
import in.nitish.impl.NotificationServiceImpl;
import in.nitish.pojo.EmailOTP;
import in.nitish.service.NotificationService;

@Controller
public class ForgotPassword {

	private final static org.jboss.logging.Logger LOGGER=LoggerFactory.logger(ForgotPassword.class);
	
	
	
	@Autowired
	private RandomNumGenerate randomNumberGenerate;
	
	@Autowired
	NotificationService notificationService;
	
	
	//After Clicking forgot password button to redirect 
	@RequestMapping("/forgot-password")
	public String forgotPasswordOpenEmailForm() {
		System.out.println("redirecting to fogot password page where we will enter our email");
		return "/forgot-password";
	}
	
	
	@PostMapping("/send-otp")
	public String otpForm(@RequestParam("email") String email,HttpSession session) {
		System.out.println("Email for forgotPassword : " +email);
		//OTP generation
		int otp = randomNumberGenerate.generaterandomNumber(100000);
		System.out.println("OTP : " +otp);
		
		EmailOTP emailOTP=new EmailOTP();
		emailOTP.setRecipient(email);
		emailOTP.setSubject("SmartcontactManager OTP Verification");
		emailOTP.setMsgBody(String.valueOf(otp));
		
		String otpStatus=this.notificationService.verifyOtpEmail(emailOTP);
		if(otpStatus !=null) {
			System.out.println("OTP Successful Message " +otpStatus);
			
			//Here we are saving otp in session
			session.setAttribute("savedOtp", otp);
			session.setAttribute("email", email);
			return "verify-otp";
		}else {
		
			session.setAttribute("message", "Incorrect Email id...Please check ypur email Id !!");
			return "forgot-password";
		}
		
		
	}
	
	//verify-otp
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") Integer otp,HttpSession session){
		
		int savedOtp = (int) session.getAttribute("savedOtp");
		String email = (String) session.getAttribute("email");
		if(savedOtp==otp) {
			System.out.println("savedOtp and verify otp matched");
			return "change-password";
		}else {
			
			session.setAttribute("message", "Your OTP did not matched");
			return "verify-otp";
		}
		
		
	
	}
	
	
}
