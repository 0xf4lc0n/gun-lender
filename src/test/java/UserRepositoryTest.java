import com.github.javafaker.Faker;
import gunlender.domain.entities.Account;
import gunlender.domain.entities.User;
import gunlender.infrastructure.database.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

@Slf4j
class UserRepositoryTest {
    private static final Faker FAKER = new Faker();
    private static final List<String> databaseFiles = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryTest.class);

    private UserRepository getRepository() {
        var fileName = "gunlender" + UUID.randomUUID() + ".db";
        var connectionString = "jdbc:sqlite:" + fileName;
        var userRepo = new UserRepository(connectionString);
        userRepo.migrate();
        databaseFiles.add(fileName);
        return userRepo;
    }

    @Test
    void insertingNewUserDoesNotThrow() {
        var userRepo = getRepository();
        var user = new User(firstName(), lastName(), emailAddress(), phoneNumber(), login(), passwordHash(), Account.AccountType.STANDARD);
        assertDoesNotThrow(() -> userRepo.addUser(user));
    }

    @Test
    void retrievingAllUsersDoesNotThrow() {
        var userRepo = getRepository();

        var user1 = new User(firstName(), lastName(), emailAddress(), phoneNumber(), login(), passwordHash(), Account.AccountType.STANDARD);
        var user2 = new User(firstName(), lastName(), emailAddress(), phoneNumber(), login(), passwordHash(), Account.AccountType.STANDARD);
        var user3 = new User(firstName(), lastName(), emailAddress(), phoneNumber(), login(), passwordHash(), Account.AccountType.STANDARD);

        assertDoesNotThrow(() -> userRepo.addUser(user1));
        assertDoesNotThrow(() -> userRepo.addUser(user2));
        assertDoesNotThrow(() -> userRepo.addUser(user3));

        assertDoesNotThrow(() -> {
            var users = userRepo.getUsers();
            assertEquals(3, users.size());
        });
    }

    @Test
    void retrievingExistingUserByIdDoesNotThrow() {
        var userRepo = getRepository();

        var user1 = new User(firstName(), lastName(), emailAddress(), phoneNumber(), login(), passwordHash(), Account.AccountType.STANDARD);

        assertDoesNotThrow(() -> userRepo.addUser(user1));

        assertDoesNotThrow(() -> {
            var user = userRepo.getUserById(user1.getId());
            assert (user.isPresent());
            assertEquals(user.get().getId(), user1.getId());
        });
    }

    @Test
    void retrievingNoneExistingUserByIdDoesNotThrow() {
        var userRepo = getRepository();

        assertDoesNotThrow(() -> {
            var user = userRepo.getUserById(UUID.randomUUID());
            assert (user.isEmpty());
        });
    }

    @AfterAll
    static void Cleanup() {
        var path = Paths.get(System.getProperty("user.dir"));
        for (var db : databaseFiles) {
            var file = new File(Paths.get(path.toString(), db).toString());

            if (!file.delete()) {
                //TODO: Change this to logger
                System.out.println("Cannot delete file");
            }
        }
    }

    private String firstName() {
        return FAKER.name().firstName();
    }


    private String lastName() {
        return FAKER.name().lastName();
    }

    private String emailAddress() {
        return FAKER.cat().name().toLowerCase(Locale.ROOT) + "@local.com";
    }

    private String phoneNumber() {
        return FAKER.phoneNumber().phoneNumber();
    }

    private String login() {
        return FAKER.cat().name();
    }

    private String passwordHash() {
        return FAKER.regexify("[a-z]{128}");
    }
}
