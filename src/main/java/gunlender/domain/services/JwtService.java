package gunlender.domain.services;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Key;
import java.util.Calendar;
import java.util.Optional;

public class JwtService {
    private final Logger logger = LoggerFactory.getLogger(JwtService.class);
    private final Key key;

    public JwtService() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateJwt(String userEmail, String role) {
        var date = Calendar.getInstance();
        date.add(Calendar.MINUTE, 30);
        var expTime = date.getTime();
        return Jwts.builder().setSubject(userEmail).claim("Role", role).setExpiration(expTime).signWith(key).compact();
    }


    public boolean verifyJwt(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token);
        } catch (JwtException ex) {
            logger.error("Cannot parse JWT token", ex);
            return false;
        }

        return true;
    }

    public Optional<String> getRole(String token) {
        try {
            var jwt = token.split(" ")[1];
            var claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
            var role = claims.getBody().get("Role", String.class);
            return Optional.of(role);
        } catch (JwtException ex) {
            logger.error("Cannot parse JWT token", ex);
            return Optional.empty();
        }
    }
}
