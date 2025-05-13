package com.example.aniting.admin.users;

import java.util.List;

import com.example.aniting.dto.UsersDTO;

public interface AdminUserService {

	public List<UsersDTO> getUserList(String activeYn, String joinStartDt, String joinEndDt, String inactiveStartDt,
			String inactiveEndDt, String keyword);
	public void updateUser(Long usersNo, UsersDTO dto);
	public void deleteUser(Long usersNo);
	public void deleteUsers(List<Long> usersNos);
	public UsersDTO getUser(Long usersNo);

}
