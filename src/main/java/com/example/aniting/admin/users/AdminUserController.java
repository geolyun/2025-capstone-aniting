package com.example.aniting.admin.users;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.aniting.dto.UsersDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

	@Autowired
	AdminUserService adminUserService;
	
    @GetMapping
    public List<UsersDTO> getUserList(
            @RequestParam(required = false) String activeYn,
            @RequestParam(required = false) String joinStartDt,
            @RequestParam(required = false) String joinEndDt,
            @RequestParam(required = false) String inactiveStartDt,
            @RequestParam(required = false) String inactiveEndDt,
            @RequestParam(required = false) String keyword
    ) {
        return adminUserService.getUserList(activeYn, joinStartDt, joinEndDt, inactiveStartDt, inactiveEndDt, keyword);
    }
    
    @GetMapping("/{usersNo}")
    public UsersDTO getUser(@PathVariable Long usersNo) {
        return adminUserService.getUser(usersNo);
    }

    @PutMapping("/{usersNo}")
    public void updateUser(@PathVariable Long usersNo, @RequestBody UsersDTO dto) {
        adminUserService.updateUser(usersNo, dto);
    }

    @DeleteMapping("/{usersNo}")
    public void deleteUser(@PathVariable Long usersNo) {
        adminUserService.deleteUser(usersNo);
    }
    
    @DeleteMapping("/batch")
    public void deleteUsers(@RequestBody List<Long> usersNos) {
        adminUserService.deleteUsers(usersNos);
    }
	
}
