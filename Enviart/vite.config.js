import { defineConfig } from 'vite';
import path from 'path';

export default defineConfig({
    build: {
        outDir: 'src/main/resources/static/assets',
        emptyOutDir: false, // Don't delete other assets
        lib: {
            entry: 'src/main/resources/static/js/home.js',
            name: 'bundle',
            fileName: (format) => `bundle.js`,
            formats: ['umd'] // Universal Module Definition for browser compatibility
        },
        rollupOptions: {
            // Ensure external dependencies are bundled or handled correctly
            // For a simple bundle.js used in a script tag, we usually want everything bundled.
            // If we wanted to keep them external, we'd list them here.
        }
    },
    define: {
        'process.env.NODE_ENV': '"production"' // Fix for some libs that expect process.env
    }
});
