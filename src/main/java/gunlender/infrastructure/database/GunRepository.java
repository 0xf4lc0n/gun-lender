package gunlender.infrastructure.database;

import gunlender.domain.entities.Gun;
import gunlender.domain.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GunRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(GunRepository.class);
    private final String databaseUrl;

    public GunRepository(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public List<Gun> getGuns() {
        var guns = new ArrayList<Gun>();

        try (var connection = getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.setQueryTimeout(30);

                var rs = statement.executeQuery("select * from guns");

                while (rs.next()) {
                    guns.add(Gun.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot get guns from database", e);
        }

        return guns;
    }

    public Optional<Gun> getGunById(UUID uuid) {
        Optional<Gun> gun = Optional.empty();

        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement("select * from guns where Id = ?")) {
                statement.setString(1, uuid.toString());
                statement.setQueryTimeout(30);

                var rs = statement.executeQuery();

                if (rs.next()) {
                    gun = Optional.of(Gun.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot get gun from database", e);
        }

        return gun;
    }


    public void addGun(Gun gun) {
        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement("insert into guns values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                statement.setQueryTimeout(30);

                statement.setString(1, gun.getId().toString());
                statement.setString(2, gun.getProducer());
                statement.setString(3, gun.getModel());
                statement.setString(4, gun.getType().name());
                statement.setString(5, gun.getCaliber());
                statement.setDouble(6, gun.getWeight());
                statement.setInt(7, gun.getLength());
                statement.setInt(8, gun.getAmount());
                statement.setDouble(9, gun.getPrice());
                statement.setString(10, gun.getPicture());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot insert gun: '{}' to database", gun, e);
        }
    }

    public void migrate() {
        try (var connection = getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.setQueryTimeout(30);
                statement.executeUpdate(String.format("create table if not exists guns %s", Gun.toSqlTableDefinition()));
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot migrate 'guns' table", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }
}
