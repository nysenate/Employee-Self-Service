import React, { StrictMode } from 'react'
import "./app.css"
import { createRoot } from "react-dom/client";
import App from "app/App";


const rootElement = document.getElementById("app");
const root = createRoot(rootElement);

root.render(
  <StrictMode>
    <App/>
  </StrictMode>
);
