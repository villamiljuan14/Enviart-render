import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon-2x.png',
    iconUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-icon.png',
    shadowUrl: 'https://unpkg.com/leaflet@1.7.1/dist/images/marker-shadow.png',
});

/**
 * Controlador del mapa de tracking
 * Gestiona la visualización del mapa Leaflet y la integración con TrackingClient
 * Dependencias: Leaflet (L), TrackingClient
 */
class TrackingMap {
    constructor(mapId, pedidoId) {
        this.mapId = mapId;
        this.pedidoId = pedidoId;
        this.map = null;
        this.marker = null;
        this.routePolyline = null;
        this.routePath = []; // Array de [lat, lng]
        this.trackingClient = new TrackingClient();

        // Icono personalizado para el repartidor
        this.driverIcon = L.icon({
            iconUrl: '/images/delivery-scooter.png', // Asegúrate de tener esta imagen o usa default
            iconSize: [32, 32],
            iconAnchor: [16, 16],
            popupAnchor: [0, -16]
        });
    }

    /**
     * Inicializar mapa y conexión
     */
    init() {
        // Inicializar mapa centrado en Bogotá (default)
        this.map = L.map(this.mapId).setView([4.7110, -74.0721], 13);

        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors'
        }).addTo(this.map);

        // Inicializar capa de ruta
        this.routePolyline = L.polyline([], {
            color: 'blue',
            weight: 4,
            opacity: 0.7,
            dashArray: '10, 10',
            lineCap: 'round'
        }).addTo(this.map);

        console.log('Mapa inicializado');
        this.connectTracking();
    }

    /**
     * Conectar al servicio de tracking
     */
    connectTracking() {
        this.trackingClient.connect(
            () => {
                console.log('Conectado al servicio de tracking');
                this.updateStatus('Conectado', 'success');

                // Suscribirse a actualizaciones
                this.trackingClient.subscribeToPedido(this.pedidoId, (data) => {
                    this.updateLocation(data);
                });

                // Cargar historial inicial (opcional, si implementas API de historial)
                // this.loadHistory();
            },
            (error) => {
                console.error('Error de conexión:', error);
                this.updateStatus('Desconectado', 'error');
            }
        );
    }

    /**
     * Actualizar ubicación en el mapa
     */
    updateLocation(data) {
        const lat = parseFloat(data.latitud);
        if (this.markers[data.pedidoId]) {
            this.markers[data.pedidoId].setLatLng(latlng);
            this.markers[data.pedidoId].getPopup().setContent(this.createPopupContent(data));
        } else {
            // Crear nuevo marker
            const marker = L.marker(latlng).addTo(this.map);
            marker.bindPopup(this.createPopupContent(data));
            this.markers[data.pedidoId] = marker;

            // Abrir popup automáticamente
            marker.openPopup();
        }

        // Centrar mapa en la ubicación si está habilitado
        if (this.options.autoCenter) {
            this.map.setView(latlng, this.options.zoom);
        }

        console.log(`✓ Marker actualizado en [${lat}, ${lon}]`);
    }

    /**
     * Crear contenido HTML para el popup del marker
     */
    createPopupContent(data) {
        const velocidad = data.velocidad ? `${data.velocidad} km/h` : 'N/A';
        const fecha = data.ultimaActualizacion
            ? new Date(data.ultimaActualizacion).toLocaleString('es-CO')
            : 'N/A';

        return `
            <div style="min-width: 200px;">
                <h4 style="margin: 0 0 8px 0; font-weight: bold;">
                    📦 ${data.numeroGuia || 'Pedido #' + data.pedidoId}
                </h4>
                <p style="margin: 4px 0;">
                    <strong>Estado:</strong> 
                    <span style="color: #4F46E5;">${data.estado || 'N/A'}</span>
                </p>
                <p style="margin: 4px 0;">
                    <strong>Velocidad:</strong> ${velocidad}
                </p>
                <p style="margin: 4px 0; font-size: 0.85em; color: #666;">
                    Última actualización:<br>${fecha}
                </p>
            </div>
        `;
    }

    /**
     * Cargar y mostrar ruta histórica del pedido
     */
    async loadRouteHistory(pedidoId) {
        try {
            const response = await fetch(`/api/tracking/pedido/${pedidoId}/historial`);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            const locations = await response.json();
            console.log(`Historial cargado: ${locations.length} puntos`);

            if (locations.length > 1) {
                // Convertir a array de coordenadas [lat, lon]
                const latlngs = locations
                    .map(loc => [parseFloat(loc.latitud), parseFloat(loc.longitud)])
                    .filter(coords => !isNaN(coords[0]) && !isNaN(coords[1]));

                // Crear o actualizar polyline
                if (this.polylines[pedidoId]) {
                    this.polylines[pedidoId].setLatLngs(latlngs);
                } else {
                    const polyline = L.polyline(latlngs, {
                        color: '#4F46E5',
                        weight: 3,
                        opacity: 0.7,
                        dashArray: '10, 5'
                    }).addTo(this.map);

                    this.polylines[pedidoId] = polyline;
                }

                // Ajustar vista al bounds de la ruta
                if (latlngs.length > 0) {
                    const bounds = L.latLngBounds(latlngs);
                    this.map.fitBounds(bounds, { padding: [50, 50] });
                }

                console.log('✓ Ruta histórica dibujada');
            }
        } catch (error) {
            console.error('Error cargando ruta histórica:', error);
        }
    }

    /**
     * Detener tracking y limpiar
     */
    stopTracking(pedidoId) {
        console.log(`Deteniendo tracking para pedido ${pedidoId}`);

        this.trackingClient.unsubscribeFromPedido(pedidoId);

        // Opcional: limpiar markers y polylines
        if (this.markers[pedidoId]) {
            this.map.removeLayer(this.markers[pedidoId]);
            delete this.markers[pedidoId];
        }

        if (this.polylines[pedidoId]) {
            this.map.removeLayer(this.polylines[pedidoId]);
            delete this.polylines[pedidoId];
        }
    }

    /**
     * Destruir mapa y desconectar WebSocket
     */
    destroy() {
        console.log('Destruyendo mapa y desconectando WebSocket');

        this.trackingClient.disconnect();

        if (this.map) {
            this.map.remove();
            this.map = null;
        }
    }
}

export default TrackingMap;
