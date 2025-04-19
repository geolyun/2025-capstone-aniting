package com.example.aniting.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.aniting.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeleteUserScheduler {

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private UsersService usersService;
	
	@Scheduled(cron = "0 0 3 * * 0")
	public void deleteInactiveUsers() {
		usersService.deleteInactiveUsers();
	}
	
}
