package com.david0926.enlight.Main1;

import com.david0926.enlight.Main2.CountBodyData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlertBody {
    private Integer status;
    private String message;
    private Boolean success;
}
