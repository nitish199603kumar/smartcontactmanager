package in.nitish.impl;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.nitish.invoker.NotificaionServiceInvoker;
import in.nitish.pojo.EmailOTP;

@Service
public class NotificationServiceInvokerImpl implements NotificaionServiceInvoker {

	@Override
	public String invokeEmail(String url, EmailOTP emailOTP) {
		
		RestTemplate restTemplate=new RestTemplate();
		String emailRespone = restTemplate.postForObject(url, emailOTP, String.class);
		
		System.out.println("Email Api Response " +emailRespone);
		
		
		return emailRespone;
	}

}
