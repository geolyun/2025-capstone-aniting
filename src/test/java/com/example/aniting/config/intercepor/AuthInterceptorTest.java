package com.example.aniting.config.intercepor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.example.aniting.config.AuthInterceptorAdmin;
import com.example.aniting.config.AuthInterceptorResetPassword;
import com.example.aniting.config.AuthInterceptorUser;

@SpringBootTest
public class AuthInterceptorTest {

	@Autowired 
	private AuthInterceptorAdmin adminInterceptor;
	
    @Autowired 
    private AuthInterceptorUser userInterceptor;
    
    @Autowired 
    private AuthInterceptorResetPassword resetPasswordInterceptor;

    // ADMIN
    @Test
    void 관리자세션_존재시_통과() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.getSession().setAttribute("role", "admin");

        boolean result = adminInterceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    @Test
    void 관리자세션_없으면_리다이렉트() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = adminInterceptor.preHandle(request, response, new Object());
        assertFalse(result);
        assertEquals("/admin/login", response.getRedirectedUrl());
    }

    // USER
    @Test
    void 사용자세션_존재시_통과() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.getSession().setAttribute("role", "user");

        boolean result = userInterceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    @Test
    void 사용자세션_없으면_리다이렉트() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = userInterceptor.preHandle(request, response, new Object());
        assertFalse(result);
        assertEquals("/", response.getRedirectedUrl());
    }

    // RESET PASSWORD
    @Test
    void resetUserId_존재시_통과() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.getSession().setAttribute("resetUserId", "testUser");

        boolean result = resetPasswordInterceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    @Test
    void resetUserId_없으면_스크립트_반환() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = resetPasswordInterceptor.preHandle(request, response, new Object());

        assertFalse(result);
        String body = response.getContentAsString();
        assertTrue(body.contains("비밀번호 찾기를 먼저 진행해주세요."));
        assertTrue(body.contains("location.href = '/user/find_password';"));
    }
	
}
