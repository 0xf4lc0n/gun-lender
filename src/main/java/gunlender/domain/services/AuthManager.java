package gunlender.domain.services;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.security.AccessManager;
import io.javalin.security.RouteRole;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Set;

public class AuthManager implements AccessManager {

    private final JwtService jwtService;

    public AuthManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    @Override
    public void manage(@NotNull Handler handler, @NotNull Context ctx, @NotNull Set<? extends RouteRole> set) throws Exception {
        var userRole = getRole(ctx);

        if (set.contains(userRole)) {
            ctx.sessionAttribute("Role", roleToString(userRole));
            handler.handle(ctx);
        } else {
            ctx.status(401).result("Unauthorized");
        }
    }

    private Role getRole(Context ctx) {
        var jwt = ctx.header("Authorization");

        if (jwt != null) {
            return roleFromString(jwtService.getRole(jwt).orElse("anyone"));
        }

        return Role.ANYONE;
    }

    public enum Role implements RouteRole {
        ANYONE, STANDARD_USER, ADMINISTRATOR
    }

    public static Role roleFromString(String role) {
        return switch (role.toLowerCase(Locale.ROOT)) {
            case "standard" -> Role.STANDARD_USER;
            case "administrator" -> Role.ADMINISTRATOR;
            default -> Role.ANYONE;
        };
    }

    public static String roleToString(Role role) {
        return switch (role) {
            case ADMINISTRATOR -> "administrator";
            case STANDARD_USER -> "standard_user";
            case ANYONE -> "anyone";
        };
    }
}
