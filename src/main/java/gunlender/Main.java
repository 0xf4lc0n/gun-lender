package gunlender;

import gunlender.infrastructure.database.AmmoRepository;
import gunlender.infrastructure.database.GunRepository;
import gunlender.infrastructure.database.UserRepository;
import io.javalin.Javalin;
import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) {
        var logger = LoggerFactory.getLogger(Main.class);
        var connectionStr = "jdbc:sqlite:gunlender.db";

        var userRepo = new UserRepository(connectionStr);
        var ammoRepo = new AmmoRepository(connectionStr);
        var gunRepo = new GunRepository(connectionStr);

        userRepo.migrate();
        ammoRepo.migrate();
        gunRepo.migrate();

        var app = Javalin.create().get("/", ctx -> ctx.result("Hello world")).start(8080);
    }
}