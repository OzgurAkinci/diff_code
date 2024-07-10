package com.app.diff_code.controller;

import com.app.diff_code.api.ApiService;
import com.app.diff_code.dto.RequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;

    @RequestMapping(value = {"/","/index"})
    public ModelAndView api(ModelAndView mv) {
        //RequestDTO req = new RequestDTO(3, "1,2,3", new BigDecimal(0),new BigDecimal(0),  true, false);
        //var response = apiService.run(req);
        ////mv.addObject("latexFormat", response.getLatexFormat());
        mv.setViewName("index");
        return mv;
    }
}
