package com.example.authservice.service;


import com.example.authservice.BaseResponse.BaseResponse;
import com.example.authservice.constant.Constant;
import com.example.authservice.dto.LoginRequest;
import com.example.authservice.dto.LoginResponse;
import com.example.authservice.dto.UserRequest;
import com.example.authservice.exception.BadRequestCustomException;
import com.example.authservice.exception.DataExistException;
import com.example.authservice.exception.NotFoundException;
import com.example.authservice.model.User;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.jwt.JwtUtils;
import com.example.authservice.security.service.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class UserService {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;
    @Value("${user.role}")
    private String userRole;
    @Value("${user.gender}")
    private String userGender;

    Date nowDate = new Date();

    @Transactional
    public Response<Object> register(UserRequest userRequest) {
        User user = userRepository.findByUsername(userRequest.getUsername());
        if (Objects.nonNull(user)) {
            throw new DataExistException(Constant.Message.EXIST_DATA_MESSAGE);
        }

        if (!isValid(userRole, userRequest.getRole())) {
            throw new BadRequestCustomException(Constant.Message.FORBIDDEN_REQUEST_MESSAGE.replace("{value}", "role"));
        }
        if (!isValid(userGender, userRequest.getGender())) {
            throw new BadRequestCustomException(Constant.Message.FORBIDDEN_REQUEST_MESSAGE.replace("{value}", "gender"));
        }

        User savedUser = userRepository.save(mappingUser(userRequest));
        return (Response<Object>) BaseResponse.builder()
                .responseCode(Constant.Response.SUCCESS_CODE)
                .responseMessage(Constant.Response.SUCCESS_MESSAGE)
                .data(savedUser)
                .build();

    }


    public BaseResponse<Object> login(LoginRequest loginRequest) {

        User user = userRepository.findByUsername(loginRequest.getUsername());
        if (Objects.isNull(user)) {
            throw new NotFoundException(Constant.Message.INVALID_LOGIN_MESSAGE);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        LoginResponse response = mappingLoginResponse(user, jwt);
        return   BaseResponse.builder()
                .responseCode(Constant.Response.SUCCESS_CODE)
                .responseMessage(Constant.Response.SUCCESS_MESSAGE)
                .data(response)
                .build();
    }

    public BaseResponse<Object> validateAccessToken(String accessToken) {
        boolean isValid = jwtUtils.validateJwtToken(accessToken);
        if (!isValid) {
            throw new BadRequestCustomException(Constant.Message.INVALID_TOKEN_MESSAGE);
        }
        return  BaseResponse.builder()
                .responseCode(Constant.Response.SUCCESS_CODE)
                .responseMessage(Constant.Response.SUCCESS_VALID_TOKEN_MESSAGE)
                .build();
    }

    private LoginResponse mappingLoginResponse(User user,String jwt) {
        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(jwtExpirationMs)
                .role(user.getRole())
                .build();
    }

    private User mappingUser(UserRequest userRequest) {
        return User.builder()
                .name(userRequest.getName())
                .phoneNumber(userRequest.getPhoneNumber())
                .address(userRequest.getAddress())
                .email(userRequest.getEmail())
                .gender(userRequest.getGender())
                .username(userRequest.getUsername())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(userRequest.getRole())
                .createdBy("")
                .createdDate(this.nowDate)
                .isDeleted(false)
                .build();
    }

    private boolean isValid(String value, String request) {
        String[] valueList = value.split("\\|");
        List<String> reqList = new ArrayList<>();
        for (int i=0 ; i < valueList.length; i++) {
            if (valueList[i].equals(request)) {
                reqList.add(request);
                break;
            }
        }
        if (reqList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

}
