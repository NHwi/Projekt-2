package com.academy.projekt2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class Projekt2Controller {
    @Autowired
    LoginRepository lr;
    int activeRoom = 1;
    List<User> users = new ArrayList<>();
    int currentRoom = 1;
    String loginText = "Sign In  ";
    String btnclass = "";

    @PostMapping("/adduser")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          HttpServletRequest request){
        users.add(new User(lr.addUser(email, username, password),username, password, email));
        return login(username, password, request);
    }


    @PostMapping("/sendmessage")
    public String sendMessage(@RequestParam String message, HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session == null) {
            lr.addMessage(currentRoom, 1, message);
        }
        else {
            lr.addMessage(currentRoom, (int)session.getAttribute("id"), message);
        }
        return "redirect:/";
    }

    @GetMapping("/")
    public ModelAndView index() {
        return loadMessages();
    }

    public ModelAndView loadMessages(){
        String messagesString = "";
        List<Message> messageList = lr.getMessages(1, 100);
        for (Message message : messageList) {
            messagesString += message.getDate() + ", " + message.getUsername() + ": " + message.getMessage();
        }

        return new ModelAndView("index")
                .addObject("chatmessages", messageList)
                .addObject("logintext", loginText + "  ")
                . addObject("btnclass", btnclass);
    }
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request)
    { User user = lr.loggedIn(username, password);
        if(user != null){
            HttpSession session = request.getSession(true);
            session.setAttribute("id",user.getId());
            users.add(user);
            loginText = user.getUsername();
            btnclass = "account";
            return "redirect:/";

        }

        return "redirect:/";
    }

    @GetMapping("/logout")
     public String logout (
             HttpServletRequest request){
        HttpSession session = request.getSession(true);
        if (session.getAttribute("id")!=null) {
            int id = (int)session.getAttribute("id");
            System.out.println(id);
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId() == id){
                    users.remove(i);
                    i = users.size();
                }
            }
             currentRoom = 1;

        }
        session.setAttribute("id",0);
        session.invalidate();
        btnclass = "";
        loginText = "Sign In  ";
        return "redirect:/";
    }


    @PostMapping("/addRoom")

    public String addRoom(@RequestParam String name,
                          @RequestParam String description,
                            HttpServletRequest request)
       {
        lr.addRoom(name, description, 1);
        return "redirect:/";
    }




}
