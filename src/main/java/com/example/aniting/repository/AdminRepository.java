package com.example.aniting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.aniting.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {
	
	Admin findByAdminId(String adminId);
	
}
