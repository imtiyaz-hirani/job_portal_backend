package com.pluralsight.jobportal.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

 
@Configuration
public class SecurityConfig {
	 
	@Bean
    UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("{noop}admin123")
                .roles("ADMIN")
                .build();

        UserDetails applicant = User.withUsername("user")
                .password("{noop}user123")
                .roles("APPLICANT")
                .build();

        return new InMemoryUserDetailsManager(admin, applicant);
    }
	
	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthFilter jwtAuthFilter) throws Exception { 
		return http
				.cors(Customizer.withDefaults())
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers(HttpMethod.POST, "/api/jobs").hasRole("ADMIN")
	                .requestMatchers(HttpMethod.GET, "/api/jobs/all").permitAll()
	                .requestMatchers(HttpMethod.POST, "/api/application/apply/**")
	                										.hasRole("APPLICANT")
	                .requestMatchers(HttpMethod.POST, "/api/auth/login").authenticated()
	                .requestMatchers("/oauth/**", "/login/**", "/oauth2/**", "/api/oauth/**").permitAll() //Allow OAuth endpoints
	                .requestMatchers(HttpMethod.GET,"/api/auth/details").permitAll()
	                .anyRequest().authenticated()
	            )
	            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
	            .httpBasic(Customizer.withDefaults()) 
	            //.oauth2Login(Customizer.withDefaults()) //Enable Google OAuth login
	            .oauth2Login(oauth -> oauth
	            	    .userInfoEndpoint(userInfo -> userInfo
	            	        .userService(userRequest -> {
	            	            var delegate = new DefaultOAuth2UserService();
	            	            var oauth2User = delegate.loadUser(userRequest);

	            	            // Assign ROLE_APPLICANT to every OAuth user
	            	            return new DefaultOAuth2User(
	            	                List.of(new SimpleGrantedAuthority("ROLE_APPLICANT")),
	            	                oauth2User.getAttributes(),
	            	                "email"
	            	            );
	            	        })
	            	    )
	            	)

	            .build();
     }
	
	@Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
		//Use environment variable for the frontend URL
	    String frontendUrl = System.getenv("FRONTEND_URL");
	    if (frontendUrl == null || frontendUrl.isBlank()) {
	        frontendUrl = "http://localhost:5173";  
	    }
	    
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(frontendUrl));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
