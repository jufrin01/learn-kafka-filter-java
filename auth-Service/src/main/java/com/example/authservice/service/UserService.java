package com.example.authservice.service;

import com.example.authservice.BaseResponse.BaseResponse;
import com.example.authservice.config.AppConfig;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AppConfig appConfig;

    public UserService(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            AppConfig appConfig
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        log.info("UserRepository initialized: {}", userRepository);
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.appConfig = appConfig;
    }

    @Transactional
    public BaseResponse<Object> register(UserRequest userRequest) {
        User user = userRepository.findByUsername(userRequest.getUsername());

        if (Objects.nonNull(user)) {
            throw new DataExistException(Constant.Message.EXIST_DATA_MESSAGE);
        }

        if (!isValid(appConfig.getRole(), userRequest.getRole())) {
            throw new BadRequestCustomException(Constant.Message.FORBIDDEN_REQUEST_MESSAGE.replace("{value}", "role"));
        }
        if (!isValid(appConfig.getGender(), userRequest.getGender())) {
            throw new BadRequestCustomException(Constant.Message.FORBIDDEN_REQUEST_MESSAGE.replace("{value}", "gender"));
        }

        User savedUser = userRepository.save(mappingUser(userRequest));
        return BaseResponse.builder()
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

        LoginResponse response = mappingLoginResponse(user, jwt);
        return BaseResponse.builder()
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
        return BaseResponse.builder()
                .responseCode(Constant.Response.SUCCESS_CODE)
                .responseMessage(Constant.Response.SUCCESS_VALID_TOKEN_MESSAGE)
                .build();
    }

    private LoginResponse mappingLoginResponse(User user, String jwt) {
        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .accessToken(jwt)
                .tokenType("Bearer ")
                .expiresIn(appConfig.getJwtExpirationMs())
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
                .createdDate(new Date())
                .isDeleted(false)
                .build();
    }

    private boolean isValid(String allowedValues, String value) {
        if (allowedValues == null || value == null) {
            return false;
        }
        return Arrays.asList(allowedValues.split("\\|")).contains(value);
    }
}