package com.app.diff_code.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private StringBuilder textFormat;
    private StringBuilder latexFormat;
    private byte[] pdfFile;
    private String pdfFilePath;
}
