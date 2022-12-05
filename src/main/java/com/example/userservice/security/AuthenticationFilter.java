package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final Environment env;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
            //사용자가 입력했던 email 과 password 를 spring security 에서 사용할 수 있는 형태로 변환하기 위해서 UsernamePasswordAuthenticationToken 의 형태로 바꿔주어야한다.
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList<>());
            // 권한에 관련된 인자를 하나 추가한 token 객체를 만든다.
            return getAuthenticationManager().authenticate(token);
            //AuthenticationManager 에게 인증작업을 요청한다. 후
            //AuthenticationManager 가 인증작업을 처리한 후 반환한다.
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //인증이 되었을 때 어떤 행동을 할 건지 ex) 만료시간
        String email = ((User) authResult.getPrincipal()).getUsername();
        //인증에 성공하면 jwt 토큰을 만들고 싶은데 나는 이메일이 아닌 id에 따른 토큰을 만든다.
        UserDto userDto = userService.getUserByEmail(email);

    }
}
