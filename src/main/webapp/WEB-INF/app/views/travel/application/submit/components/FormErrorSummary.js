import React, { forwardRef } from "react";
import ErrorAlert from "app/components/ErrorAlert";

const FormErrorSummary = forwardRef(function FormErrorSummary({ errors }, ref) {
  const entries = Object.entries(errors);
  if (entries.length === 0) return null;

  return (
    <ErrorAlert ref={ref} tabIndex="-1" title="Please correct the following:">
      <ul className="list-disc space-y-1 pl-5">
        {entries.map(([field, message]) => (
          <li key={field}>
            <a className="underline" href={`#purpose-${field}`}>
              {message}
            </a>
          </li>
        ))}
      </ul>
    </ErrorAlert>
  );
});

export default FormErrorSummary;
