/**
 * Theme Toggle - Dark/Light Mode Switch
 * Handles theme persistence and icon switching
 */

// Get elements
const themeToggleBtn = document.getElementById('theme-toggle');
const darkIcon = document.getElementById('theme-toggle-dark-icon');
const lightIcon = document.getElementById('theme-toggle-light-icon');

if (themeToggleBtn && darkIcon && lightIcon) {
    // Check for saved theme preference or default to 'light' mode
    const currentTheme = localStorage.getItem('theme') || 'light';

    // Apply theme on page load
    if (currentTheme === 'dark') {
        document.documentElement.classList.add('dark');
        lightIcon.classList.remove('hidden');
        darkIcon.classList.add('hidden');
    } else {
        document.documentElement.classList.remove('dark');
        darkIcon.classList.remove('hidden');
        lightIcon.classList.add('hidden');
    }

    // Toggle theme on button click
    themeToggleBtn.addEventListener('click', function () {
        // Toggle dark class on html element
        document.documentElement.classList.toggle('dark');

        // Toggle icons
        darkIcon.classList.toggle('hidden');
        lightIcon.classList.toggle('hidden');

        // Save theme preference
        const theme = document.documentElement.classList.contains('dark') ? 'dark' : 'light';
        localStorage.setItem('theme', theme);
    });
}
