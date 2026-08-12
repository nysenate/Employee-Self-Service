import React, { forwardRef } from "react";

const FormErrorSummary = forwardRef(function FormErrorSummary({ errors }, ref) {
  const entries = Object.entries(errors);
  if (entries.length === 0) return null;

  return (
    <div
      ref={ref}
      role="alert"
      tabIndex="-1"
      className="border-l-4 border-red-600 bg-red-50 p-4 text-red-900 outline-none focus:ring-2 focus:ring-red-600"
    >
      <h2 className="font-semibold">Please correct the following:</h2>
      <ul className="mt-2 list-disc space-y-1 pl-5">
        {entries.map(([field, message]) => (
          <li key={field}>
            <a className="underline" href={`#purpose-${field}`}>
              {message}
            </a>
          </li>
        ))}
      </ul>
    </div>
  );
});

export default FormErrorSummary;
