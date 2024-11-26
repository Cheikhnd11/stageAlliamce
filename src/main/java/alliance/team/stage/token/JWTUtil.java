package alliance.team.stage.token;

import alliance.team.stage.entity.RoleUtilisateur;
import alliance.team.stage.entity.Utilisateur;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.stream.Collectors;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private final long EXPIRATION_TIME = 3600000; // 1 heure (en millisecondes)

    public String generateToken(Utilisateur utilisateur) {
        return Jwts.builder()
                .setSubject(String.valueOf(utilisateur.getId()))
                .claim("nom", utilisateur.getNom())
                .claim("prenom", utilisateur.getPrenom())
                .claim("username", utilisateur.getUsername())
                .claim("email", utilisateur.getEmail())
                .claim("roles", utilisateur.getRoles().stream()
                        .map(RoleUtilisateur::getLibelle)
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}