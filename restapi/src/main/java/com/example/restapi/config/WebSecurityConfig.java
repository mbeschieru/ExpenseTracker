    package com.example.restapi.config;

    import com.example.restapi.entity.ProfileEntity;
    import com.example.restapi.io.AuthResponse;
    import com.example.restapi.repository.IProfileRepository;
    import com.example.restapi.service.implementation.CustomOAuth2ProfileService;
    import com.example.restapi.service.implementation.CustomUserDetailsService;
    import com.example.restapi.service.implementation.TokenBlackListService;
    import com.example.restapi.util.JwtTokenUtil;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.core.userdetails.User;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
    import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
    import org.springframework.security.oauth2.core.user.OAuth2User;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import java.util.ArrayList;
    import java.util.List;

    @Configuration
    @RequiredArgsConstructor

    public class WebSecurityConfig {

        private final CustomUserDetailsService customUserDetailsService;
        private final JwtTokenUtil jwtTokenUtil;
        private final TokenBlackListService tokenBlackListService;
        private final CustomOAuth2ProfileService customOAuth2ProfileService;
        private final IProfileRepository profileRepository;
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
            return httpSecurity.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth.requestMatchers( "/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**","/oauth2/**")
                            .permitAll()
                            .requestMatchers("/expenses/**")
                            .permitAll()
                            .anyRequest().authenticated())
                    .oauth2Login(oauth -> oauth
                            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2ProfileService))
                            .successHandler(OAuth2SuccessHandler())
                    )
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                    .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
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
            return new JwtRequestFilter(jwtTokenUtil,customUserDetailsService,tokenBlackListService);
        }
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        @Bean
        public AuthenticationSuccessHandler OAuth2SuccessHandler () {
            return ((request, response, authentication) -> {
                OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                String email = oAuth2User.getAttribute("email");

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                String token = jwtTokenUtil.generateToken(userDetails);


                String frontendRedirectUri = "http://localhost:3000/oauth/callback?token=" + token + "&email=" + email;

                response.sendRedirect(frontendRedirectUri);
            });
        }
    }
