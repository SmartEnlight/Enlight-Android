package com.david0926.enlight.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterBody {
    private Integer status;
    private String message;
    private Boolean success;
}
