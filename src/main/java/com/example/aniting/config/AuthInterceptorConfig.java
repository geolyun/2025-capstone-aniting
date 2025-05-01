package com.example.aniting.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthInterceptorConfig implements WebMvcConfigurer {
	
	@Autowired
	private AuthInterceptorAdmin authInterceptorAdmin;
	
	@Autowired
	private AuthInterceptorUser authInterceptorUser;
	
	@Autowired
	private AuthInterceptorResetPassword authInterceptorResetPassword;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		// 관리자 전용 경로 보호
        registry.addInterceptor(authInterceptorAdmin)
                .order(1)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login", "/api/admin/adminLogin");

        // 일반 사용자 전용 경로 보호
        registry.addInterceptor(authInterceptorUser)
                .order(2)
                .addPathPatterns("/user/**", "/recommend/**")
                .excludePathPatterns(
                    "/", "/user/register", "/user/find_password", "/user/reset_password",
                    "/api/users/**"
                );

        // 비밀번호 재설정 전용 보호
        registry.addInterceptor(authInterceptorResetPassword)
                .order(3)
                .addPathPatterns("/user/reset_password");
		
	}
	
	
}
