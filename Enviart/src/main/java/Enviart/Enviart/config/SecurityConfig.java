package Enviart.Enviart.config;

import Enviart.Enviart.security.CustomUserDetailsService;
import Enviart.Enviart.security.TwoFactorAuthenticationSuccessHandler;
import Enviart.Enviart.security.TwoFactorVerificationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    private final TwoFactorAuthenticationSuccessHandler twoFactorAuthenticationSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
            TwoFactorAuthenticationSuccessHandler twoFactorAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.twoFactorAuthenticationSuccessHandler = twoFactorAuthenticationSuccessHandler;
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
                                "/favicon.ico",
                                "/ws-tracking/**", // WebSocket endpoint
                                "/api/tracking/**", // REST API tracking
                                "/tracking-demo") // Página demo tracking
                        .permitAll()
                        // Rutas restringidas por rol
                        .requestMatchers("/usuarios", "/usuarios/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/rutas", "/rutas/**").hasAnyRole("ADMINISTRADOR", "MENSAJERO")
                        .requestMatchers("/vehiculos", "/vehiculos/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/tipos-servicio", "/tipos-servicio/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/tarifas").hasAnyRole("ADMINISTRADOR", "PROVEEDOR")
                        .requestMatchers("/tipos-pago", "/tipos-pago/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/pagos", "/pagos/**").hasAnyRole("ADMINISTRADOR", "PROVEEDOR")
                        .requestMatchers("/novedades", "/novedades/**").hasAnyRole("ADMINISTRADOR", "MENSAJERO")
                        .requestMatchers("/reporte-usuarios", "/vista-usuarios").permitAll()
                        // Rutas de pedidos - las más específicas primero
                        .requestMatchers("/pedidos/admin/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/pedidos/**").authenticated()
                        // Otras rutas requieren autenticación
                        // Otras rutas requieren autenticación
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login") // Página de login personalizada
                        .permitAll()
                        .successHandler(twoFactorAuthenticationSuccessHandler) // Usar handler personalizado para 2FA
                        .failureUrl("/login?error") // Redirige con error si falla
                        .usernameParameter("email") // Usa 'email' en lugar de 'username'
                )
                .addFilterAfter(new TwoFactorVerificationFilter(), UsernamePasswordAuthenticationFilter.class)
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
