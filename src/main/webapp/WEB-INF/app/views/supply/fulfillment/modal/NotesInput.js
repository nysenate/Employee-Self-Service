import React from "react";

export default function NotesInput({ register }) {
  return (
    <div className="mt-4 flex items-baseline justify-center gap-2">
      <label htmlFor="note" className="font-light">
        Note:
      </label>
      <textarea
        {...register("note")}
        id="note"
        name="note"
        rows="3"
        className="input w-4/5"
      />
    </div>
  );
}
