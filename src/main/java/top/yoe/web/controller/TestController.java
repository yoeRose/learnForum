package top.yoe.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.yoe.dao.UserRepository;
import top.yoe.pojo.User;
import top.yoe.util.MD5Util;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/hello")
    @ResponseBody
    public void hello(){

//        String str1 = "123456789";
//        String encode = MD5Util.MD5Encode(str1, null);
//        System.out.println(encode);
//        System.out.println(MD5Util.MD5Encode("123456789",null));
//
//        System.out.println(session.getAttribute("userID"));
//        User user = userRepository.findByUsernameOrRealname("3117002711",null);
//        System.out.println(user);
    }
}
