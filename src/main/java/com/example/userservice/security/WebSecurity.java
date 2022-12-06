package com.example.userservice.security;

import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/**")//모든 요청에
                .hasIpAddress("192.168.219.142 ")// 이 아이피만
                .and()//그리고
                .addFilter(getAuthenticationFilter());//todo 1: 필터를 거친 요청만 처리한다. 여기서 필터는 인증처리 필터
        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager(),userService,env);//todo 2 인증처리하기 위한 UsernamePasswordFilter 를 상속받은 필터 정의
        authenticationFilter.setAuthenticationManager(authenticationManager());//todo 3 인증처리하기 위해, Spring security 에서 Manager 가져옴

        return authenticationFilter;
    }

    // 1. select pwd from users where email=?
    // 2. 암호화된 비밀번호와 입력한 비밀번호를 비교
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService) //로그인을 해주는 서비스이다. 사용자의 입력값으로 계정을 검색을 한다. 1의역할을 한다.loadByUsername 을 구현해야
                .passwordEncoder(bCryptPasswordEncoder);
    }
}
