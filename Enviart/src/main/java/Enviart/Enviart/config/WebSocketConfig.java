package Enviart.Enviart.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * Configuración de WebSocket para tracking en tiempo real
 * Habilita comunicación bidireccional entre servidor y clientes
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el broker de mensajes
     * - /topic: Para broadcast a múltiples clientes (ej: tracking de un pedido)
     * - /queue: Para mensajes privados a un cliente específico
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broker simple en memoria para enviar mensajes a clientes suscritos
        config.enableSimpleBroker("/topic", "/queue");

        // Prefijo para mensajes enviados desde clientes al servidor
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra el endpoint WebSocket principal
     * URL: /ws-tracking
     * Incluye fallback con SockJS para compatibilidad con navegadores antiguos
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-tracking")
                .setAllowedOriginPatterns("*") // En producción, especificar dominios exactos
                .withSockJS(); // Fallback si WebSocket no está disponible
    }
}
