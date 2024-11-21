import React, { useState } from 'react';
import { useDebounce } from "use-debounce";
import { setEmpName } from "app/views/myinfo/personnel/to-do-reporting/todoReportingActions";

export default function EmployeeSearch({ state, dispatch }) {
  const [term, setTerm] = useState('');
  const [debouncedTerm] = useDebounce(term, 500);

  React.useEffect(() => {
    dispatch(setEmpName(debouncedTerm))
  }, [debouncedTerm])

  React.useEffect(() => {
    if (state.name !== term) {
      setTerm(state.name)
    }
  }, [state.name])

  return (
    <div>
      <label className="flex font-light" htmlFor="name">Search by Employee Name</label>
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
