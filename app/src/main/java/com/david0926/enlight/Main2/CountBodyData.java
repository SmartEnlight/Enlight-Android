package com.david0926.enlight.Main2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountBodyData {
    private Integer morning;
    private Integer night;
    private Integer total;
}
