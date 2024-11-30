package com.example.authservice.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class BaseResponse<T> {
    private int responseCode;
    private String responseMessage;
    private T data;

    public static <T> BaseResponseBuilder<T> builder() {
        return new BaseResponseBuilder<T>();
    }

    public static class BaseResponseBuilder<T> {
        private int responseCode;
        private String responseMessage;
        private T data;

        public BaseResponseBuilder<T> responseCode(int responseCode) {
            this.responseCode = responseCode;
            return this;
        }

        public BaseResponseBuilder<T> responseMessage(String responseMessage) {
            this.responseMessage = responseMessage;
            return this;
        }

        public BaseResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public BaseResponse<T> build() {
            return new BaseResponse<T>(responseCode, responseMessage, data);
        }
    }
}
