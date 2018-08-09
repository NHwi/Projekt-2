package com.academy.projekt2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class Projekt2Controller {
    @Autowired
    LoginRepository lr;
    int activeRoom = 1;
    List<User> users = new ArrayList<>();

    @PostMapping("/adduser")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email){
        users.add(new User(lr.addUser(email, username, password),username, password, email));
        return "index";
    }


    @PostMapping("/sendmessage")
    public String sendMessage(@RequestParam String message){
        lr.addMessage(1, 1, message);
        return "redirect:/";
    }

    @GetMapping("/")
    public ModelAndView index() {
        return loadMessages();
    }

    public ModelAndView loadMessages(){
        String messagesString = "";
        List<Message> messageList = lr.getMessages(1);
        for (Message message : messageList) {
            messagesString += message.getDate() + ", " + message.getUsername() + ": " + message.getMessage();
        }

        return new ModelAndView("index").addObject("chatmessages", messageList);
    }
    @PostMapping("/login")
    public String login(@RequestParam String username,
                              @RequestParam String password){
        if(lr.loggedIn(username, password) != null){
            users.add(lr.loggedIn(username, password));
            return "redirect:/";
        }
        return "redirect:/";

    }
}
