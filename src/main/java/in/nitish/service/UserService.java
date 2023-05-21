package in.nitish.service;

import java.io.Writer;

import antlr.collections.List;
import in.nitish.entity.User;

public interface UserService {
	
	
	User getUserByEmail(String email);
	
	String exportUserDataToCsv(Writer writer);
	

}
