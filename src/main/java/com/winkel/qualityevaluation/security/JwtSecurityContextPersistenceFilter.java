package com.winkel.qualityevaluation.security;


import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.exception.TokenParseException;
import com.winkel.qualityevaluation.util.JWTUtil;
import com.winkel.qualityevaluation.util.SecurityResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName JwtSecurityContextPersistenceFilter
 * @Description 检验请求头中是由带有token
 * @Author zyx
 * @Date 2020/4/14 20:00
 * @Blog www.winkelblog.top
 */
//@Component
public class JwtSecurityContextPersistenceFilter extends GenericFilterBean {

    //    @Autowired
    private final MyAuthenticationFailureHandler myAuthenticationFailureHandler;

    /**
     * 过滤器标识
     */
    private static final String FILTER_APPLIED = "__spring_security_jwtscpf_applied";
//    private static final String FILTER_APPLIED = "JSCPF";

    private final String TOKEN_HEADER = "Authorization";

    private final String STARTS_WITH = "Bearer ";

//    public static JwtSecurityContextPersistenceFilter jwtSecurityContextPersistenceFilter;

    //    @Autowired
//    private UserService userService;

//    @PostConstruct
//    public void init(){
//        jwtSecurityContextPersistenceFilter = this;
//        jwtSecurityContextPersistenceFilter.userService = this.userService;
//    }

    public JwtSecurityContextPersistenceFilter(MyAuthenticationFailureHandler myAuthenticationFailureHandler) {
        this.myAuthenticationFailureHandler = myAuthenticationFailureHandler;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        System.out.println("进入JwtSecurityContextPersistenceFilter");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // 确保该过滤器只被执行一次
        if (request.getAttribute(FILTER_APPLIED) != null) {
            chain.doFilter(req, res);
            return;
        }
        // 标识该过滤器已使用过
        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);

        try {
            System.out.println("JwtSecurityContextPersistenceFilter开始");
            String authorization = request.getHeader(TOKEN_HEADER);
            if (StringUtils.isBlank(authorization) || !StringUtils.startsWithIgnoreCase(authorization, STARTS_WITH) || "null".equals(authorization)) {
                System.out.println("token为空");
                throw new NullPointerException();
            }
            String token = authorization.substring(STARTS_WITH.length());
            User tokenUser = JWTUtil.parseJWTUser(token);
            SecurityContextHolder.getContext().setAuthentication(new AuthenticationToken(token, tokenUser));

            System.out.println("识别请求头中token成功");
        } catch (NullPointerException e) {
            SecurityResponseUtil.fail(response, 401, "Token为空或Header_Authorization错误");
            return;
        } catch (ExpiredJwtException e) {
            SecurityResponseUtil.fail(response, 401, "Token已过期");
            return;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            SecurityResponseUtil.fail(response, 401, "Token格式异常");
            return;
        } catch (TokenParseException e) {
            SecurityResponseUtil.fail(response, 401, "Token解析异常");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            SecurityResponseUtil.fail(response, 401, "Token异常");
            return;
        }

        System.out.println("JwtSecurityContextPersistenceFilter执行完毕");
        chain.doFilter(req, res);
    }
}
