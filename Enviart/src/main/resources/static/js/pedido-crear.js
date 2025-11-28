let currentStep = 1;

function nextStep(step) {
    // Validar campos del paso actual antes de avanzar
    const currentStepContent = document.getElementById('stepContent' + currentStep);
    const inputs = currentStepContent.querySelectorAll('input, select, textarea');
    let isValid = true;

    inputs.forEach(input => {
        if (!input.checkValidity()) {
            isValid = false;
            input.reportValidity();
            return;
        }
    });

    if (!isValid) {
        return;
    }

    // Ocultar paso actual
    document.getElementById('stepContent' + currentStep).classList.add('hidden');

    // Marcar paso actual como completado
    document.getElementById('step' + currentStep).classList.remove('step-active');
    document.getElementById('step' + currentStep).classList.add('step-completed');

    // Mostrar nuevo paso
    document.getElementById('stepContent' + step).classList.remove('hidden');

    // Marcar nuevo paso como activo
    document.getElementById('step' + step).classList.remove('step-inactive');
    document.getElementById('step' + step).classList.add('step-active');

    currentStep = step;
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function prevStep(step) {
    // Ocultar paso actual
    document.getElementById('stepContent' + currentStep).classList.add('hidden');

    // Marcar paso actual como inactivo
    document.getElementById('step' + currentStep).classList.remove('step-active');
    document.getElementById('step' + currentStep).classList.add('step-inactive');

    // Mostrar paso anterior
    document.getElementById('stepContent' + step).classList.remove('hidden');

    // Marcar paso anterior como activo
    document.getElementById('step' + step).classList.remove('step-completed');
    document.getElementById('step' + step).classList.add('step-active');

    currentStep = step;
    window.scrollTo({ top: 0, behavior: 'smooth' });
}
