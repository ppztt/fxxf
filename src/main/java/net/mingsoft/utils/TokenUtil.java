package net.mingsoft.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import net.mingsoft.fxxf.entity.User;
import net.mingsoft.shiro.jwt.JwtToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class TokenUtil {

    @Autowired
    @Qualifier("tokenCache")
    private Cache<String, User> tokenCache;

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.issuer}")
    private String issuer;

    @Value("${jwt.token.subject}")
    private String subject;

    @Value("${jwt.token.audience}")
    private String audience;

    @Value("${jwt.token.expiration}")
    private Long expiration;

    /**
     * 刷新token倒计时，默认10分钟，10*60=600
     */
    @Value("${jwt.token.refresh-token-countdown}")
    private Integer refreshTokenCountdown;

    /**
     * 生成token
     *
     * @param username 用户名
     * @return
     */
    public String generateToken(String username) {
        try {
            if (StringUtils.isBlank(username)) {
                log.error("username不能为空");
                return null;
            }
            log.debug("username:{}", username);
            Date expireDate = DateUtils.addSeconds(new Date(), expiration.intValue());
            log.debug("expireDate:{}", expireDate);
            // 生成token
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withClaim(CommonConstant.JWT_USERNAME, username)
                    .withJWTId(UUID.randomUUID().toString().replaceAll("-", ""))              // jwt唯一id
                    .withIssuer(issuer)                         // 签发人
                    .withSubject(subject)                       // 主题
                    .withAudience(audience)                     // 签发的目标
                    .withIssuedAt(new Date())                   // 签名时间
                    .withExpiresAt(expireDate)                  // token过期时间
                    .sign(algorithm);                           // 签名
            return token;
        } catch (Exception e) {
            log.error("generateToken exception", e);
        }
        return null;
    }

    public boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)      // 签发人
                    .withSubject(subject)    // 主题
                    .withAudience(audience)  // 签发的目标
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            if (jwt != null) {
                return true;
            }
        } catch (Exception e) {
            log.error("Verify Token Exception", e);
        }
        return false;
    }

    /**
     * 解析token，获取token数据
     *
     * @param token
     * @return
     */
    public DecodedJWT getJwtInfo(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return decodedJWT;
    }

    /**
     * 根据token获取用户名
     *
     * @param token
     * @return
     */
    public String getUsernameFromToken(String token) {
        if (StringUtils.isBlank(token)) {
            return null;
        }
        DecodedJWT decodedJWT = getJwtInfo(token);
        if (decodedJWT == null) {
            return null;
        }
        String username = decodedJWT.getClaim(CommonConstant.JWT_USERNAME).asString();
        return username;
    }

    /**
     * 获取创建时间
     *
     * @param token
     * @return
     */
    public Date getIssuedAt(String token) {
        DecodedJWT decodedJWT = getJwtInfo(token);
        if (decodedJWT == null) {
            return null;
        }
        return decodedJWT.getIssuedAt();
    }


    /**
     * 判断token失效时间是否到了
     *
     * @param token
     * @return
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 获取设置的token失效时间
     *
     * @param token
     * @return
     */
    public Date getExpirationDateFromToken(String token) {
        DecodedJWT decodedJWT = getJwtInfo(token);
        if (decodedJWT == null) {
            return null;
        }
        return decodedJWT.getExpiresAt();
    }

    /**
     * 判断token是否已过期
     *
     * @param token
     * @return
     */
    public boolean isExpired(String token) {
        Date expireDate = getExpirationDateFromToken(token);
        if (expireDate == null) {
            return true;
        }
        return expireDate.before(new Date());
    }

    public void refreshToken(JwtToken jwtToken, HttpServletResponse httpServletResponse) throws Exception {
        if (jwtToken == null) {
            return;
        }
        String token = jwtToken.getToken();
        if (StringUtils.isBlank(token)) {
            return;
        }
        // 获取过期时间
        Date expireDate = getExpirationDateFromToken(token);
        // 如果(当前时间+倒计时) > 过期时间，则刷新token
        boolean refresh = DateUtils.addSeconds(new Date(), refreshTokenCountdown).after(expireDate);
        if (!refresh) {
            return;
        }
        // 如果token继续发往后台，则提示，此token已失效，请使用新token，不在返回新token，返回状态码：461
        // 如果缓存中没有，JwtToken没有过期，则说明，已经刷新token
        User user = tokenCache.getIfPresent(token);
        if (user == null) {
            httpServletResponse.setStatus(CommonConstant.JWT_INVALID_TOKEN_CODE);
            throw new AuthenticationException("token已无效，请使用已刷新的token");
        }
        String newToken = generateToken(jwtToken.getPrincipal());
        // 更新缓存
        tokenCache.invalidate(token);
        tokenCache.put(newToken, user);
        log.info("刷新token成功，原token:{}，新token:{}", token, newToken);
        httpServletResponse.setStatus(CommonConstant.JWT_REFRESH_TOKEN_CODE);
        httpServletResponse.setHeader(CommonConstant.JWT_DEFAULT_TOKEN_NAME, newToken);
    }


}