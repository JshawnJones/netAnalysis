package com.jshawn.netanalysis.secureweb;

import org.springframework.data.repository.CrudRepository;

import com.jshawn.netanalysis.secureweb.users;
import java.util.List;
// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface usersRepository extends CrudRepository<users, Integer> {

	List<users> findByUsername(String username);
	
}