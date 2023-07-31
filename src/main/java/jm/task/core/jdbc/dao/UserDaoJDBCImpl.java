package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoJDBCImpl implements UserDao {
    private static final String sqlCreateDatabase = """
            CREATE DATABASE IF NOT EXISTS users;
            """;
    private static final String sqlUseUsers = """
            USE users;
            """;
    private static final String sqlCreateTable = """
            CREATE TABLE IF NOT EXISTS users (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(50) NOT NULL, 
            last_name VARCHAR(50) NOT NULL,
            age INT NOT NULL                     
            );
            """;
    private static final String sqlDropTable = """
            DROP TABLE IF EXISTS users;
            """;
    private static final String sqlSaveUser = """
            INSERT INTO users (name, last_name, age)
            VALUES (?, ?, ?);
            """;
    private static final String sqlClearUsers = """
            TRUNCATE users;
            """;
    private static final String sqlRemoveUserById = """
            DELETE FROM users
            WHERE id = ?;
            """;
    private static final String sqlGetAllUsers = """
            SELECT id, name, last_name, age FROM users;
            """;

    public UserDaoJDBCImpl() {

    }
    public void createUsersTable() {
        try (Connection conn = Util.open(); Statement statement = conn.createStatement()) {
            statement.addBatch(sqlCreateDatabase);
            statement.addBatch(sqlUseUsers);
            statement.addBatch(sqlCreateTable);
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropUsersTable() {
        try (Connection conn = Util.open(); Statement statement = conn.createStatement()) {
            statement.addBatch(sqlUseUsers);
            statement.addBatch(sqlDropTable);
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        try (Connection conn = Util.open()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlSaveUser);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setInt(3, age);
            preparedStatement.execute(sqlUseUsers);
            preparedStatement.execute();
            System.out.printf("User с именем – %s добавлен в базу данных%n", name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeUserById(long id) {
        try (Connection conn = Util.open()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlRemoveUserById);
            preparedStatement.setInt(1, (int) id);
            preparedStatement.execute(sqlUseUsers);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = Util.open(); Statement statement = conn.createStatement()) {
            statement.execute(sqlUseUsers);
            ResultSet result = statement.executeQuery(sqlGetAllUsers);
            while (result.next()) {
                User user = new User(
                        result.getString("name"),
                        result.getString("last_name"),
                        result.getByte("age")
                );
                user.setId(result.getLong("id"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public void cleanUsersTable() {
        try (Connection conn = Util.open(); Statement statement = conn.createStatement()) {
            statement.addBatch(sqlUseUsers);
            statement.addBatch(sqlClearUsers);
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
