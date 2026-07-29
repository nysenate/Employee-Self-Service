import React from "react";
import { cn } from "app/utils/cn";

export default function Controls({ children, className }) {
  return (
    <div className={cn("bg-white p-3", className)}>{children}</div>
  );
}
