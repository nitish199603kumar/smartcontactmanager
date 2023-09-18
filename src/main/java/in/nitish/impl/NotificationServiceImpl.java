package in.nitish.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import in.nitish.invoker.NotificaionServiceInvoker;
import in.nitish.pojo.EmailOTP;
import in.nitish.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Value("${otp.email.baseurl}")
	private String otpEmailBaseUrl;
	
	@Autowired
	NotificaionServiceInvoker notificaionServiceInvoker;
	
	public String verifyOtpEmail(EmailOTP emailOTP) {
		System.out.println("Email Base Url " + otpEmailBaseUrl);
		StringBuilder builder=new StringBuilder(otpEmailBaseUrl);
		builder.append("/verify-otp");
		System.out.println("Verify Otp URL "+builder.toString());
		
		String otpEmailResponse=this.notificaionServiceInvoker.invokeEmail(builder.toString(), emailOTP);
		
		return otpEmailResponse;
	}

}
