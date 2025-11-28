// Toggle password visibility
function togglePassword(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icon = document.getElementById(iconId);

    if (input.type === 'password') {
        input.type = 'text';
        icon.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21"/>';
    } else {
        input.type = 'password';
        icon.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>';
    }
}

// Real-time password validation
const passwordInput = document.getElementById('password');
const confirmInput = document.getElementById('passwordConfirm');
const submitBtn = document.getElementById('submitBtn');
const matchError = document.getElementById('password-match-error');

passwordInput.addEventListener('input', validatePassword);
confirmInput.addEventListener('input', checkPasswordMatch);

function validatePassword() {
    const password = passwordInput.value;

    // Length check
    const lengthValid = password.length >= 8;
    updateRequirement('req-length', lengthValid);

    // Upper and lowercase check
    const caseValid = /[a-z]/.test(password) && /[A-Z]/.test(password);
    updateRequirement('req-upper', caseValid);

    // Number check
    const numberValid = /\d/.test(password);
    updateRequirement('req-number', numberValid);

    // Calculate password strength
    calculatePasswordStrength(password);

    checkPasswordMatch();
}

function calculatePasswordStrength(password) {
    const strengthBar = document.getElementById('strength-bar');
    const strengthText = document.getElementById('strength-text');

    if (!password) {
        strengthBar.style.width = '0%';
        strengthBar.style.backgroundColor = '#cbd5e1';
        strengthText.textContent = '-';
        strengthText.style.color = '#94a3b8';
        return;
    }

    let strength = 0;

    // Length contribution (0-40 points)
    if (password.length >= 8) strength += 20;
    if (password.length >= 12) strength += 10;
    if (password.length >= 16) strength += 10;

    // Character variety (0-60 points)
    if (/[a-z]/.test(password)) strength += 15;
    if (/[A-Z]/.test(password)) strength += 15;
    if (/\d/.test(password)) strength += 15;
    if (/[^a-zA-Z0-9]/.test(password)) strength += 15; // Special chars

    // Update UI based on strength
    if (strength < 40) {
        strengthBar.style.width = '33%';
        strengthBar.style.backgroundColor = '#ef4444'; // red
        strengthText.textContent = 'Débil';
        strengthText.style.color = '#ef4444';
    } else if (strength < 70) {
        strengthBar.style.width = '66%';
        strengthBar.style.backgroundColor = '#f59e0b'; // orange
        strengthText.textContent = 'Media';
        strengthText.style.color = '#f59e0b';
    } else {
        strengthBar.style.width = '100%';
        strengthBar.style.backgroundColor = '#10b981'; // green
        strengthText.textContent = 'Fuerte';
        strengthText.style.color = '#10b981';
    }
}

function updateRequirement(id, isValid) {
    const element = document.getElementById(id);
    if (isValid) {
        element.classList.remove('invalid');
        element.classList.add('valid');
    } else {
        element.classList.remove('valid');
        element.classList.add('invalid');
    }
}

function checkPasswordMatch() {
    const password = passwordInput.value;
    const confirm = confirmInput.value;

    if (confirm.length > 0) {
        if (password !== confirm) {
            matchError.classList.remove('hidden');
            submitBtn.disabled = true;
        } else {
            matchError.classList.add('hidden');
            submitBtn.disabled = false;
        }
    } else {
        matchError.classList.add('hidden');
        submitBtn.disabled = false;
    }
}
