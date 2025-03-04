    package com.example.restapi.config;

    import com.example.restapi.service.implementation.CustomUserDetailsService;
    import com.example.restapi.util.JwtTokenUtil;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    @Configuration
    @RequiredArgsConstructor

    public class WebSecurityConfig {

        private final CustomUserDetailsService customUserDetailsService;
        private final JwtTokenUtil jwtTokenUtil;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
            return httpSecurity.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.requestMatchers("/login" , "/register").permitAll().anyRequest().authenticated())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                    .httpBasic(Customizer.withDefaults())
                    .build();
        }
        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
            DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
            authenticationProvider.setUserDetailsService(customUserDetailsService);
            authenticationProvider.setPasswordEncoder(passwordEncoder());
            return authenticationProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
            return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public JwtRequestFilter authenticationJwtTokenFilter() {
            return new JwtRequestFilter(jwtTokenUtil,customUserDetailsService);
        }
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
