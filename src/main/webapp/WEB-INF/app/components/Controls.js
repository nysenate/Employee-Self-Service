import React from "react"

export default function Controls({ children, className }) {
  return (
    <div className={`bg-white ${className}`}>
      {children}
    </div>
  )
}