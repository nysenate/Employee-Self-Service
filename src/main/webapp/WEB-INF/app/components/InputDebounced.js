import React, { useEffect, useState, useRef } from "react";
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
  const [isInvalid, setIsInvalid] = useState(false);
  const inputRef = useRef(null);

  useEffect(() => {
    setTerm(value || "");
  }, [value]);

  useEffect(() => {
    const isWithinDateRange =
      type !== "date" ||
      !debouncedTerm ||
      ((!min || debouncedTerm >= min) && (!max || debouncedTerm <= max));

    if (isWithinDateRange) {
      onChange(debouncedTerm);
    }
  }, [debouncedTerm, min, max, type]);

  // A date can become valid or invalid when its range changes.
  useEffect(() => {
    if (inputRef.current) {
      setIsInvalid(!inputRef.current.checkValidity());
    }
  }, [term, min, max]);

  // Check validity on every change
  const handleInputChange = (e) => {
    const newValue = e.target.value;
    setTerm(newValue);

    if (inputRef.current) {
      setIsInvalid(!inputRef.current.checkValidity());
    }
  };

  const classes = twMerge(
    className,
    "input",
    isInvalid ? "input--invalid" : "",
  );

  return (
    <div>
      <input
        ref={inputRef}
        id={id}
        name={id}
        type={type}
        autoComplete="off"
        value={term}
        placeholder={placeholder}
        onChange={handleInputChange}
        className={classes}
        {...(type === "date" ? { min, max } : {})}
      />
    </div>
  );
}
