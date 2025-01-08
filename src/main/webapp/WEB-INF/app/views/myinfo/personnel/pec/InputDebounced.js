import React, { useState } from "react";
import { useDebounce } from "use-debounce";

export default function InputDebounced({ label, initValue, onChange }) {
  const [term, setTerm] = useState(initValue || '');
  const [debouncedTerm] = useDebounce(term, 500);

  React.useEffect(() => {
    onChange(debouncedTerm)
  }, [debouncedTerm])

  return (
    <div>
      <label className="flex font-light" htmlFor="name">{label}</label>
      <input
        id="name"
        type="text"
        autoComplete="off"
        value={term}
        onChange={e => setTerm(e.target.value)}
        className="input w-64"
      />
    </div>
  );
};
