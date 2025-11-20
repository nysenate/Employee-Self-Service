import * as React from "react";

import { cn } from "app/utils/cn";

function Table({ className, ...props }) {
  return (
    <div
      data-slot="table-container"
      className="relative w-full overflow-x-auto"
    >
      <table
        data-slot="table"
        className={cn("w-full caption-bottom text-sm", className)}
        {...props}
      />
    </div>
  );
}

function TableHeader({ className, ...props }) {
  return (
    <thead
      data-slot="table-header"
      className={cn(
        "border-b border-teal-600 [&_tr]:border-b [&_tr]:border-teal-600",
        className,
      )}
      {...props}
    />
  );
}

function TableBody({ className, ...props }) {
  return (
    <tbody
      data-slot="table-body"
      className={cn("[&_tr:last-child]:border-0", className)}
      {...props}
    />
  );
}

function TableFooter({ className, ...props }) {
  return (
    <tfoot
      data-slot="table-footer"
      className={cn(
        "bg-muted/50 border-t font-medium [&>tr]:last:border-b-0",
        className,
      )}
      {...props}
    />
  );
}

function TableRow({ className, ...props }) {
  return (
    <tr
      data-slot="table-row"
      className={cn(
        "hover:bg-muted/50 data-[state=selected]:bg-muted border-b border-gray-300 transition-colors",
        className,
      )}
      {...props}
    />
  );
}

function TableHead({ className, numeric = false, ...props }) {
  return (
    <th
      data-slot="table-head"
      className={cn(
        "text-foreground h-10 px-4 text-left align-middle font-semibold tracking-wide whitespace-nowrap",
        "[&:has([role=checkbox])]:pr-0",
        "[&>[role=checkbox]]:translate-y-[2px]",
        numeric && "text-right",
        className,
      )}
      {...props}
    />
  );
}

function TableCell({ className, numeric = false, ...props }) {
  return (
    <td
      data-slot="table-cell"
      className={cn(
        "px-4 py-2 align-middle whitespace-nowrap",
        "[&:has([role=checkbox])]:pr-0",
        "[&>[role=checkbox]]:translate-y-[2px]",
        numeric && "text-right font-mono tabular-nums",
        className,
      )}
      {...props}
    />
  );
}

function TableCaption({ className, ...props }) {
  return (
    <caption
      data-slot="table-caption"
      className={cn("text-muted-foreground mt-4 text-sm", className)}
      {...props}
    />
  );
}

export {
  Table,
  TableHeader,
  TableBody,
  TableFooter,
  TableHead,
  TableRow,
  TableCell,
  TableCaption,
};
