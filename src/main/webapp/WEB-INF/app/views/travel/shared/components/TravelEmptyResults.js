import React from "react";
import { RotateCcw, SearchX } from "lucide-react";
import Button from "app/components/Button";
import Card from "app/components/Card";

export default function TravelEmptyResults({
  itemLabel,
  onResetFilters,
  title,
  description,
}) {
  return (
    <Card className="mt-6">
      <div className="flex flex-col items-center px-4 py-10 text-center">
        <SearchX aria-hidden="true" className="mb-3 h-10 w-10 text-gray-400" />
        <h2 className="text-xl font-semibold">
          {title ?? `No ${itemLabel} match these filters`}
        </h2>
        <p className="mt-1 text-gray-600">
          {description ?? (
            <>
              Try adjusting the filters above
              {onResetFilters ? " or reset them to the defaults." : "."}
            </>
          )}
        </p>
        {onResetFilters && (
          <Button
            variant="secondary"
            className="mt-4"
            contentClassName="gap-2"
            onPress={onResetFilters}
          >
            <RotateCcw aria-hidden="true" className="h-4 w-4" />
            Reset filters
          </Button>
        )}
      </div>
    </Card>
  );
}
