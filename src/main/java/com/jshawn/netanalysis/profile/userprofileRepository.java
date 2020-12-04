package com.jshawn.netanalysis.profile;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jshawn.netanalysis.profile.userprofile;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface userprofileRepository extends CrudRepository<userprofile, Integer> {
	
	List<userprofile> findByUsername(String username);
}