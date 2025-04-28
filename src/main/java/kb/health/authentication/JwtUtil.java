package kb.health.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //토큰 생성
    public String generateJwtToken(String account, long id ) {
        long now = System.currentTimeMillis();
        long exp = 1000 * 60 * 60;

        return Jwts.builder()
                .setSubject(account)
                .claim("id",id)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now+exp))
                .signWith(key)
                .compact();

    }

    //토큰에서 아이디 추출
    public String getAccountFromJwt(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    //토큰에서 인덱스 추출
    public Long getIdFromJwt(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("id",Long.class);
    }


    //토큰에서 유혀성 검사
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
