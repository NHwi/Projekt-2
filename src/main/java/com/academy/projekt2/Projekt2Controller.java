package com.academy.projekt2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class Projekt2Controller {
    @Autowired
    LoginRepository lr;

    List<User> users = lr.getAllUsers();

    @PostMapping("/adduser")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email){
        users.add(new User(lr.addUser(email, username, password),username, password, email));
        return "index";
    }

    @PostMapping("/sendmessage")
    public void sendMessage(@RequestParam String message,
                            @RequestParam int userID,
                            @RequestParam int roomID){
        lr.addMessage(roomID, userID, message);
    }

    @GetMapping("/")
    public String index(){

        return "index";
    }
}
