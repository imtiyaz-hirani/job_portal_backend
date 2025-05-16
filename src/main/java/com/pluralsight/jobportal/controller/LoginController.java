package com.pluralsight.jobportal.controller;

 
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pluralsight.jobportal.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http//localhost:5173"})
public class LoginController {

	@Autowired
    private JwtUtil jwtUtil;
	
	@PostMapping("/login")
    public Map<String, String> login(Principal principal) {
        String username = principal.getName(); // fetched from Spring Security

        // Generate token
        String token = jwtUtil.generateToken(username);

        // Return as JSON
        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return response;
    }
	
	 @GetMapping("/details")
	    public Map<String, Object> getUserDetails(Principal principal) {
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        
	        Map<String, Object> response = new HashMap<>();
	        
	        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
	            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	            response.put("username", userDetails.getUsername());
	            response.put("roles", userDetails.getAuthorities().stream()
	                    .map(GrantedAuthority::getAuthority)
	                    .collect(Collectors.toList()));
	        } else {
	            response.put("error", "User not authenticated");
	        }

	        return response;
	    }
}
