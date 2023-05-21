package in.nitish.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import in.nitish.entity.User;
import in.nitish.repository.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User userByUserName = userRepository.getUserByUserName(username);
		if(userByUserName==null)
		{
			throw new UsernameNotFoundException("User not found !!");
		}
		
		CustomerUserDetails customerUserDetails=new CustomerUserDetails(userByUserName);
		
		return customerUserDetails ;
	}

}
