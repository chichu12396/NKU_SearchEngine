package com.study.config;

import com.study.interceptor.TokenInterceptor;
import com.study.utils.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final JwtUtil jwtUtil;

	public WebConfig(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new TokenInterceptor(jwtUtil))
				.addPathPatterns("/**")
				.excludePathPatterns(
						"/user/login",
						"/swagger-ui/**",
						"/user/register",  // 注册接口必须放行
                        "/event/list",     // 首页拉取活动列表必须放行
                        "/swagger-ui/**",
						"/v3/**",
						"/event/detail",
						"/api/search/**"
				);
	}
	@Override
    public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600);
    }
}