package gunlender.infrastructure.database;

import gunlender.application.Repository;
import gunlender.domain.entities.Lending;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LendingRepository implements Repository {
    private static final Logger LOGGER = LoggerFactory.getLogger(LendingRepository.class);
    private final String databaseUrl;

    public LendingRepository(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    public List<Lending> getLendings() {
        var lendings = new ArrayList<Lending>();

        try (var connection = getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.setQueryTimeout(30);

                var rs = statement.executeQuery("select * from lendings");

                while (rs.next()) {
                    lendings.add(Lending.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot get all lending data from database", e);
        }

        return lendings;
    }

    public List<Lending> getLendingByUserId(UUID userId) {
        var lendings = new ArrayList<Lending>();

        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement("select * from lendings where UserId = ?")) {
                statement.setString(1, userId.toString());
                statement.setQueryTimeout(30);

                var rs = statement.executeQuery();

                while (rs.next()) {
                    lendings.add(Lending.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot get lendings with UserId '{}' from database. Reason: {}", userId, e);
        }

        return lendings;
    }

    public List<Lending> getLendingByGunId(UUID gunId) {
        var lendings = new ArrayList<Lending>();

        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement("select * from lendings where GunId = ?")) {
                statement.setString(1, gunId.toString());
                statement.setQueryTimeout(30);

                var rs = statement.executeQuery();

                while (rs.next()) {
                    lendings.add(Lending.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot get lendings with GunId '{}' from database. Reason: {}", gunId, e);
        }

        return lendings;
    }

    public List<Lending> getLendingByAmmoId(UUID ammoId) {
        var lendings = new ArrayList<Lending>();

        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement("select * from lendings where AmmoId = ?")) {
                statement.setString(1, ammoId.toString());
                statement.setQueryTimeout(30);

                var rs = statement.executeQuery();

                while (rs.next()) {
                    lendings.add(Lending.fromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot get lendings with AmmoId '{}' from database. Reason: {}", ammoId, e);
        }

        return lendings;
    }


    public void addLending(Lending lending) {
        try (var connection = getConnection()) {
            try (var statement = connection.prepareStatement("insert into lendings values (?, ?, ?, ?, ?, ?)")) {
                statement.setQueryTimeout(30);

                statement.setString(1, lending.getUserId().toString());
                statement.setString(2, lending.getGunId().toString());
                statement.setString(3, lending.getAmmoId().toString());
                statement.setInt(4, lending.getAmmoAmount());
                statement.setTimestamp(5,  java.sql.Timestamp.from(lending.getReservationDate()));
                statement.setDouble(6, lending.getTotalPrice());

                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot insert lending: '{}' to database.", lending, e);
        }
    }

    public void migrate() {
        try (var connection = getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.setQueryTimeout(30);
                statement.executeUpdate(String.format("create table if not exists lendings %s", Lending.toSqlTableDefinition()));
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot migrate 'lendings' table", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl);
    }
}
