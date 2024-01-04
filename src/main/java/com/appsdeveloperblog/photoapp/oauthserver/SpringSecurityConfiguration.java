package com.appsdeveloperblog.photoapp.oauthserver;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

	@Bean
	SecurityFilterChain configureSecurityFilterChain(HttpSecurity http) throws Exception {
		
		http
		.authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
		.formLogin(Customizer.withDefaults());
		
		return http.build();
		
	}
	

	@Bean
	public UserDetailsService users() {
		
		PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		
		UserDetails user = User.withUsername("sergey")
				.password(encoder.encode("password"))
				.roles("USER")
				.build();
		
		return new InMemoryUserDetailsManager(user);
		
	}
	
	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
	    return context -> {
	        if (context.getTokenType() == OAuth2TokenType.ACCESS_TOKEN) {
	            Authentication principal = context.getPrincipal();
	            Set<String> authorities = principal.getAuthorities().stream()
	                    .map(GrantedAuthority::getAuthority)
	                    .collect(Collectors.toSet());
	            context.getClaims().claim("roles", authorities);
	        }
	    };
	}

}
