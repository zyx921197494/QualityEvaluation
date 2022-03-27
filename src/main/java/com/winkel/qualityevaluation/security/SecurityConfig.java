package com.winkel.qualityevaluation.security;


import com.winkel.qualityevaluation.security.authorization.MyAccessDecisionManager;
import com.winkel.qualityevaluation.security.authorization.MyAccessDecisionVoter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.csrf.CsrfFilter;

import java.util.Arrays;
import java.util.List;

/**
 * @ClassName SecurityConfig
 * @Description
 * @Author zyx
 * @Date 2020/4/14 19:35
 * @Blog www.winkelblog.top
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, order = Ordered.HIGHEST_PRECEDENCE)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        AuthenticationFilter authenticationFilter = new AuthenticationFilter(this.authenticationManagerBean(), myAuthenticationSuccessHandler);

        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterAfter((new JwtSecurityContextPersistenceFilter(new MyAuthenticationFailureHandler())), SecurityContextPersistenceFilter.class)
                .addFilterAfter(authenticationFilter, CsrfFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())
                .and()
                .formLogin().failureHandler(new MyAuthenticationFailureHandler())
                .and()
                .logout().logoutSuccessHandler(new MyLogoutSuccessHandler())
                .and().authorizeRequests()
                .antMatchers("/auth/login").permitAll()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/admin/**").hasAnyRole("ADMIN_COUNTY","ADMIN_CITY","ADMIN_PROVINCE","ADMIN_EXPERT")
                .antMatchers("/evaluate/self/**").hasAnyRole("EVALUATE_SELF","EVALUATE_LEADER_SELF")
                .antMatchers("/evaluate/supervise/**").hasAnyRole("EVALUATE_SUPERVISOR", "EVALUATE_LEADER_SUPERVISOR")
                .antMatchers("/evaluate/common/**").hasAnyRole("EVALUATE_SELF","EVALUATE_SUPERVISOR","EVALUATE_LEADER_SELF","EVALUATE_LEADER_SUPERVISOR")
//                .antMatchers("/evaluate/**").hasRole("EVALUATE_SELF")
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .authorizeRequests().accessDecisionManager(accessDecisionManager())
                .and()
                .httpBasic().disable();
    }

    @Bean
    public AccessDecisionManager myAccessDecisionManager() {
        List<AccessDecisionVoter<?>> decisionVoters = Arrays.asList(new MyAccessDecisionVoter(), new WebExpressionVoter(), new AuthenticatedVoter());
        return new MyAccessDecisionManager(decisionVoters);
    }

    @Bean
    public AccessDecisionManager accessDecisionManager(){
        List<AccessDecisionVoter<?>> decisionVoters = Arrays.asList(new MyAccessDecisionVoter(), new WebExpressionVoter(), new AuthenticatedVoter());
        return new UnanimousBased(decisionVoters);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers("/auth/login");
        web.ignoring().mvcMatchers("/auth/login");
    }

}
