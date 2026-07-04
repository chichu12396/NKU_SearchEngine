package com.study.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.entity.Result;
import com.study.utils.JwtUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenInterceptor implements HandlerInterceptor {

	private final JwtUtil jwtUtil;

	public TokenInterceptor(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

		String token = request.getHeader("token");
		if(token == null || !jwtUtil.validate(token)){
			response.setContentType("application/json;charset=utf-8");
			response.getWriter().write(new ObjectMapper().writeValueAsString(Result.unauthorized()));
			return false;
		}
		return true;
	}
}