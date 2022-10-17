package gunlender.domain.entities;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Getter
public class Gun {
    private UUID id;
    private String producer;
    private String model;
    private Weapon.WeaponType type;
    private String caliber;
    private double weight;
    private int length;
    private int amount;
    private double price;
    private String picture;

    private static final String SQL_DEFINITION = "(Id string, Producer string, Model id, Type string, Caliber string," +
            "Weight double, Length int, Amount int, Price double, Picture string)";

    public Gun(String producer, String model, Weapon.WeaponType type, String caliber, double weight,
               int length, int amount, double price, String picture) {
        this.id = UUID.randomUUID();
        this.producer = producer;
        this.model = model;
        this.type = type;
        this.caliber = caliber;
        this.weight = weight;
        this.length = length;
        this.amount = amount;
        this.price = price;
        this.picture = picture;
    }

    private Gun() {}

    public static Gun fromResultSet(ResultSet rs) throws SQLException {
        var gun = new Gun();

        gun.id = UUID.fromString(rs.getString("Id"));
        gun.producer = rs.getString("Producer");
        gun.model = rs.getString("Model");
        gun.type = Weapon.FromString(rs.getString("Type"));
        gun.caliber = rs.getString("Caliber");
        gun.weight = rs.getDouble("Weight");
        gun.length = rs.getInt("Length");
        gun.amount = rs.getInt("Amount");
        gun.price = rs.getDouble("Price");
        gun.picture = rs.getString("Picture");

        return gun;
    }

    public static String toSqlTableDefinition() {
        return SQL_DEFINITION;
    }

    @Override
    public String toString() {
        return "Gun{" +
                "id=" + id +
                ", producer='" + producer + '\'' +
                ", model='" + model + '\'' +
                ", type=" + type +
                ", caliber='" + caliber + '\'' +
                ", weight=" + weight +
                ", length=" + length +
                ", amount=" + amount +
                ", price=" + price +
                ", picture='" + picture + '\'' +
                '}';
    }
}
