package com.david0926.enlight.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginBody {
    private Integer status;
    private String message;
    private String accessToken;
    private Boolean success;
    private UserModel userInfo;
}
