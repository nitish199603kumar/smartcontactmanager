package in.nitish.controller;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.security.Principal;
import java.util.List;

import org.hibernate.stat.SecondLevelCacheStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import in.nitish.entity.Contact;
import in.nitish.entity.User;
import in.nitish.repository.ContactRepository;
import in.nitish.repository.UserRepository;

@RestController
public class SearchController {
	private final static Logger LOGGER=LoggerFactory.getLogger(SearchController.class);
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ContactRepository contactRepository;

	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		
		LOGGER.info("search keyword from front-end side {}",query);
		User user = this.userRepository.getUserByUserName(principal.getName());
		List<Contact> findByNameContainingAndUser = this.contactRepository.findByNameContainingAndUser(query, user);
		
		return new ResponseEntity<>(findByNameContainingAndUser,HttpStatus.OK);
		
	}
}
