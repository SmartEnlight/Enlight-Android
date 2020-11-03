package com.david0926.enlight.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private String _id;
    private String email;
    private String username;
    private String lastLoginTime;
    private String createdTime;
    private Integer loginCount;
    private String deviceId;
    private Boolean notification;
}
