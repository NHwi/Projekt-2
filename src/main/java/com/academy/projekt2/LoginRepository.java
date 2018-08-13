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

    public List<Message> getMessages(int roomID, int numbers) {
        List<Message> templist = new ArrayList<>();
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("EXEC GetMessages @roomid = ?, @numbers = ?");
            ps.setString(1, Integer.toString(roomID));
            ps.setString(2, Integer.toString(numbers));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                templist.add(new Message(rs.getString("username"), rs.getString("message"), rs.getString("date")));

            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return templist;
    }

    public void addKey(int userid, int roomid, String username) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("EXEC CreateKey @userid = ?, @roomid = ?, @username =?");
            ps.setInt(1, userid);
            ps.setInt(2, roomid);
            ps.setString(3, username);
            ps.executeUpdate();
            conn.close();
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
            ps.setInt(3, userid);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Room> getRooms(int userid) {
        List<Room> templist = new ArrayList<>();
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("EXEC GetRooms @userid = ?");
            ps.setString(1, Integer.toString(userid));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                templist.add(new Room(rs.getInt("id"), rs.getString("name"), rs.getString("description")));
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return templist;
    }

    public void addMessage(int roomID, int userID, String message) {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("EXEC CreateMessage @roomid = ?, @userid = ?,@message = ?");

            ps.setString(1, Integer.toString(roomID));
            ps.setString(2, Integer.toString(userID));
            ps.setString(3, message);
            ps.executeUpdate();
            conn.close();
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
            ps.executeQuery();
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
            if (rs.next()) {
                return new User(rs.getInt("id"), username, password, rs.getString("email"));
            }
            conn.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}