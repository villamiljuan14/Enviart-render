import { defineConfig } from 'vite';
import path from 'path';

export default defineConfig({
  root: path.resolve(__dirname, 'Enviart'),
  build: {
    lib: {
      entry: path.resolve(__dirname, 'Enviart/src/main/resources/static/js/home.js'),
      name: 'AppBundle',
      fileName: (format) => 'assets/bundle.js',
    },
    minify: true,
    emptyOutDir: true,
    rollupOptions: {
      treeshake: {
        moduleSideEffects: false,
        propertyReadSideEffects: true,
        tryCatchDeoptimization: false,
      },

    },
  },
});