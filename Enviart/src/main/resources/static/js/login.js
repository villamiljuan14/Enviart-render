// Script para ocultar mensaje de sesión cerrada
setTimeout(function () {
    const mensaje = document.getElementById("id_sesioncerrada");
    if (mensaje) {
        mensaje.style.display = "none";
    }
    // También ocultamos el mensaje de registro exitoso después de un tiempo
    const registroMensaje = document.querySelector('[th\\:if="${param.registroExitoso}"]');
    if (registroMensaje) {
        setTimeout(() => {
            registroMensaje.style.display = 'none';
        }, 5000); // Ocultar después de 5 segundos
    }
}, 2000);
