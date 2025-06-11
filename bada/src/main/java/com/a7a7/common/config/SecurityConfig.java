package com.a7a7.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
            )
            .oauth2Login(oauth2Login -> oauth2Login
                    .loginPage("/bada/signIn") // OAuth2 로그인 페이지도 커스텀 로그인 페이지로 설정
                    .userInfoEndpoint(userInfo -> userInfo
                        .userService(oauth2UserService()) // OAuth2 사용자 정보를 처리할 서비스 설정
                    )
                    .failureUrl("/bada/signIn?error")
                    .defaultSuccessUrl("/index", true) // OAuth2 로그인 성공 시 기본 이동 경로
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/index") // 로그아웃 후 이동할 URL
                    .deleteCookies("JSESSIONID")   // 쿠키 삭제
            )
            .sessionManagement(session -> session
            		.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 기본값, 세션 생성됨
            )
        ;

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
    
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        return new CustomOAuth2UserService();
    }
    
}
