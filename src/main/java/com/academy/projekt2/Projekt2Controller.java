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
    List<Room> rooms = new ArrayList<>();
    int currentRoom = 1;
    String loginText = "Sign In  ";
    String btnclass = "";
    String errorText = "";
    String errorClass = "hidden";

    @PostMapping("/adduser")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          HttpServletRequest request){
        users.add(new User(lr.addUser(email, username, password),username, password, email));
        return login(username, password, request);
    }

    public String addError(String errorMessage){
        errorText = errorMessage;
        errorClass = "alert alert-danger";
        return "redirect:/?roomid=" + currentRoom;
    }

    @PostMapping("/sendmessage")
    public String sendMessage(@RequestParam String message, @RequestParam String room, HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session == null) {
            lr.addMessage(Integer.parseInt(room), 1, message);
        }
        else {
            lr.addMessage(Integer.parseInt(room), (int)session.getAttribute("id"), message);
        }
        return "redirect:/?roomid=" + currentRoom;
    }

    @GetMapping("/")
    public ModelAndView index(@RequestParam(required = false, defaultValue = "1") int roomid) {
        currentRoom = roomid;
        return loadMessages(roomid);
    }

    public void loadRooms(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null)
            rooms = lr.getRooms((int)session.getAttribute("id"));
    }

    public ModelAndView loadMessages(int roomid){
        /*Thread updateThread = new Thread(updatePage);
        updateThread.start();*/

        String messagesString = "";
        List<Message> messageList = lr.getMessages(roomid, 100);
        for (Message message : messageList) {
            messagesString += message.getDate() + ", " + message.getUsername() + ": " + message.getMessage();
        }
        String hidden = "hidden";
        if (btnclass == "account"){
            hidden = "";
        }
        return new ModelAndView("index")
                .addObject("chatmessages", messageList)
                .addObject("logintext", loginText + "  ")
                .addObject("btnclass", btnclass).addObject("rooms", rooms)
                .addObject("errorText", errorText)
                .addObject("errorClass", errorClass)
                .addObject("currentRoom", currentRoom)
                .addObject("hidemenu", hidden);
    }
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request)
    { User user = lr.loggedIn(username, password);
        if(user != null){

            HttpSession session = request.getSession(true);
            session.setAttribute("id",user.getId());
            loadRooms(request);
            users.add(user);
            loginText = user.getUsername();
            btnclass = "account";

            return "redirect:/?roomid=" + currentRoom;
        } else {
            addError("Wrong username or password");
        }
        return "redirect:/?roomid=" + currentRoom;
    }

    @GetMapping("/logout")
     public String logout (
             HttpServletRequest request){
        HttpSession session = request.getSession(true);
        if (session.getAttribute("id")!=null) {
            int id = (int)session.getAttribute("id");
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
        return "redirect:/?roomid=" + currentRoom;
    }


    @PostMapping("/addRoom")
    public String addRoom(@RequestParam String name,
                          @RequestParam String description,
                            HttpServletRequest request)
       {
           HttpSession session = request.getSession(false);
           if (session !=null) {
               System.out.println(name.length());
               if(name.length() >= 3) {
                   int id = (int) session.getAttribute("id");
                   lr.addRoom(name, description, id);
                   loadRooms(request);
               } else{
                    addError("Room name need to be atleast 3 letters long");
               }
           } else {
               addError("You need to login to create a new room");
           }
           return "redirect:/?roomid=" + currentRoom;
    }
    /*Runnable updatePage = new Runnable() {
        public void run() {
            try {
                while (true) {
                    loadMessages(currentRoom);
                    Thread.sleep(1000L);
                }
            } catch (InterruptedException iex) {
                iex.printStackTrace();
            }
        }
    };*/

    @PostMapping("/addKeys")
    public String addKey(@RequestParam String name,
                         HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session !=null) {
            int uid = (int) session.getAttribute("id");
            lr.addKey(uid, 19, name);
            loadRooms(request);
        }
        return "redirect:/";
    }

}
