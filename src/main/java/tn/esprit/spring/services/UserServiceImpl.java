package tn.esprit.spring.services;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tn.esprit.spring.entities.User;
import tn.esprit.spring.repository.UserRepository;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	UserRepository userRepository;



	private static final Logger l = LogManager.getLogger(UserServiceImpl.class);

	@Override
	public List<User> retrieveAllUsers() { 

		return null;
	}


	@Override
	public User addUser(User u) {

		User utilisateur = null; 

		try {
			l.info("Adding user: {}", u);
			utilisateur = userRepository.save(u); 
			l.info("User added successfully with id: {}", utilisateur.getId());
		} catch (Exception e) {
			l.error("Error in addUser(): ", e);
		}

		return utilisateur; 
	}

	@Override 
	public User updateUser(User u) {

		User userUpdated = null; 
		User u_saved = null; 

		
		try {
			l.info("Updating user: {}", u);
			userUpdated = userRepository.save(u); 
			l.info("User updated successfully with id: {}", userUpdated.getId());
		} catch (Exception e) {
			l.error("Error in updateUser(): ", e);
		}

		return userUpdated; 
	}

	@Override
	public void deleteUser(String id) {

		try {
			l.info("Deleting user with id: {}", id);
			userRepository.deleteById(Long.parseLong(id)); 
			l.info("User deleted successfully with id: {}", id);
		} catch (Exception e) {
			l.error("Error in deleteUser(): ", e);
		}

	}

	@Override
	public User retrieveUser(String id) {
		User u = null;
		try {
			u =  userRepository.findById(Long.parseLong(id)).get();
		} catch (Exception e) {
			l.error("Error in retrieveUser(): ", e);
		}

		return u;
	}

	
	
	
}
