import React from 'react';


export default function ErrorBanner({ children }) {
  return (
    <div className="bg-red-50 border-2 border-red-300 p-6 text-center">
      {children}
    </div>
  )
}