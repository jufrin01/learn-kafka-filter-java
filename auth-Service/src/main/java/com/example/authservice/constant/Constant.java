package com.example.authservice.constant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Builder
public final class Constant {

    public static final String[] AUTH_WHITELIST = {
            "/swagger-ui/**",
            "/api-docs/**",
            "/swagger-ui.html",
            "/verif/register",
            "/verif/login",
            "/verif/validateAccessToken"
    };

    public static final class Response {
        private Response() {

        }

        public static final int SUCCESS_CODE = 200;
        public static final String SUCCESS_MESSAGE = "Success";
        public static final String SUCCESS_VALID_TOKEN_MESSAGE = "Access token valid";
    }

    public static final class Message {
        private Message() {

        }

        public static final String EXIST_DATA_MESSAGE = "Data already exists";
        public static final String NOT_FOUND_DATA_MESSAGE = "Data not found";
        public static final String FORBIDDEN_REQUEST_MESSAGE = "Different {value} with existing data is forbidden";
        public static final String INVALID_LOGIN_MESSAGE = "Invalid username or password";
        public static final String INVALID_TOKEN_MESSAGE = "Invalid access token";
    }

    public static final class Regex {
        private Regex() {

        }

        public static final String NUMERIC = "\\d+";
        public static final String ALPHANUMERIC = "^[a-zA-Z0-9]+$";
        public static final String ALPHABET = "^[a-zA-Z]+$";
        public static final String ALPHANUMERIC_WITH_DOT_AND_SPACE = "^[a-zA-Z0-9.' ]+$";
    }
}