import React from "react";
import { twMerge } from "tailwind-merge";

function Card({ children, className }) {
  return <div className={`bg-white shadow ${className}`}>{children}</div>;
}

function Header({ children, className }) {
  return (
    <div
      className={twMerge(
        "mb-3 flex justify-center border-b-1 border-solid border-teal-400 py-3",
        className,
      )}
    >
      {children}
    </div>
  );
}

Card.Header = Header;

export default Card;
