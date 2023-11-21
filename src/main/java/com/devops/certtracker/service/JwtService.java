package com.devops.certtracker.service;

import com.devops.certtracker.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private static  final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${application.security.jwt.secretKey}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private int jwtExpiration;

    @Value("${application.security.jwt.cookieName}")
    private String jwtCookie;

    @Value("${application.security.jwt.refresh.cookieName}")
    private String jwtRefreshCookie;

    public ResponseCookie generateJwtCookie(UserDetailsImpl principal){
        String jwt = generateTokenFromUserName(principal.getUsername());
        return generateCookie(jwtCookie, jwt, "/api");
    }

    public ResponseCookie generateJwtCookie(User user){
        String jwt = generateTokenFromUserName(user.getEmail());
        return generateCookie(jwtCookie, jwt ,"/api");
    }

    public ResponseCookie generateRefreshJwtCookie(String refreshToken){
        return generateCookie(jwtRefreshCookie, refreshToken, "/api/auth");
    }

    public String getJwtFromCookies(HttpServletRequest request){
        return getCookieValueByName(request, jwtCookie);
    }

    public String getJwtRefreshFromCookies(HttpServletRequest request){
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    public ResponseCookie getCleanJwtCookie(){
        return ResponseCookie.from(jwtCookie, null).path("/api").maxAge(0).build();
    }

    public ResponseCookie getCleanJwtRefreshCookies(){
        return ResponseCookie.from(jwtRefreshCookie, null).path("/api/auth").maxAge(0).build();
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public boolean validateJwtToken(String authenticationToken){
        try{
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authenticationToken);
            return true;
        }catch (MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        }catch (ExpiredJwtException e){
            logger.error("JWT token is expired: {}", e.getMessage());
        }catch(UnsupportedJwtException e){
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }catch(IllegalArgumentException e){
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private String generateTokenFromUserName(String userEmail){
        return Jwts.builder()
                .setSubject(userEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private ResponseCookie generateCookie(String name, String value, String path){
        return ResponseCookie.from(name, value).path(path).maxAge(24 * 60 * 60).sameSite("None").httpOnly(true).secure(true).build();
    }

    private String getCookieValueByName(HttpServletRequest request, String name){
        Cookie cookie = WebUtils.getCookie(request, name);

        return cookie != null ? cookie.getValue():null;
    }

}

