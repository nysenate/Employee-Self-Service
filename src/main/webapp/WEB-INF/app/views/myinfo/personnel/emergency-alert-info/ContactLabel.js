import React from "react"

export default function ContactLabel({ id, children }) {
  return (
    <label htmlFor={id}
           className="inline-block w-16 text-right text-teal-700 font-semibold">
      {children}
    </label>
  )
}