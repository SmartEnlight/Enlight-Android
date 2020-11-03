package com.david0926.enlight.Main2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountBody {
    private Integer status;
    private String message;
    private CountBodyData data;
    private Boolean success;
}
