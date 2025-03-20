import React, { useContext } from "react";
import { ThemeContext } from "app/contexts/ThemeContext";
import { twMerge } from "tailwind-merge";

export default function Hero({ children, className }) {
  const theme = useContext(ThemeContext);

  return (
    <div
      className={twMerge(
        `text-3xl text-center text-white py-4 ${heroStyles[theme]}`,
        className,
      )}
    >
      {children}
    </div>
  );
}

const heroStyles = {
  myinfo: "bg-green-600",
  time: "bg-teal-600",
  supply: "bg-purple-600",
  travel: "bg-orange-600",
};
