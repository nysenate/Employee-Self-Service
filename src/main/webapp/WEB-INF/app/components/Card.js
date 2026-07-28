import React from "react";
import { cn } from "app/utils/cn";

function Card({ children, className }) {
  return (
    <div
      className={cn(
        "bg-card text-card-foreground shadow",
        "print:shadow-none",
        className,
      )}
    >
      {children}
    </div>
  );
}

function CardHeader({ children, className }) {
  return (
    <div
      className={cn(
        "flex items-center justify-center border-b border-solid border-teal-400 px-3 py-3",
        className,
      )}
    >
      {children}
    </div>
  );
}

function CardTitle({ children, className }) {
  return (
    <span className={cn("text-lg font-semibold", className)}>{children}</span>
  );
}

function CardContent({ children, className }) {
  return <div className={cn("p-3", className)}>{children}</div>;
}

function CardFooter({ children, className }) {
  return (
    <div
      className={cn(
        "mt-3 flex items-center border-t border-teal-400 p-3",
        className,
      )}
    >
      {children}
    </div>
  );
}

Card.Header = CardHeader;
Card.Title = CardTitle;
Card.Content = CardContent;
Card.Footer = CardFooter;

export default Card;
