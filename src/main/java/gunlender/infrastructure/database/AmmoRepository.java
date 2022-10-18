package gunlender.infrastructure.database;

import gunlender.application.Repository;
import gunlender.domain.entities.Ammo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AmmoRepository implements Repository {
    private static final Logger LOGGER = LoggerFactory.getLogger(AmmoRepository.class);
    private final String databaseUrl;

    public AmmoRepository(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public List<Ammo> getAmmo() {
        var ammo = new ArrayList<Ammo>();

        try (var connection = getConnection()) {
            try (var statement = connection.createStatement()) {

                statement.setQueryTimeout(30);

                var rs = statement.executeQuery("select * from ammo");

                while (rs.next()) {
                    ammo.add(Ammo.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot get ammo from database", e);
        }

        return ammo;
    }

    public Optional<Ammo> getAmmoById(UUID uuid) {
        Optional<Ammo> ammo = Optional.empty();

        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement("select * from ammo where Id = ?")) {
                statement.setString(1, uuid.toString());
                statement.setQueryTimeout(30);

                var rs = statement.executeQuery();

                if (rs.next()) {
                    ammo = Optional.of(Ammo.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot get ammo from database", e);
        }

        return ammo;
    }


    public void addAmmo(Ammo ammo) {
        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement("insert into ammo values (?, ?, ?, ?, ?)")) {
                statement.setQueryTimeout(30);

                statement.setString(1, ammo.getId().toString());
                statement.setString(2, ammo.getCaliber());
                statement.setInt(3, ammo.getAmount());
                statement.setDouble(4, ammo.getPrice());
                statement.setString(5, ammo.getPicture());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot insert ammo to database", e);
        }
    }

    public void migrate() {
        try (var connection = getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.setQueryTimeout(30);
                statement.executeUpdate(String.format("create table if not exists ammo %s", Ammo.toSqlTableDefinition()));
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot migrate 'ammo' table", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }
}
