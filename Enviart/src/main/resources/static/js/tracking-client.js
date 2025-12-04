/**
 * Cliente WebSocket para tracking en tiempo real
 * Manejo de conexión, suscripciones y envío de ubicaciones GPS
 * Dependencias: SockJS, Stomp.js (cargadas vía CDN)
 */
class TrackingClient {
    constructor() {
        this.stompClient = null;
        this.subscriptions = new Map();
        this.connectionStatus = 'disconnected';
    }

    log(msg) {
        console.log(msg);
        this.logToScreen(msg, 'text-green-400');
    }

    error(msg) {
        console.error(msg);
        this.logToScreen(msg, 'text-red-500');
    }

    logToScreen(msg, colorClass) {
        const debugLog = document.getElementById('debugLog');
        if (debugLog) {
            const div = document.createElement('div');
            div.className = colorClass || 'text-gray-300';
            div.textContent = `> ${typeof msg === 'object' ? JSON.stringify(msg) : msg}`;
            debugLog.appendChild(div);
            debugLog.scrollTop = debugLog.scrollHeight;
        }
    }

    /**
     * Conectar al servidor WebSocket
     */
    connect(onConnected, onError) {
        this.log('Iniciando conexión WebSocket...');
        try {
            // Usar SockJS global
            const socket = new SockJS('/ws-tracking');
            // Usar Stomp global
            this.stompClient = Stomp.over(socket);

            // Configurar WebSocket
            this.stompClient.debug = (str) => {
                // this.log('[STOMP] ' + str); // Verbose
            };

            this.stompClient.connect({}, (frame) => {
                this.log('✓ WebSocket conectado: ' + frame);
                this.connectionStatus = 'connected';
                if (onConnected) onConnected();
            }, (error) => {
                this.error('✗ Error WebSocket: ' + error);
                this.connectionStatus = 'error';
                if (onError) onError(error);

                // Reconectar automáticamente después de 5 segundos
                setTimeout(() => {
                    this.log('Intentando reconectar...');
                    this.connect(onConnected, onError);
                }, 5000);
            });
        } catch (error) {
            this.error('✗ Error al crear WebSocket: ' + error);
            if (onError) onError(error);
        }
    }

    /**
     * Suscribirse a actualizaciones de un pedido
     */
    subscribeToPedido(pedidoId, callback) {
        if (!this.stompClient || !this.stompClient.connected) {
            this.error('WebSocket no conectado. Esperando conexión...');
            return null;
        }

        const topic = `/topic/pedido/${pedidoId}`;
        this.log(`→ Suscribiendo a: ${topic}`);

        const subscription = this.stompClient.subscribe(topic, (message) => {
            try {
                const data = JSON.parse(message.body);
                this.log('← Datos recibidos: ' + JSON.stringify(data));
                callback(data);
            } catch (error) {
                this.error('Error procesando mensaje: ' + error);
            }
        });

        this.subscriptions.set(pedidoId, subscription);
        return subscription;
    }

    /**
     * Cancelar suscripción a un pedido
     */
    unsubscribeFromPedido(pedidoId) {
        const subscription = this.subscriptions.get(pedidoId);
        if (subscription) {
            subscription.unsubscribe();
            this.subscriptions.delete(pedidoId);
            this.log(`✓ Desuscrito de pedido ${pedidoId}`);
        }
    }

    /**
     * Enviar actualización de ubicación (para mensajeros)
     */
    sendLocationUpdate(pedidoId, latitude, longitude, speed = 0) {
        if (!this.stompClient || !this.stompClient.connected) {
            this.error('WebSocket no conectado');
            return false;
        }

        const locationData = {
            pedidoId: pedidoId,
            latitud: latitude,
            longitud: longitude,
            velocidad: speed,
            timestamp: new Date().toISOString()
        };

        this.log('→ Enviando ubicación: ' + JSON.stringify(locationData));

        this.stompClient.send(
            '/app/location',
            {},
            JSON.stringify(locationData)
        );

        return true;
    }

    /**
     * Desconectar WebSocket
     */
    disconnect() {
        if (this.stompClient) {
            this.subscriptions.forEach((sub, pedidoId) => {
                sub.unsubscribe();
            });
            this.subscriptions.clear();

            this.stompClient.disconnect(() => {
                this.log('WebSocket desconectado');
                this.connectionStatus = 'disconnected';
            });
        }
    }

    /**
     * Verificar estado de conexión
     */
    isConnected() {
        return this.connectionStatus === 'connected' &&
            this.stompClient &&
            this.stompClient.connected;
    }
}

// Exponer clase globalmente
window.TrackingClient = TrackingClient;
