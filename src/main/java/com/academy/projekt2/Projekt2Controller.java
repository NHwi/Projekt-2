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
    List<User> users = new ArrayList<>();
    List<Room> rooms = new ArrayList<>();
    String errorText = "";
    String errorClass = "hidden";

    @PostMapping("/adduser")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String email,
                          HttpServletRequest request) {
        users.add(new User(lr.addUser(email, username, password), username, password, email));
        return login(username, password, request);
    }

    public int getCurrentRoom(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            user = (User) session.getAttribute("user");
            return user.getCurrentRoom();
        } else {
            return 1;
        }
    }

    public String addError(String errorMessage, HttpServletRequest request) {
        errorText = errorMessage;
        errorClass = "alert alert-danger";
        return "redirect:/?roomid=" + getCurrentRoom(request);
    }

    @PostMapping("/sendmessage")
    public String sendMessage(@RequestParam String message, @RequestParam String room, HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("user") == null) {
            lr.addMessage(Integer.parseInt(room), 1, message);
        } else {
            User user = (User) session.getAttribute("user");
            lr.addMessage(Integer.parseInt(room), user.getId(), message);
        }
        return "redirect:/?roomid=" + getCurrentRoom(request);

    }

    @GetMapping("/")
    public ModelAndView index(@RequestParam(required = false, defaultValue = "1") int roomid, HttpServletRequest request) {
        loadRooms(request);
        HttpSession session = request.getSession(true);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            user = (User) session.getAttribute("user");
            user.setCurrentRoom(roomid);
        }
        return loadMessages(roomid, request);
    }

    public void loadRooms(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            rooms = lr.getRooms(user.getId());
        } else {
            rooms = lr.getRooms(1);
        }
    }

    public ModelAndView loadMessages(int roomid, HttpServletRequest request) {

        String messagesString = "";
        List<Message> messageList = lr.getMessages(roomid, 100);
        for (Message message : messageList) {
            messagesString += message.getDate() + ", " + message.getUsername() + ": " + message.getMessage();
        }
        String roomTitle = "";
        for (Room room : rooms) {
            if (room.getId() == getCurrentRoom(request)) {
                roomTitle = room.getName();
            }
        }

        String loginText = "Sign in";
        String btnclass = "";
        String hidden = "hidden";
        HttpSession session = request.getSession(true);
        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            btnclass = "account";
            loginText = user.getUsername();
            hidden = "";
        }
        String tempError = errorText;
        String tempErrorClass = errorClass;
        errorClass = "hidden";
        errorText = "";

        return new ModelAndView("index")
                .addObject("chatmessages", messageList)
                .addObject("logintext", loginText + "  ")
                .addObject("btnclass", btnclass)
                .addObject("rooms", rooms)
                .addObject("errorText", tempError)
                .addObject("errorClass", tempErrorClass)
                .addObject("currentRoom", getCurrentRoom(request))
                .addObject("hidemenu", hidden)
                .addObject("roomtitle", roomTitle);
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request) {
        User user = lr.loggedIn(username, password);
        if (user != null) {

            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            users.add(user);

            return "redirect:/?roomid=" + getCurrentRoom(request);
        } else {
            return addError("Wrong username or password", request);
        }
    }

    @GetMapping("/logout")
    public String logout(
            HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i) == user) {
                    users.remove(i);
                    i = users.size();
                }
            }

        }
        session.setAttribute("user", 0);
        session.invalidate();
        return "redirect:/?roomid=" + getCurrentRoom(request);
    }


    @PostMapping("/addRoom")
    public String addRoom(@RequestParam String name,
                          @RequestParam String description,
                          HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            if (name.length() >= 3) {
                User user = (User) session.getAttribute("user");
                lr.addRoom(name, description, user.getId());
                loadRooms(request);
            } else {
                return addError("Room name need to be atleast 3 letters long", request);
            }
        } else {
            return addError("You need to login to create a new room", request);
        }
        return "redirect:/?roomid=" + getCurrentRoom(request);
    }

    @PostMapping("/addKeys")
    public String addKey(@RequestParam String name,
                         HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            lr.addKey(user.getId(), getCurrentRoom(request), name);
            loadRooms(request);
        }
        return "redirect:/?roomid=" + getCurrentRoom(request);
    }

}
