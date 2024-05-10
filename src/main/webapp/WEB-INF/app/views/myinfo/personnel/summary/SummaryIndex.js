import React from "react"
import useAuth from "app/core/Auth/useAuth";

export default function SummaryIndex() {
  const auth = useAuth();
  // console.log(auth.isAuthed())
  // console.log(auth.empId())
  return (
    <div>
      Employee Summary
    </div>
  )
}
