import React from "react"
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";


export default function SummaryTitle({ emp }) {
  return (
    <>
      <Hero>{emp.fullName}</Hero>
      <Controls className="p-4">
        <div className="text-teal-700 text-center font-semibold">
          If any of the information below is inaccurate, please contact Senate
          Personnel.
        </div>
      </Controls>
    </>
  )
}