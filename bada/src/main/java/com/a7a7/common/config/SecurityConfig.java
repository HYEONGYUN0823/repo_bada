package com.a7a7.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Autowired
	private MyUserDetailsService myUserDetailsService;
	
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // CSRF 비활성화
            .userDetailsService(myUserDetailsService)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // 모든 요청 허용
            )
            .formLogin(form -> form
            		.loginProcessingUrl("/login")
            		.usernameParameter("email")
                    .loginPage("/bada/signIn")  // 커스텀 로그인 페이지 경로
                    .defaultSuccessUrl("/index", true)                // 로그인 성공 후 이동할 페이지
                    .failureUrl("/bada/signIn?error")            // 로그인 실패 시 이동할 페이지
                    .permitAll()          // 로그인 페이지는 모두 접근 가능
            );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 정적 리소스 spring security 대상에서 제외
        return (web) ->
                web
                    .ignoring()
                    .requestMatchers(
                            PathRequest.toStaticResources().atCommonLocations()
                    );
    }
    
    @Bean 
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
