package calendar;

import calendar.user.User;
import calendar.user.UserDAO;
import calendar.user.UserDAOMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Class WebSecurityConfig
 *
 * @author Axel Nilsson (axnion)
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/api/user/**").hasAuthority("USER")
                    .antMatchers("/api/admin/**").hasAuthority("ADMIN")
                    .antMatchers("/api/super_admin/**").hasAuthority("SUPER_ADMIN")
                    .antMatchers("/api/event/create",
                            "/api/event/detete",
                            "/api/event/update").authenticated()
                    .antMatchers("/api/upload/**").authenticated()
                    .anyRequest().permitAll()
                    .and()
                .formLogin()
                    .loginPage("/")
                    .loginProcessingUrl("/login")
                    .permitAll()
                    .and()
                .logout()
                    .permitAll()
                    .and()
                .csrf()
                    .disable();

//        http
//                .authorizeRequests()
//                .anyRequest().permitAll().and()
//                .csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
                UserDAO dao = new UserDAOMongo();
                User user = dao.getUserByEmail(email);

                if(user != null) {
                    return new org.springframework.security.core.userdetails.User(
                            user.getEmail(),
                            user.getPassword(),
                            user.isValid(),
                            true,
                            true,
                            true,
                            AuthorityUtils.createAuthorityList(user.fetchAuthorities())
                    );
                }
                else {
                    throw new UsernameNotFoundException("Could not find that user");
                }
            }
        };
    }
}
