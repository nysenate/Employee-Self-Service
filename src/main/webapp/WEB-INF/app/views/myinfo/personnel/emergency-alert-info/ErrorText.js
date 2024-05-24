import React from "react"


export default function ErrorText({ id, errors }) {
  if (errors[id]?.message) {
    return (
      <div className="inline-block">
        <p className="pl-1 mt-0.5 text-red-600 inline-block">{errors[id]?.message}</p>
      </div>
    )
  }

  return null
}