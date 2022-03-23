package com.winkel.qualityevaluation.util;


import com.winkel.qualityevaluation.entity.Authority;
import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.exception.TokenParseException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultJwtBuilder;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName JWTUtil
 * @Description
 * @Author zyx
 * @Date 2020/4/13 12:34
 * @Blog www.winkelblog.top
 */

@SuppressWarnings("AlibabaCommentsMustBeJavadocFormat")
public class JWTUtil {

    //默认超时时间
    private static long EXPIRATION = 3600L * 1000L * 100;

    //默认key
    private static final String KEY = "JFSOIASJO32IJFJSA74L9KDJFL#FOAS1654";

    //默认issuer
    private static final String ISSUER = "Winkel";

    //默认headers
//    private static Map<String, Object> HEADERS = new HashMap<String, Object>();


//    @Autowired
//    private static RedisTemplate template;


    public static Map<String, Object> createJWT(Map<String, Object> headers, Map<String, Object> claims) {
        if (!claims.containsKey("username") || !claims.containsKey("password")) {
            throw new IllegalArgumentException("未包含用户信息");
        }
        if (headers == null) {
            headers = new HashMap<>();
            headers.put("typ", "JWT");
        } else if (!headers.containsKey("typ")) {
            headers.put("typ", "JWT");
        }
        //对用户信息进行AES加密
//        claims.put("username", AESSecretUtil.encrypt(String.valueOf(claims.get("username")), KEY));
//        claims.put("password", AESSecretUtil.encrypt(String.valueOf(claims.get("password")), KEY));

        claims.put("username", claims.get("username"));
        claims.put("password", claims.get("password"));

        Date nowDate = new Date(System.currentTimeMillis());
        Date expDate = new Date(System.currentTimeMillis() + EXPIRATION);
        DefaultJwtBuilder jwtBuilder = new DefaultJwtBuilder();
        String jwt = jwtBuilder.setHeader(headers).setClaims(claims).signWith(SignatureAlgorithm.HS256, KEY).setIssuedAt(nowDate).setExpiration(expDate).setIssuer(ISSUER).compact();


        Map<String, Object> result = new HashMap<>();
        result.put("JWT", jwt);
        result.put("token_type", TokenType.USER);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        result.put("expiration", formatter.format(expDate));
        result.put("id",claims.get("id"));
        return result;
    }

    //TODO 存入Redis缓存，缓存时间3600L * 1000L * 100
    public static void saveJWT() {
        //        template.opsForValue().set(String.valueOf(claims.get("username")), jwt, Duration.ofMillis(3600L * 1000L * 100));
//        template.opsForValue().set("username",jwt);
    }

    public static Map<String, Object> createJWT(Map<String, Object> claims) {
        return createJWT(null, claims);
    }

    public static String createJWT(Number number) {
        long nowMillis = System.currentTimeMillis();
        long ttlMillis = nowMillis + (3600L * 1000L * 100);
        Date nowDate = new Date(nowMillis);
        Date expDate = new Date(ttlMillis);

        Claims claims = Jwts.claims()
                .setIssuer("winkel")
                .setSubject(String.valueOf(number))
                .setIssuedAt(nowDate)
                .setExpiration(expDate);

        claims.put("userId", number);
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, "winkel");

        return jwtBuilder.compact();
    }

    /**
     * @return boolean
     * @description 检验token是否有效
     * @params [jwt]
     */
    public static boolean validateJWT(String jwt) {
        Jws<Claims> claims = parseJWT(jwt);
        long time = claims.getBody().getExpiration().getTime();
        return time > System.currentTimeMillis();
    }

    public static void invalidateJWT(String jwt) {
        //TODO 删除Redis中的token
    }

    private static Jws<Claims> parseJWT(String jwt) {
        if (StringUtils.isBlank(jwt)) {
            return null;
        }
        return Jwts.parser().setSigningKey(KEY).parseClaimsJws(jwt);
    }

    public static User parseJWTUser(String jwt) {
        if (StringUtils.isNotBlank(jwt)) {
            String username = (String) parseJWT(jwt).getBody().get("username");
            String password = (String) parseJWT(jwt).getBody().get("password");
            String id = (String) parseJWT(jwt).getBody().get("id");
            if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)){
                return new User(username, password).setId(id);
            }
         throw new TokenParseException("解析Token失败");
        }
        throw new TokenParseException("解析Token失败");
    }

    public static Collection<Authority> parseJWTAuthorities(String jwt) {
        if (StringUtils.isNotBlank(jwt)) {
            Collection<Authority> authorities = (Collection<Authority>) parseJWT(jwt).getBody().get("authorities");
            return authorities.isEmpty() ? null : authorities;
        }
        return null;
    }

    class TokenType {
        final static String USER = "user";
        final static String LOGIN = "login";
        final static String REGISTRY = "register";
        final static String SMS = "sms";
    }

}
