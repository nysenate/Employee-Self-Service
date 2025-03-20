import React from "react";
import { useCategories } from "app/views/supply/requisition/useCategories";
import { useSupplyContext } from "app/views/supply/requisition/useSupplyContext";
import {
  clearCategories,
  toggleCategory,
} from "app/views/supply/requisition/itemFilterActions";

export default function CategoryCard({ filterState, dispatch }) {
  const { destination } = useSupplyContext();
  const categoriesQuery = useCategories(destination.locId, filterState.term);

  if (categoriesQuery.isPending) {
    return <></>;
  }

  return (
    <div className="flex flex-col h-[420px] bg-white">
      <div className="mx-3 my-1">
        <a
          className="cursor-pointer"
          onClick={() => dispatch(clearCategories())}
        >
          Clear All
        </a>
      </div>
      <div
        className="mx-3 text-lg"
        style={{ overflowY: "auto", maxHeight: "900px" }}
      >
        <ul>
          {categoriesQuery.data.map((category) => (
            <li key={category} className="flex gap-1 my-0.5">
              <input
                id={category}
                type="checkbox"
                className="cursor-pointer"
                checked={filterState.categories.includes(category)}
                onChange={(e) =>
                  dispatch(toggleCategory(e.target.checked, category))
                }
              />
              <label htmlFor={category} className="cursor-pointer">
                {category}
              </label>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
