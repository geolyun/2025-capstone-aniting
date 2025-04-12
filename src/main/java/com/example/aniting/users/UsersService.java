package com.example.aniting.users;

import com.example.aniting.dto.UsersDTO;

public interface UsersService {

	public boolean register(UsersDTO dto);
	public boolean checkDuplicateId(String usersId);
	public UsersDTO login(String usersId, String passwd);
	public boolean verifySecurityAnswer(String usersId, String question, String answer);
	public boolean resetPassword(String usersId, String newPassword);

}
