package com.study.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI().info(new Info().title("学生教学后台API").description("SpringBoot+MyBatis+JWT").version("1.0.0")
		// 下面是添加 Token 支持

		).addSecurityItem(new SecurityRequirement().addList("token")).components(
				new io.swagger.v3.oas.models.Components().addSecuritySchemes("token", new SecurityScheme().name("token") // 后端接收的
																															// header
																															// 名
						.type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER)));
	}
}