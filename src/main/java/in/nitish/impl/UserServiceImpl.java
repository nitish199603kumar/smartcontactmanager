package in.nitish.impl;

import java.io.Writer;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.nitish.entity.User;
import in.nitish.repository.UserRepository;
import in.nitish.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepository;

	@Override
	public User getUserByEmail(String email) {
		Optional<User> user=Optional.of(userRepository.getUserByUserName(email));
		if (user.isPresent()) {
			User userDetails=user.get();
			System.out.println("userDetails : " +userDetails.getEmail());
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
