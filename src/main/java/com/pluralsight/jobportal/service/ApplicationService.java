package com.pluralsight.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pluralsight.jobportal.model.Application;
import com.pluralsight.jobportal.model.Job;
import com.pluralsight.jobportal.model.User;
import com.pluralsight.jobportal.repository.ApplicationRepository;
import com.pluralsight.jobportal.repository.JobRepository;
import com.pluralsight.jobportal.repository.UserRepository;

@Service
public class ApplicationService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
	
	public Application applyForJob(String username, Long jobId) {
	    /* Fetch User object using userId or throw exception */
		User user = userRepository.findByEmail(username)
	        .orElseThrow(() -> new RuntimeException("User not found"));
		
		/* Fetch Job object using jobId or throw exception */
	    Job job = jobRepository.findById(jobId)
	        .orElseThrow(() -> new RuntimeException("Job not found"));
	    
	    /* Attach User and Job details to Application object */
	    Application application = new Application(user, job);
	    
	    /* Save it in DB using save() method of JpaRepository */
	    return applicationRepository.save(application);
	}
}
