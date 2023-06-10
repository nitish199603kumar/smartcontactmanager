package in.nitish.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.nitish.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

//	@Query("from Contact as c where c.user.id=:userId")
//	List<Contact> getContactsByUserId(int userId);
	
//	Pageable having two information current Page and how many page do you want to show,Give to page object
	
	@Query("from Contact as c where c.user.id=:userId")
	Page<Contact> getContactsByUserId(@Param("userId") int userId,Pageable pageable);
}
