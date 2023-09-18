package in.nitish.service;

import in.nitish.pojo.EmailOTP;

public interface NotificationService {

	String verifyOtpEmail(EmailOTP emailOTP);

	
}
