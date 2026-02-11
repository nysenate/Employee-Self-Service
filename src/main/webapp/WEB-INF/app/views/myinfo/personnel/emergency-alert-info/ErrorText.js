import React from "react";

export default function ErrorText({ id, errors }) {
  if (errors[id]?.message) {
    return (
      <div className="inline-block">
        <p className="mt-0.5 inline-block pl-1 text-red-600">
          {errors[id]?.message}
        </p>
      </div>
    );
  }

  return null;
}
