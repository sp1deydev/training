    package com.gtel.srpingtutorial.configs;

    import com.gtel.srpingtutorial.domains.JwtAuthFilter;
    import lombok.AllArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

    import java.util.ArrayList;
    import java.util.List;

    @Configuration
    @EnableWebSecurity
    @EnableMethodSecurity
    @AllArgsConstructor
    public class SecurityConfig {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder(8);
        }
        private final JwtAuthFilter jwtAuthFilter;

        public static final String[] ADMIN_POST_ENDPOINTS = {
                "/v1/airports",
        };
        public static final String[] ADMIN_PUT_ENDPOINTS = {
                "/v1/airports/{iata}",
        };
        public static final String[] ADMIN_DELETE_ENDPOINTS = {
                "/v1/airports/{iata}",
        };
        public static final String[] ADMIN_PATCH_ENDPOINTS = {
                "/v1/airports/{iata}",
        };
        public static final String[] USER_ENDPOINTS = {
                "/v1/airports",
                "/v1/airports/{iata}"
        };

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


            List<String> publicApis = new ArrayList<>();
            publicApis.add("/actuator/**");
            publicApis.add("/css/**");
            publicApis.add("/favicon.ico");
            publicApis.add("/v1/login");
            publicApis.add("/api/v1/login");
            publicApis.add("/js/**");
            publicApis.add("/images/**");
            String[] array = new String[publicApis.size()];
            publicApis.toArray(array);
            return http
                    // enable csrf protection with cookie
                    .csrf(AbstractHttpConfigurer::disable)
    //                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    // use stateless session management
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .formLogin(AbstractHttpConfigurer::disable)
                    .logout(AbstractHttpConfigurer::disable)
                    .requestCache(AbstractHttpConfigurer::disable)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(auth -> auth

                            //USER ROLE
                            .requestMatchers(HttpMethod.GET, USER_ENDPOINTS).hasAnyRole("ADMIN", "USER")
                            .requestMatchers(HttpMethod.HEAD, USER_ENDPOINTS).hasAnyRole("ADMIN", "USER")

                            //ADMIN ROLE
                            .requestMatchers(HttpMethod.POST, ADMIN_POST_ENDPOINTS).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, ADMIN_PUT_ENDPOINTS).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PATCH, ADMIN_PATCH_ENDPOINTS).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, ADMIN_DELETE_ENDPOINTS).hasRole("ADMIN")

                            // accept all requests to static resources
                            .requestMatchers(array).permitAll()
                            .requestMatchers(HttpMethod.OPTIONS).permitAll()
                            // require authentication for all other requests
                            .anyRequest()
                            .authenticated()
                    ).build();
        }
    }
