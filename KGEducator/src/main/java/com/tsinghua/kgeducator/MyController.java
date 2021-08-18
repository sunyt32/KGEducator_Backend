package com.tsinghua.kgeducator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Controller
public class MyController {
    @RequestMapping ( "/" )
    @ResponseBody
    public String Test() {
        return "Hello World!";
    }
}
