function mostrarAlerta() {
    alert("¡Usuario registrado correctamente!");
}

document.addEventListener("DOMContentLoaded", function () {
    const formulario = document.querySelector("form");
    if (formulario) {
        formulario.addEventListener("submit", function () {
            mostrarAlerta();
        });
    }
});