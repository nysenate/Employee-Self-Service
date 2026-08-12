import { defineConfig } from "vitest/config";
import react from "@vitejs/plugin-react";
import path from "node:path";

export default defineConfig({
  plugins: [react()],
  esbuild: {
    loader: "jsx",
    include: /WEB-INF\/app\/.*\.js$/,
    exclude: [],
  },
  optimizeDeps: {
    esbuildOptions: {
      loader: { ".js": "jsx" },
    },
  },
  resolve: {
    alias: {
      app: path.resolve(__dirname, "WEB-INF/app"),
    },
  },
  test: {
    environment: "jsdom",
    setupFiles: ["./WEB-INF/app/test/setup.js"],
  },
});
