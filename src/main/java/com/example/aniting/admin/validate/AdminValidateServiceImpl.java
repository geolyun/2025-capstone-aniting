package com.example.aniting.admin.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.aniting.entity.Admin;
import com.example.aniting.repository.AdminRepository;

@Service
public class AdminValidateServiceImpl implements AdminValidateService {

	@Autowired
	private AdminRepository adminRepository;
	
	@Override
	public Admin validateAdmin(String adminId, String passwd) {
		
		Admin admin = adminRepository.findByAdminId(adminId);
		
	    if (admin != null && admin.getPasswd().equals(passwd)) {
	        return admin;
	    }
	    
	    return null;
	    
	}

}
