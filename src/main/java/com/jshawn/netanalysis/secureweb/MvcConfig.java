package com.jshawn.netanalysis.secureweb;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class MvcConfig implements WebMvcConfigurer {

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("main");
		registry.addViewController("/login").setViewName("login");
		registry.addViewController("/profile").setViewName("profile");
		registry.addViewController("/error").setViewName("error");
		registry.addViewController("/newprofile/new").setViewName("newprofile");
		registry.addViewController("/sites").setViewName("sites");
		registry.addViewController("/logs").setViewName("logs");
		registry.addViewController("/files").setViewName("files");
		registry.addViewController("/git").setViewName("git");
		registry.addViewController("/git/code/*").setViewName("gitrepo");
		registry.addViewController("/git/code/**").setViewName("gitFileCode");
		registry.addViewController("/git/commits/**").setViewName("gitCommits");
		registry.addViewController("/git/compareCommits/**").setViewName("gitCompareCommits");
		registry.addViewController("/settings").setViewName("settings");
	}
	
}