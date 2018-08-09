package com.academy.projekt2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class LoginRepository {
    @Autowired
    public DataSource dataSource;

    public List<User> getAllUsers() {
        List<User> templist = new ArrayList<>();
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, username, password, email FROM Users");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                templist.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("email")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return templist;
    }

    public List<Message> getMessages(int roomID, int numbers) {
        List<Message> templist = new ArrayList<>();
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("EXEC GetMessages @roomid = ?, @numbers = ?");
            ps.setString(1, Integer.toString(roomID));
            ps.setString(2, Integer.toString(numbers));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                templist.add(new Message(rs.getString("username"), rs.getString("message"), String.format("%02d", rs.getTime("date").getHours()) + ":" + String.format("%02d", rs.getTime("date").getMinutes())));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return templist;
    }

    public void addKey(int userID, int roomID) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Keys VALUES(?,?)");
            ps.setString(1, Integer.toString(userID));
            ps.setString(2, Integer.toString(roomID));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addRoom(String name, String description, int userid) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("EXEC CreateRoom @name = ?, @description = ?, @userid = ?");
            ps.setString(1, name);
            ps.setString(2, description);
            ps.setInt(3,userid);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMessage(int roomID, int userID, String message) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("EXEC CreateMessage @roomid = ?, @userid = ?,@message = ?");

            ps.setString(1, Integer.toString(roomID));
            ps.setString(2, Integer.toString(userID));
            ps.setString(3, message);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int addUser(String email, String username, String password) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("EXEC CreateUser @username = ?, @password = ?, @email = ?", new String[]{"id"});
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

            rs.next();
            return rs.getInt("GENERATED_KEYS");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public User loggedIn(String username, String password) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("EXEC Login @username = ?, @password = ? ");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return new User(rs.getInt("id"), username, password, rs.getString("email"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}


