package gunlender.server.routes;

import gunlender.domain.services.AuthManager;
import gunlender.infrastructure.database.UserRepository;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class UserController implements CrudHandler {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void create(@NotNull Context ctx) {
        ctx.redirect("/register");
    }

    @Override
    public void delete(@NotNull Context ctx, @NotNull String s) {

    }

    @Override
    public void getAll(@NotNull Context ctx) {
        if (ctx.sessionAttribute("Role") != AuthManager.roleToString(AuthManager.Role.ADMINISTRATOR)) {
            ctx.status(401);
        } else {
            ctx.status(200);
        }
    }

    @Override
    public void getOne(@NotNull Context ctx, @NotNull String s) {

    }

    @Override
    public void update(@NotNull Context ctx, @NotNull String s) {

    }
}
