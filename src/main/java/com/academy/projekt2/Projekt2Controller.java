package com.academy.projekt2;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Projekt2Controller {
    LoginRepository lr;

    @PostMapping("/sendmessage")
    public void sendMessage(@RequestParam String message, @RequestParam int userID, @RequestParam int roomID){
        lr.addMessage(roomID, userID, message);
    }
}
