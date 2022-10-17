package gunlender.domain.entities;

import java.util.Locale;

public class Account {
    public enum AccountType {
        STANDARD, ADMINISTRATOR,
    }

    public static AccountType FromString(String accountType) {
        return switch (accountType.toLowerCase(Locale.ROOT)) {
            case "standard" -> AccountType.STANDARD;
            case "administrator" -> AccountType.ADMINISTRATOR;
            default -> throw new IllegalArgumentException();
        };
    }
}



