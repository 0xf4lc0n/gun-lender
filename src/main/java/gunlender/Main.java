package gunlender;

import gunlender.domain.exceptions.RepositoryException;
import gunlender.infrastructure.database.AmmoRepository;
import gunlender.infrastructure.database.GunRepository;
import gunlender.infrastructure.database.UserRepository;
import gunlender.server.routes.HealthCheckHandler;
import io.javalin.Javalin;
import org.slf4j.LoggerFactory;

public class Main {
    public static void main(String[] args) {
        var logger = LoggerFactory.getLogger(Main.class);
        var connectionStr = "jdbc:sqlite:gunlender.db";

        var userRepo = new UserRepository(connectionStr);
        var ammoRepo = new AmmoRepository(connectionStr);
        var gunRepo = new GunRepository(connectionStr);

        try {
            userRepo.migrate();
            ammoRepo.migrate();
            gunRepo.migrate();
        } catch (RepositoryException e) {
            logger.error("Cannot migrate repository", e);
            System.exit(1);
        }


        var app = Javalin.create();
        app.get("health_check", new HealthCheckHandler());
        app.start(8080);
    }
}