package com.example.aniting.repository;

import com.example.aniting.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

	Optional<Users> findByUsersId(String usersId);
	boolean existsByUsersId(String usersId);
	
}
