package com.winkel.qualityevaluation.security;

import com.winkel.qualityevaluation.entity.User;
import com.winkel.qualityevaluation.util.JWTUtil;
import com.winkel.qualityevaluation.util.SecurityResponseUtil;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @ClassName AuthenticationFilter
 * @Description 获取请求头中的Token进行认证
 * @Author zyx
 * @Date 2020/4/14 19:43
 * @Blog www.winkelblog.top
 */
@SuppressWarnings("ALl")
public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final String TOKEN_HEADER = "Authorization";

    private final String STARTS_WITH = "Bearer ";

    protected AuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationSuccessHandler authenticationSuccessHandler) {
        //拦截的url
        super(new AntPathRequestMatcher("/auth/login", "POST"));
        super.setAuthenticationManager(authenticationManager);
        super.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        super.setAuthenticationFailureHandler(new MyAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        System.out.println("进入AuthenticationFilter");

        AuthenticationToken authenticationToken;
        try {
            String authorization = request.getHeader(TOKEN_HEADER);
            String token = authorization.substring(STARTS_WITH.length());
            //只包含usename password
            User tokenUser = JWTUtil.parseJWTUser(token);

            if (tokenUser == null || !StringUtils.isNotBlank(tokenUser.getUsername()) || !StringUtils.isNotBlank(tokenUser.getPassword())) {
                SecurityResponseUtil.fail(response, 401, "Token解析用户错误");
                return null;
            }
            authenticationToken = new AuthenticationToken(token, tokenUser);
            System.out.println("解析token成功");
        } catch (NonceExpiredException e) {
            SecurityResponseUtil.fail(response, 401, "Token过期");
            return null;
        } catch (SignatureException | MalformedJwtException e) {
            SecurityResponseUtil.fail(response, 401, "Token解析错误");
            return null;
        } catch (Exception e) {
            System.out.println(e.getClass());
            e.printStackTrace();
            SecurityResponseUtil.fail(response, 401, "未知登录错误");
            return null;
        }

//        AuthenticationToken authenticationToken = (AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        //交给AuthenticationManager处理
        return this.getAuthenticationManager().authenticate(authenticationToken);

//        return null;
    }

    private AuthenticationToken getAuthenticationTokenFromRequest(HttpServletRequest request) {
        boolean hasAuthorization = StringUtils.isNotBlank(request.getHeader(TOKEN_HEADER)) && (request.getHeader(TOKEN_HEADER).startsWith(STARTS_WITH));
        if (hasAuthorization) {
            System.out.println("获取到token");
            String token = request.getHeader(TOKEN_HEADER).substring(STARTS_WITH.length());
            return new AuthenticationToken(token);
        }
        System.out.println("token获取失败");
        return null;
    }

}
