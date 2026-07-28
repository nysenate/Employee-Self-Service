import React from "react";

export default function ContactLabel({ id, children }) {
  return (
    <label
      htmlFor={id}
      className="inline-block w-16 text-right font-semibold text-teal-700"
    >
      {children}
    </label>
  );
}
