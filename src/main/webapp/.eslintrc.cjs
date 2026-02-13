module.exports = {
  root: true,
  env: {
    browser: true,
    es2022: true,
    node: true,
  },
  parserOptions: {
    ecmaVersion: "latest",
    sourceType: "module",
    ecmaFeatures: {
      jsx: true,
    },
  },
  ignorePatterns: [
    "assets/help/**",
    "bower_components/**",
    "node_modules/**",
    "pdf.worker.min.js",
  ],
  overrides: [
    {
      files: ["WEB-INF/app/**/*.js"],
      excludedFiles: [
        "WEB-INF/app/components/**/*.js",
        // Legacy direct imports allowed temporarily while migrating.
        "WEB-INF/app/views/myinfo/personnel/pec/OfficeMultiSelect.js",
        "WEB-INF/app/views/supply/fulfillment/components/editModal/RequisitionEditModal.js",
        "WEB-INF/app/views/supply/reconciliation/ReconciliationTabs.js",
        "WEB-INF/app/views/supply/store/shop/components/SelectDestination.js",
        "WEB-INF/app/views/travel/shared/components/InfoPopover.js",
      ],
      rules: {
        "no-restricted-imports": [
          "error",
          {
            paths: [
              {
                name: "@headlessui/react",
                message:
                  "Use shared components from app/components. Direct Headless UI imports are migration-only.",
              },
              {
                name: "react-aria-components",
                message:
                  "Use shared components from app/components instead of direct react-aria-components imports in feature files.",
              },
              {
                name: "react-aria",
                message:
                  "Use shared components from app/components instead of direct react-aria imports in feature files.",
              },
              {
                name: "react-stately",
                message:
                  "Use shared components from app/components instead of direct react-stately imports in feature files.",
              },
            ],
          },
        ],
      },
    },
  ],
};
