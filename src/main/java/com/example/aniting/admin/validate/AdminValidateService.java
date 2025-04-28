package com.example.aniting.admin.validate;

import com.example.aniting.entity.Admin;

public interface AdminValidateService {

	public Admin validateAdmin(String adminId, String passwd);

}
