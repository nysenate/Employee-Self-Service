import React from "react";
import { useCategories } from "app/views/supply/store/shop/hooks/useCategories";
import { useSupplyContext } from "app/views/supply/store/useSupplyContext";
import {
  clearCategories,
  toggleCategory,
} from "app/views/supply/store/shop/utils/itemFilterActions";

export default function CategoryCard({ filterState, dispatch }) {
  const { destination } = useSupplyContext();
  const categoriesQuery = useCategories(destination.locId, filterState.term);

  if (categoriesQuery.isPending) {
    return <></>;
  }

  return (
    <div className="flex max-h-[calc(100vh-530px)] flex-col bg-white">
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
            <li key={category} className="my-0.5 flex gap-1">
              <input
                id={category}
                type="checkbox"
                className="cursor-pointer"
                checked={filterState.categories.includes(category)}
                onChange={(e) =>
                  dispatch(toggleCategory(e.target.checked, category))
                }
              />
              <label htmlFor={category} className="cursor-pointer font-light">
                {category}
              </label>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
