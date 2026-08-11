import React, { useContext } from "react";
import { ThemeContext, themes } from "app/ThemeContext";
import { cn } from "app/utils/cn";

const themeClasses = {
  [themes.myinfo]: "bg-green-600",
  [themes.time]: "bg-teal-800",
  [themes.supply]: "bg-purple-800",
  [themes.travel]: "bg-orange-800",
};

/**
 * Displays a numeric count using the current application's theme.
 */
export default function Badge({
  value,
  hideWhenZero = true,
  className,
  ...props
}) {
  const theme = useContext(ThemeContext);
  const count = Number(value);

  if (!Number.isFinite(count) || count < 0 || (hideWhenZero && count === 0)) {
    return null;
  }

  return (
    <span
      {...props}
      className={cn(
        "inline-flex h-5 min-w-5 shrink-0 items-center justify-center",
        "rounded px-1.5 text-sm leading-none font-semibold text-white tabular-nums",
        themeClasses[theme] ?? "bg-gray-700",
        className,
      )}
    >
      <span className="translate-y-px">{count}</span>
    </span>
  );
}

/**
 * Fetches and displays a badge count.
 *
 * The supplied hook owns the API request and caching behavior. Its data is
 * converted to a number by selectCount.
 */
export function AsyncBadge({
  useData,
  queryArgs = [],
  selectCount = (data) => data,
  ...badgeProps
}) {
  const query = useData(...queryArgs);

  if (query.isPending || query.isError) {
    return null;
  }

  return <Badge value={selectCount(query.data)} {...badgeProps} />;
}
