package com.jshawn.netanalysis.secureweb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private usersRepository usersRepository;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
		.authorizeRequests()
			.antMatchers("/css/**", "/images/**", "/js/**").permitAll()
			.antMatchers("/profile").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
			.antMatchers("/logs").access("hasRole('ROLE_ADMIN')")
			.antMatchers("/").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
			.anyRequest().authenticated();
		

		
		http.csrf().disable();
		
		http.formLogin()
			.loginPage("/login")
			.successHandler(AuthenticationSuccessHandler())
			.permitAll();
		
		http.logout()
			.deleteCookies("JSESSIONID")
			.invalidateHttpSession(true)
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
			.logoutSuccessUrl("/login");
		
		http.sessionManagement()
		  .maximumSessions(1).sessionRegistry(sessionRegistry());
	}
	
	@Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring().antMatchers("/newprofile/new/{hash}");
		
    }
	
	@Bean(name="testURLs")
    public List<String> testURLs() {
		
		List<String> testURLs = new ArrayList<String>();
		
        return testURLs;
    }
	
	
	@Bean
	public SessionRegistry sessionRegistry() {
	    return new SessionRegistryImpl();
	}

	@Bean
	public AuthenticationSuccessHandler AuthenticationSuccessHandler(){
		
	    return new UrlAuthenticationSuccessHandler();
	}

//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//		
//		Iterable<users> userslist = usersRepository.findAll();
//		
//		for(users u: userslist){
//			
//			auth.inMemoryAuthentication()
//			.passwordEncoder(passwordEncoder())
//			.withUser(u.getUsername())
//			.password(passwordEncoder().encode(u.getPass()))
//			.roles(u.getRole());
//			
//		}
//		
//	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(inMemoryUserDetailsManager());
	}
	
	@Bean
	public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
		List<UserDetails> userDetailsList = new ArrayList<>();
		Iterable<users> userslist = usersRepository.findAll();
		for(users u: userslist){
			userDetailsList.add(
					User.withUsername(u.getUsername())
						.password(
							passwordEncoder()
							.encode(u.getPass())
						)
					.roles(u.getRole()).build());
			
		}

		return new InMemoryUserDetailsManager(userDetailsList);
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}