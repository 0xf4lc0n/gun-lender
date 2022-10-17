import com.github.javafaker.Faker;
import gunlender.domain.entities.Account;
import gunlender.domain.entities.Gun;
import gunlender.domain.entities.User;
import gunlender.domain.entities.Weapon;
import gunlender.infrastructure.database.GunRepository;
import gunlender.infrastructure.database.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class GunRepositoryTest {
    private static final Faker FAKER = new Faker();
    private static final List<String> databaseFiles = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryTest.class);

    private GunRepository getRepository() {
        var fileName = "gunlender" + UUID.randomUUID() + ".db";
        var connectionString = "jdbc:sqlite:" + fileName;
        var gunRepo = new GunRepository(connectionString);
        gunRepo.migrate();
        databaseFiles.add(fileName);
        return gunRepo;
    }

    @Test
    void insertingNewUserDoesNotThrow() {
        var gunRepo = getRepository();
        var gun = new Gun(producer(), model(), weaponType(), caliber(), weight(), length(), amount(), price(), picture());
        assertDoesNotThrow(() -> gunRepo.addGun(gun));
    }

    @Test
    void retrievingAllUsersDoesNotThrow() {
        var gunRepo = getRepository();

        var gun1 = new Gun(producer(), model(), weaponType(), caliber(), weight(), length(), amount(), price(), picture());
        var gun2 = new Gun(producer(), model(), weaponType(), caliber(), weight(), length(), amount(), price(), picture());
        var gun3 = new Gun(producer(), model(), weaponType(), caliber(), weight(), length(), amount(), price(), picture());

        assertDoesNotThrow(() -> gunRepo.addGun(gun1));
        assertDoesNotThrow(() -> gunRepo.addGun(gun2));
        assertDoesNotThrow(() -> gunRepo.addGun(gun3));

        assertDoesNotThrow(() -> {
            var guns = gunRepo.getGuns();
            assertEquals(3, guns.size());
        });
    }

    @Test
    void retrievingExistingUserByIdDoesNotThrow() {
        var gunRepo = getRepository();

        var gun1 = new Gun(producer(), model(), weaponType(), caliber(), weight(), length(), amount(), price(), picture());

        assertDoesNotThrow(() -> gunRepo.addGun(gun1));

        assertDoesNotThrow(() -> {
            var guns = gunRepo.getGunById(gun1.getId());
            assert (guns.isPresent());
            assertEquals(guns.get().getId(), gun1.getId());
        });
    }

    @Test
    void retrievingNoneExistingUserByIdDoesNotThrow() {
        var gunRepo = getRepository();

        assertDoesNotThrow(() -> {
            var gun = gunRepo.getGunById(UUID.randomUUID());
            assert (gun.isEmpty());
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

    private String producer() {
        return FAKER.company().name();
    }

    private String model() {
        return FAKER.dog().name();
    }

    private Weapon.WeaponType weaponType() {
        return switch (FAKER.number().numberBetween(1, 6)) {
            case 1 -> Weapon.WeaponType.PISTOL;
            case 2 -> Weapon.WeaponType.REVOLVER;
            case 3 -> Weapon.WeaponType.SUB_MACHINE_GUN;
            case 4 -> Weapon.WeaponType.CARBINE;
            case 5 -> Weapon.WeaponType.RIFLE;
            default -> Weapon.WeaponType.SHOTGUN;
        };
    }

    private String caliber() {
        return FAKER.cat().name();
    }

    private double weight() {
        return FAKER.number().randomDouble(2, 3, 10);
    }

    private int length() {
        return (int) FAKER.number().randomNumber(3, false);
    }

    private int amount() {
        return (int) FAKER.number().randomNumber(2, false);
    }

    private double price() {
        return FAKER.number().randomDouble(2, 10,  1000);
    }

    private String picture() {
        return FAKER.regexify("https://[a-z]{5,20}.local");
    }
}
