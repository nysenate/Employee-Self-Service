import React, { useEffect, useState } from "react";
import { useDebounce } from "use-debounce";
import { twMerge } from "tailwind-merge";

export default function InputDebounced({
  id,
  value,
  type = "text",
  placeholder = "",
  onChange,
  delay = 500,
  className = "",
  min, // optional, only used if type="date".
  max, // optional, only used if type="date".
}) {
  const [term, setTerm] = useState(value || "");
  const [debouncedTerm] = useDebounce(term, delay);
  const classes = twMerge(className, "input");

  useEffect(() => {
    setTerm(value || "");
  }, [value]);

  useEffect(() => {
    onChange(debouncedTerm);
  }, [debouncedTerm]);

  return (
    <div>
      <input
        id={id}
        name={id}
        type={type}
        autoComplete="off"
        value={term}
        placeholder={placeholder}
        onChange={(e) => setTerm(e.target.value)}
        className={classes}
        {...(type === "date" ? { min, max } : {})}
      />
    </div>
  );
}
