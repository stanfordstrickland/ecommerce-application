package com.example.demo.controllers;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			logger.error("FAIL: username {} is not found", username);
			return ResponseEntity.notFound().build();
		}
		else {
			logger.info("SUCCESS: username {} successfully found", username);
			return ResponseEntity.ok(user);
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		logger.info("User name set with {}", createUserRequest.getUsername());
		Cart cart = new Cart();

		if (createUserRequest.getPassword().length() < 7) {
			logger.error("FAIL: password for username {} is too short", user.getUsername());
			return ResponseEntity.badRequest().build();
		}
		if (!Objects.equals(createUserRequest.getPassword(), createUserRequest.getConfirmPassword())) {
			logger.error("FAIL: password and confirmPassword for username {} are different", user.getUsername());
			return ResponseEntity.badRequest().build();
		}

		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		user.setCart(cart);
		cartRepository.save(cart);
		userRepository.save(user);
		logger.info("SUCCESS: user with username {} successfully created", user.getUsername());
		return ResponseEntity.ok(user);
	}
	
}
