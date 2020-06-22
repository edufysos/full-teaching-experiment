package com.fullteaching.backend.security;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter.Breakpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fullteaching.backend.user.User;
import com.fullteaching.backend.user.UserComponent;

/**
 * This class is used to provide REST endpoints to logIn and logOut to the
 * service. These endpoints are used by Angular 2 SPA client application.
 * 
 * NOTE: This class is not intended to be modified by app developer.
 */
@RestController
public class LoginController {

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private UserComponent userComponent;

	@RequestMapping("/api-logIn")
	public ResponseEntity<User> logIn() {
		
		log.info("Logging in ...");

		if (!userComponent.isLoggedUser()) {
			if (!SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")){
				DefaultOAuth2User p = (DefaultOAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				User u = googleToUser(p);
				userComponent.setLoggedUser(u);
				User loggedUser = userComponent.getLoggedUser();
				log.info("Logged as {}", loggedUser.getName());
				return new ResponseEntity<>(loggedUser, HttpStatus.OK);
			}
			else{
				log.info("Not user logged");
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
				
			}	
		} else {
			User loggedUser = userComponent.getLoggedUser();
			log.info("Logged as {}", loggedUser.getName());
			return new ResponseEntity<>(loggedUser, HttpStatus.OK);
		}
	}

	@RequestMapping("/api-logOut")
	public ResponseEntity<Boolean> logOut(HttpSession session) {
		
		log.info("Logging out...");

		if (!userComponent.isLoggedUser()) {
			log.info("No user logged");
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		} else {
			String name = userComponent.getLoggedUser().getName();
			session.invalidate();
			log.info("Logged out user {}", name);
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
	}

	private User googleToUser(DefaultOAuth2User g){
		String name = (String) g.getAttributes().get("name");
		String email = (String) g.getAttributes().get("email");
		String picture = (String) g.getAttributes().get("picture");
		
		User u = new User(email, email, name, picture,"ROLE_TEACHER");
		return u;

	}

}