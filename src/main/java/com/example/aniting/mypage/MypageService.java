package com.example.aniting.mypage;

import java.util.List;
import java.util.Map;

import com.example.aniting.dto.UsersDTO;
import com.example.aniting.entity.Users;

public interface MypageService {

	public UsersDTO getUserInfo(String usersId);
	public List<Map<String, Object>> getRecommendHistory(String usersId);
	public UsersDTO updateUser(String usersId, String usersNm, String passwd);
	public void deactivateUser(String usersId);

}
