import React from "react"

export default function LoginDevBanner() {
  return (
    <div className="absolute inset-0 w-full h-24 bg-yellow-600">
      <div className="text-center text-white">
        <h3 className="text-3xl my-3">Running in dev mode</h3>
        <span className="font-medium">Actions performed here will not affect the production database.</span>
      </div>
    </div>
  )
}
