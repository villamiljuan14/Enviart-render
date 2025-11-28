package Enviart.Enviart.config;

import Enviart.Enviart.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF para simplificar pruebas iniciales (no
                                                       // recomendado en prod)
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/", "/index", "/login", "/register", "/api/usuarios/registro", "/api/auth/**",
                                "/css/**", "/js/**", "/images/**", "/videos/**", "/robots.txt", "/error",
                                "/favicon.ico")
                        .permitAll()
                        // Rutas restringidas por rol
                        .requestMatchers("/usuarios", "/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/reporte-usuarios", "/vista-usuarios").permitAll()
                        // Rutas de pedidos - las más específicas primero
                        .requestMatchers("/pedidos/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/pedidos/**").authenticated()
                        // Otras rutas requieren autenticación
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login") // Página de login personalizada
                        .permitAll()
                        .defaultSuccessUrl("/home", true) // Redirige a /home después del login
                        .failureUrl("/login?error") // Redirige con error si falla
                        .usernameParameter("email") // Usa 'email' en lugar de 'username'
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para hacer logout
                        .logoutSuccessUrl("/") // Redirige al index después del logout
                        .permitAll())
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
