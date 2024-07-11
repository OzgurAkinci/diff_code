package com.app.diff_code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {
    private Integer d;
    private Integer v;
    private String m;
    private BigDecimal h;
    private double[] yvalues;
    private boolean toText;
    private boolean toPdf;
}
