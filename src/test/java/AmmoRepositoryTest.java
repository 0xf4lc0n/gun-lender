import com.github.javafaker.Faker;
import gunlender.domain.entities.Ammo;
import gunlender.infrastructure.database.AmmoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
 class AmmoRepositoryTest {
    private static final Faker FAKER = new Faker();
    private static final Random RANDOM = new Random();
    private static final List<String> databaseFiles = new ArrayList<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepositoryTest.class);

    private AmmoRepository getRepository() {
        var fileName = "gunlender" + UUID.randomUUID() + ".db";
        var connectionString = "jdbc:sqlite:" + fileName;
        var ammoRepository = new AmmoRepository(connectionString);
        ammoRepository.migrate();
        databaseFiles.add(fileName);
        return ammoRepository;
    }

    @Test
    void insertingNewUserDoesNotThrow() {
        var ammoRepo = getRepository();
        var ammo = new Ammo(caliber(), amount(), price(), picture());
        assertDoesNotThrow(() -> ammoRepo.addAmmo(ammo));
    }

    @Test
    void retrievingAllUsersDoesNotThrow() {
        var ammoRepo = getRepository();

        var ammo1 = new Ammo(caliber(), amount(), price(), picture());
        var ammo2 = new Ammo(caliber(), amount(), price(), picture());
        var ammo3 = new Ammo(caliber(), amount(), price(), picture());

        assertDoesNotThrow(() -> ammoRepo.addAmmo(ammo1));
        assertDoesNotThrow(() -> ammoRepo.addAmmo(ammo2));
        assertDoesNotThrow(() -> ammoRepo.addAmmo(ammo3));

        assertDoesNotThrow(() -> {
            var ammo = ammoRepo.getAmmo();
            assertEquals(3, ammo.size());
        });
    }

    @Test
    void retrievingExistingUserByIdDoesNotThrow() {
        var ammoRepo = getRepository();

        var ammo1 = new Ammo(caliber(), amount(), price(), picture());

        assertDoesNotThrow(() -> ammoRepo.addAmmo(ammo1));

        assertDoesNotThrow(() -> {
            var ammo = ammoRepo.getAmmoById(ammo1.getId());
            assert (ammo.isPresent());
            assertEquals(ammo.get().getId(), ammo1.getId());
        });
    }

    @Test
    void retrievingNoneExistingUserByIdDoesNotThrow() {
        var ammoRepo = getRepository();

        assertDoesNotThrow(() -> {
            var ammo = ammoRepo.getAmmoById(UUID.randomUUID());
            assert (ammo.isEmpty());
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

    private String caliber() {
        return FAKER.cat().breed();
    }

    private int amount() {
        return RANDOM.nextInt();
    }

    private double price() {
        return RANDOM.nextDouble();
    }

    private String picture() {
        return FAKER.regexify("https://[a-z]{5,20}.local");
    }
}
