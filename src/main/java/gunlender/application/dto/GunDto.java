package gunlender.application.dto;

import gunlender.domain.entities.Weapon;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class GunDto {
    @NotNull
    private String producer;
    @NotNull
    private String model;
    @NotNull
    private Weapon.WeaponType type;
    @NotNull
    private String caliber;
    private double weight;
    private int length;
    private int amount;
    private double price;
    @NotNull
    private String picture;
}
