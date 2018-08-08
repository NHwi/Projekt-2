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

    public List<Message> getMessages(int roomID) {
        List<Message> templist = new ArrayList<>();
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, userid, message FROM Messages WHERE roomid = ?");
            ps.setString(1, Integer.toString(roomID));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                templist.add(new Message(rs.getInt("id"), roomID, rs.getInt("userid"), rs.getString("message")));
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

    public void addRoom(String name, String description) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Rooms VALUES(?,?)");
            ps.setString(1, name);
            ps.setString(2, description);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMessage(int roomID, int userID, String message) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO Messages VALUES(?,?,?)");

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
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT (username) FROM Users WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {


                ps = conn.prepareStatement("EXEC CreateUser @username = ?, @password = ?, @email = ?", new String[]{"id"});
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, email);
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();

                rs.next();
                return rs.getInt("GENERATED_KEYS");
            }
            throw new SQLException("Användaren existerar redan");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    public int loggedIn(String username, String password) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, password FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (password.equals(rs.getString("password"))) {
                    return rs.getInt("id");
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}


