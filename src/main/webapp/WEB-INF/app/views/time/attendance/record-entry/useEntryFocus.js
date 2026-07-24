import { useState } from "react";

/**
 * Tracks which time entry field currently holds focus.
 *
 * The entry tables use this for two things the legacy page handled with jQuery: shading the
 * date cell of the row being edited, and keeping the focused field in the tab order even when
 * it would otherwise be skipped.
 */
export default function useEntryFocus() {
  const [focusedField, setFocusedField] = useState(null);

  const onFocus = (entryIndex, field) => {
    setFocusedField(getFieldKey(entryIndex, field));
  };

  const onBlur = (entryIndex, field) => {
    const key = getFieldKey(entryIndex, field);
    // Moving between fields blurs the old one before focusing the new one, so only give up
    // the focused field if nothing else has claimed it in the meantime.
    setTimeout(() => {
      setFocusedField((current) => (current === key ? null : current));
    }, 0);
  };

  return {
    activeEntryIndex: focusedField
      ? parseInt(focusedField.split(":")[0])
      : null,
    isFieldFocused: (entryIndex, field) =>
      focusedField === getFieldKey(entryIndex, field),
    onFocus,
    onBlur,
  };
}

function getFieldKey(entryIndex, field) {
  return `${entryIndex}:${field}`;
}
