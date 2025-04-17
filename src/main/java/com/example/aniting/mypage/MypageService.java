package com.example.aniting.mypage;

import java.util.List;
import java.util.Map;

import com.example.aniting.dto.UsersDTO;

public interface MypageService {

	public UsersDTO getUserInfo(String usersId);
	public List<Map<String, Object>> getRecommendHistory(String usersId);

}
