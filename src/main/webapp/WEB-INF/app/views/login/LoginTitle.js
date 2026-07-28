import React from "react";

export default function LoginTitle({ children }) {
  return (
    <h3 className="mb-3 border-b-1 border-gray-300 pb-1 text-xl text-teal-700">
      {children}
    </h3>
  );
}
