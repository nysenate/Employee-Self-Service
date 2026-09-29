import React from "react";
import { LoaderCircle } from "lucide-react";
import { cn } from "app/utils/cn";

const sizes = { sm: "size-4", md: "size-5", lg: "size-6" };

/** Decorative only. Use LoadingStatus for an announced message, or inside Button. */
export default function Spinner({ size = "sm", className }) {
  return (
    <LoaderCircle
      aria-hidden="true"
      focusable="false"
      className={cn(
        "shrink-0 animate-spin motion-reduce:animate-none",
        sizes[size],
        className,
      )}
    />
  );
}
