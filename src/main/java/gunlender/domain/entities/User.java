package gunlender.domain.entities;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Getter
public class User {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;

    private String phoneNumber;
    private String login;

    private String passwordHash;
    private Account.AccountType accountType;

    private static final String SQL_DEFINITION = "(Id string, FirstName string, LastName string, Email string," +
            "Login string, PasswordHash string, PhoneNumber string, AccountType string)";

    public User(String firstName, String lastName, String email, String phoneNumber, String login, String passwordHash,
                Account.AccountType type) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.login = login;
        this.passwordHash = passwordHash;
        this.accountType = type;
    }

    private User() {}

    public static User fromResultSet(ResultSet rs) throws SQLException {
        var user = new User();

        user.id = UUID.fromString(rs.getString("Id"));
        user.firstName = rs.getString("FirstName");
        user.lastName = rs.getString("LastName");
        user.login = rs.getString("Login");
        user.email = rs.getString("Email");
        user.passwordHash = rs.getString("PasswordHash");
        user.phoneNumber = rs.getString("PhoneNumber");
        user.accountType = Account.FromString(rs.getString("AccountType"));

        return user;
    }

    public static String toSqlTableDefinition() {
        return SQL_DEFINITION;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", login='" + login + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", accountType=" + accountType +
                '}';
    }
}

