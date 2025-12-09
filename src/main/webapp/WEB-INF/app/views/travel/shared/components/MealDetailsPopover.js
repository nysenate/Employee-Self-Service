import React from "react";
import InfoPopover from "app/views/travel/shared/components/InfoPopover";
import {
  Table,
  TableBody,
  TableCell,
  TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from "app/components/ui/table";
import { isoToShortDate } from "app/utils/dateUtils";
import { toCurrency } from "app/utils/textUtils";

export default function MealDetailsPopover({ amendment }) {
  const mealPerDiem = amendment?.mealPerDiems ?? {};
  const rows = mealPerDiem.requestedMealPerDiems ?? [];

  if (rows.length === 0) {
    return <span>&nbsp;</span>;
  }

  return (
    <div className="flex items-center">
      <InfoPopover label="Meal Summary">
        <Table className="[&_td]:px-3 [&_th]:px-3">
          <TableHeader>
            <TableRow>
              <TableHead>Date</TableHead>
              <TableHead>Address</TableHead>
              <TableHead numeric>Breakfast</TableHead>
              <TableHead numeric>Dinner</TableHead>
              <TableHead numeric>Total</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {rows.map((row) => (
              <TableRow key={row.id}>
                <TableCell>{isoToShortDate(row.date)}</TableCell>
                <TableCell className="whitespace-normal">
                  {row.address.formattedAddressWithCounty}
                </TableCell>
                <TableCell numeric>
                  {toCurrency(row.breakfast) || "-"}
                </TableCell>
                <TableCell numeric>{toCurrency(row.dinner) || "-"}</TableCell>
                <TableCell
                  numeric
                  className={mealPerDiem.isOverridden && "line-through"}
                >
                  {toCurrency(row.total) || "-"}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
          <TableFooter>
            <TableRow>
              <TableCell colSpan={4}>
                {mealPerDiem.isOverridden ? "Overridden to" : "Total"}
              </TableCell>
              <TableCell numeric>
                {toCurrency(mealPerDiem.totalPerDiem) || "-"}
              </TableCell>
            </TableRow>
          </TableFooter>
        </Table>
      </InfoPopover>
    </div>
  );
}
