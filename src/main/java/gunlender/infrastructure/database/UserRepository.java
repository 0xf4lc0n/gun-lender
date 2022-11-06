package gunlender.infrastructure.database;

import gunlender.application.Repository;
import gunlender.domain.entities.User;
import gunlender.domain.exceptions.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository implements Repository {
    private final String databaseUrl;

    public UserRepository(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public List<User> getUsers() throws RepositoryException {
        var users = new ArrayList<User>();

        try (var connection = getConnection()) {
            try (var statement = connection.createStatement()) {

                statement.setQueryTimeout(30);

                var rs = statement.executeQuery("select * from users");

                while (rs.next()) {
                    users.add(User.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Cannot get users from database", e);
        }

        return users;
    }

    public Optional<User> getUserById(UUID uuid) throws RepositoryException {
        Optional<User> user = Optional.empty();

        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement("select * from users where Id = ?")) {
                statement.setString(1, uuid.toString());
                statement.setQueryTimeout(30);

                var rs = statement.executeQuery();

                if (rs.next()) {
                    user = Optional.of(User.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            var msg = String.format("Cannot get user with id '%s' from database", uuid.toString());
            throw new RepositoryException(msg, e);
        }

        return user;
    }


    public void addUser(User user) throws RepositoryException {
        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement("insert into users values (?, ? ,? ,?, ? ,?, ?, ?)")) {
                statement.setQueryTimeout(30);

                statement.setString(1, user.getId().toString());
                statement.setString(2, user.getFirstName());
                statement.setString(3, user.getLastName());
                statement.setString(4, user.getEmail());
                statement.setString(5, user.getLogin());
                statement.setString(6, user.getPasswordHash());
                statement.setString(7, user.getPhoneNumber());
                statement.setString(8, user.getAccountType().name());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RepositoryException("Cannot insert user to database", e);
        }
    }

    public void migrate() throws RepositoryException {
        try (var connection = getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.setQueryTimeout(30);
                statement.executeUpdate(String.format("create table if not exists users %s", User.toSqlTableDefinition()));
            }
        } catch (SQLException e) {
            throw new RepositoryException("Cannot migrate 'users' table", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }
}
