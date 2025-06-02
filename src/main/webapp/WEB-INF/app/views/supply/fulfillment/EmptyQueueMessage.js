import React from "react";

export default function EmptyQueueMessage({ children }) {
  return (
    <div className="-mt-3 py-4 text-center text-2xl text-gray-600">
      {children}
    </div>
  );
}
