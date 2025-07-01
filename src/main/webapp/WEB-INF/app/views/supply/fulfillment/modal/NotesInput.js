import React from "react";
import clsx from "clsx";

export default function NotesInput({ register, errors }) {
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
        className={clsx("input w-4/5", errors?.note && "input--invalid")}
      />
    </div>
  );
}
