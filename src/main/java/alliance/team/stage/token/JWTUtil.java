package alliance.team.stage.token;

import alliance.team.stage.entity.RoleUtilisateur;
import alliance.team.stage.entity.Utilisateur;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.InvalidKeyException;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;

@Component
public class JWTUtil {
    // Une clé générée avec une taille appropriée (256 bits)
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("allianceFrancaiseDeZiguinchorSenegalLongueCléSécurisée".getBytes(StandardCharsets.UTF_8));
    private final long EXPIRATION_TIME = 3600000; // 1 heure (en millisecondes)

    public String generateToken(Utilisateur utilisateur) {
        try {
            return Jwts.builder()
                    .setSubject(String.valueOf(utilisateur.getId()))
                    .claim("nom", utilisateur.getNom())
                    .claim("prenom", utilisateur.getPrenom())
                    .claim("username", utilisateur.getUsername())
                    .claim("email", utilisateur.getEmail())
                    .claim("role", utilisateur.getRoles().stream()
                            .map(RoleUtilisateur::getLibelle)
                            .collect(Collectors.toList()))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // Utilisation correcte de la clé secrète
                    .compact();
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Erreur lors de la génération du token : Clé secrète invalide", e);
        }
    }

    public String extractEmailFromToken(String token){
        return extractClaims(token).get("email").toString();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'extraction des claims : Token invalide ou expiré", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY) // Utilisation de parserBuilder avec la clé correcte
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Token invalide ou expiré
        }
    }
}