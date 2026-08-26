import React, { forwardRef } from "react";
import { CircleAlert } from "lucide-react";
import { twMerge } from "tailwind-merge";

const ErrorAlert = forwardRef(function ErrorAlert(
  {
    children,
    className,
    headingAs: Heading = "h2",
    title = "There was a problem",
    ...props
  },
  ref,
) {
  return (
    <div
      ref={ref}
      role="alert"
      className={twMerge(
        "border-destructive bg-destructive/10 focus:ring-destructive text-foreground border border-l-4 p-4 outline-none focus:ring-2",
        className,
      )}
      {...props}
    >
      <div className="grid grid-cols-[1.25rem_minmax(0,1fr)] items-center gap-x-3 gap-y-1">
        <CircleAlert
          aria-hidden="true"
          className="text-destructive col-start-1 row-start-1 size-5"
        />
        <Heading className="text-foreground col-start-2 row-start-1 font-semibold">
          {title}
        </Heading>
        {children && (
          <div className="col-start-2 row-start-2 min-w-0">{children}</div>
        )}
      </div>
    </div>
  );
});

export default ErrorAlert;
