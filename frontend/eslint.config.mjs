import js from "@eslint/js";
import globals from "globals";
import tseslint from "typescript-eslint";
import pluginVue from "eslint-plugin-vue";

export default tseslint.config(
  { ignores: ["*.config.js", "dist/"] }, // No pierdas tiempo escaneando dist o configs viejos
  js.configs.recommended,
  ...tseslint.configs.recommended,
  ...pluginVue.configs["flat/essential"],
  {
    files: ["**/*.{js,mjs,ts,vue}"],
    languageOptions: {
      globals: globals.browser,
      parserOptions: {
        parser: tseslint.parser, // Mantiene a TS feliz dentro de los <script> de Vue
        extraFileExtensions: [".vue"],
        sourceType: "module",
      },
    },
  },
);