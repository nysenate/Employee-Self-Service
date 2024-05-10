import React from "react";


function Card({ children, className }) {
  return (
    <div className={`bg-white shadow ${className}`}>
      {children}
    </div>
  )
}

function Header({ children }) {
  return (
    <div className="py-3 mb-3 text-center border-b-1 border-solid border-teal-400">
      {children}
    </div>
  )
}

Card.Header = Header

export default Card
