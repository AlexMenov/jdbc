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
            TRUNCATE TABLE users;
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
        executeWithStatementBatch(sqlCreateTable);
    }

    public void dropUsersTable() {
        executeWithStatementBatch(sqlDropTable);
    }

    public void saveUser(String name, String lastName, byte age) {
        executeWithPreparedStatement(sqlSaveUser, name, lastName, age);
        System.out.printf("User с именем – %s добавлен в базу данных%n", name);
    }

    public void removeUserById(long id) {
        executeWithPreparedStatement(sqlRemoveUserById, id);
    }

    public List<User> getAllUsers() {
        return executeWithPreparedStatement(sqlGetAllUsers, null);
    }

    public void cleanUsersTable() {
        executeWithStatementBatch(sqlClearUsers);
    }

    private List<User> executeWithPreparedStatement(String sql, Object... otherElements) {
        try (Connection conn = Util.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.execute(sqlUseUsers);
            if (otherElements != null) {
                for (int i = 1; i <= otherElements.length; i++) {
                    preparedStatement.setObject(i, otherElements[i - 1]);
                }
                preparedStatement.execute();
            } else {
                List<User> users = new ArrayList<>();
                try (ResultSet result = preparedStatement.executeQuery()) {
                    while (result.next()) {
                        User user = new User(
                                result.getString("name"),
                                result.getString("last_name"),
                                result.getByte("age")
                        );
                        user.setId(result.getLong("id"));
                        users.add(user);
                    }
                    return users;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeWithStatementBatch(String sql) {
        try (Connection conn = Util.getConnection(); Statement statement = conn.createStatement()) {
            statement.addBatch(sqlUseUsers);
            statement.addBatch(sql);
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
