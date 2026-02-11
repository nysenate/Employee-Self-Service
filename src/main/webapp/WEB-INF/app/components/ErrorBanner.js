import React from "react";

export default function ErrorBanner({ children }) {
  return (
    <div className="border-2 border-red-300 bg-red-50 p-6 text-center">
      {children}
    </div>
  );
}
