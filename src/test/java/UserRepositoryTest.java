import gunlender.domain.entities.Account;
import gunlender.domain.entities.User;
import gunlender.domain.exceptions.RepositoryException;
import gunlender.infrastructure.database.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;


@Slf4j
class UserRepositoryTest extends BaseRepositoryTest {
    private UserRepository getRepository() throws Exception {
        return (UserRepository) getRepository(UserRepository.class);
    }

    @Test
    void insertingNewUserDoesNotThrow() throws Exception {
        var userRepo = getRepository();
        var user = new User(firstName(), lastName(), emailAddress(), phoneNumber(), login(), passwordHash(), Account.AccountType.STANDARD);
        assertDoesNotThrow(() -> userRepo.addUser(user));
    }

    @Test
    void retrievingAllUsersDoesNotThrow() throws Exception {
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
    void retrievingExistingUserByIdDoesNotThrow() throws Exception {
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
    void retrievingNoneExistingUserByIdDoesNotThrow() throws Exception {
        var userRepo = getRepository();

        assertDoesNotThrow(() -> {
            var user = userRepo.getUserById(UUID.randomUUID());
            assert (user.isEmpty());
        });
    }

    @Test
    void retrievingExistingUserByEmailDoesNotThrow() throws Exception {
        var userRepo = getRepository();

        var user1 = new User(firstName(), lastName(), emailAddress(), phoneNumber(), login(), passwordHash(), Account.AccountType.STANDARD);

        assertDoesNotThrow(() -> userRepo.addUser(user1));

        assertDoesNotThrow(() -> {
            var user = userRepo.getUserByEmail(user1.getEmail());
            assert (user.isPresent());
            assertEquals(user.get().getEmail(), user1.getEmail());
        });
    }

    @Test
    void retrievingNonExistingUserByEmailDoesNotThrow() throws Exception {
        var userRepo = getRepository();

        assertDoesNotThrow(() -> {
            var user = userRepo.getUserByEmail(emailAddress());
            assert (user.isEmpty());
        });
    }

    @Test
    void retrievingExistingUserByLoginDoesNotThrow() throws Exception {
        var userRepo = getRepository();

        var user1 = new User(firstName(), lastName(), emailAddress(), phoneNumber(), login(), passwordHash(), Account.AccountType.STANDARD);

        assertDoesNotThrow(() -> userRepo.addUser(user1));

        assertDoesNotThrow(() -> {
            var user = userRepo.getUserByLogin(user1.getLogin());
            assert (user.isPresent());
            assertEquals(user.get().getLogin(), user1.getLogin());
        });
    }

    @Test
    void retrievingNonExistingUserByLoginDoesNotThrow() throws Exception {
        var userRepo = getRepository();

        assertDoesNotThrow(() -> {
            var user = userRepo.getUserByLogin(login());
            assert (user.isEmpty());
        });
    }

    @Test
    void insertingUserWithExistingEmailAddressThrows() throws Exception {
        var userRepo = getRepository();

        var email = emailAddress();

        var user1 = new User(firstName(), lastName(), email, phoneNumber(), login(), passwordHash(), Account.AccountType.STANDARD);
        var user2 = new User(firstName(), lastName(), email, phoneNumber(), login(), passwordHash(), Account.AccountType.STANDARD);

        assertDoesNotThrow(() ->userRepo.addUser(user1));

        assertThrows(RepositoryException.class,() -> userRepo.addUser(user2));
    }

    @Test
    void insertingUserWithExistingLoginThrows() throws Exception {
        var userRepo = getRepository();

        var login = login();

        var user1 = new User(firstName(), lastName(), emailAddress(), phoneNumber(), login, passwordHash(), Account.AccountType.STANDARD);
        var user2 = new User(firstName(), lastName(), emailAddress(), phoneNumber(), login, passwordHash(), Account.AccountType.STANDARD);

        assertDoesNotThrow(() ->userRepo.addUser(user1));

        assertThrows(RepositoryException.class,() -> userRepo.addUser(user2));
    }

    @Test
    void insertingUserWithExistingPhoneNumberThrows() throws Exception {
        var userRepo = getRepository();

        var phoneNumber = phoneNumber();

        var user1 = new User(firstName(), lastName(), emailAddress(), phoneNumber, login(), passwordHash(), Account.AccountType.STANDARD);
        var user2 = new User(firstName(), lastName(), emailAddress(), phoneNumber, login(), passwordHash(), Account.AccountType.STANDARD);

        assertDoesNotThrow(() ->userRepo.addUser(user1));

        assertThrows(RepositoryException.class,() -> userRepo.addUser(user2));
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
