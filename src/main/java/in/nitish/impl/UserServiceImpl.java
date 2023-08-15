package in.nitish.impl;

import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.header.Header;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.nitish.entity.User;
import in.nitish.repository.UserRepository;
import in.nitish.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Value("${json.data.url}")
	private String url;
	
	@Autowired
	private Environment environment;
	@Autowired
	UserRepository userRepository;

	@Override
	public User getUserByEmail(String email) {
		System.out.println("Inside getUserByEmail Method ");
		Optional<User> user=Optional.of(userRepository.getUserByUserName(email));
		if (user.isPresent()) {
			User userDetails=user.get();
			System.out.println("userDetails : " +userDetails.getEmail());
			RestTemplate restTemplate=new RestTemplate();
			
//			Way to get the data from application.properties file
			System.out.println("URL " +url);
			String propValue = environment.getProperty("json.data.url");
			System.out.println("propValue" +propValue);
			
			HttpHeaders headers=new HttpHeaders();
			
//			    String notEncoded = user + ":" + password;
//			    String encodedAuth = Base64.getEncoder().encodeToString(notEncoded.getBytes());
//				headers.add("Authorization", "Basic " + encodedAuth);			
//			headers.set("Authorization", "Basic XXXXXXXXXXXXXXXX=");
//			HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
			    headers.setContentType(MediaType.APPLICATION_JSON);
			    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<String> entity=new HttpEntity<String>(headers);
			ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			if (exchange.getStatusCodeValue()==200) {
				
				System.out.println("Exchange " +exchange);
				String body = exchange.getBody();
				System.out.println("body " +body);
				
			}
			
			
//			try {
//				System.out.println("Call write csv method");
//				exportUserDetailsToCsv(userDetails);
//			} catch (IOException e) {
//				
//				e.printStackTrace();
//			}
//		return userDetails;
		}
		return null;
	}

//	private void exportUserDetailsToCsv(User userDetails) throws IOException {
//		
//		System.out.println("csvMethod");
//		CSVWriter writer=new CSVWriter(new FileWriter("C:\\Users\\NKSK\\Desktop\\New SpringBoot\\6-SmartContactManager-1\\src\\main\\resources"));
//		Object[] data= {userDetails.getId(),userDetails.getName()};
//		writer.writeNext((String[]) data);
//		writer.close();
//	}

	@Override
	public String exportUserDataToCsv(Writer writer) {
		
		System.out.println("Inside CSV Method");
		List<User> users = userRepository.findAll();
		
		try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT))
			{
			
			csvPrinter.printRecord("ID","NAME","EMAIL");
			for(User user:users)
			{
				csvPrinter.printRecord(user.getId(),user.getName(),user.getEmail());
				System.out.println("SUCCESSFUL");
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "True";
		
		
	}
	


}
