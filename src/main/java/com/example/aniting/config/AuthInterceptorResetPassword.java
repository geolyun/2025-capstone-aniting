package com.example.aniting.config;

import java.io.PrintWriter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptorResetPassword implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpSession session = request.getSession();
        String resetUserId = (String) session.getAttribute("resetUserId");

        if (resetUserId == null) {
            response.setContentType("text/html; charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print("""
                <script>
                    alert('비밀번호 찾기를 먼저 진행해주세요.');
                    location.href = '/user/find_password';
                </script>
            """);
            return false;
        }

        return true;
        
	}
	
}
