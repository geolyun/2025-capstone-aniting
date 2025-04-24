package com.example.aniting.admin.user;

import com.example.aniting.entity.Admin;

public interface AdminUserService {

	public Admin validateAdmin(String adminId, String passwd);

}
