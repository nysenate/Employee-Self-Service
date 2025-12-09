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

export default function LodgingDetailsPopover({ amendment }) {
  const lodgingPerDiem = amendment?.lodgingPerDiems ?? {};
  const rows = lodgingPerDiem.requestedLodgingPerDiems ?? [];

  if (rows.length === 0) {
    return <span>&nbsp;</span>;
  }

  return (
    <div className="flex items-center">
      <InfoPopover label="Lodging Summary">
        <Table className="[&_td]:px-3 [&_th]:px-3">
          <TableHeader>
            <TableRow>
              <TableHead>Date</TableHead>
              <TableHead>Address</TableHead>
              <TableHead numeric>Lodging Per Diem</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {rows.map((row) => (
              <TableRow key={row.id}>
                <TableCell>{isoToShortDate(row.date)}</TableCell>
                <TableCell className="whitespace-normal">
                  {row.address.formattedAddressWithCounty}
                </TableCell>
                <TableCell
                  numeric
                  className={lodgingPerDiem.isOverridden && "line-through"}
                >
                  {toCurrency(row.rate) || "-"}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
          <TableFooter>
            <TableRow>
              <TableCell colSpan={2}>
                {lodgingPerDiem.isOverridden ? "Overridden to" : "Total"}
              </TableCell>
              <TableCell numeric>
                {toCurrency(lodgingPerDiem.totalPerDiem) || "-"}
              </TableCell>
            </TableRow>
          </TableFooter>
        </Table>
      </InfoPopover>
    </div>
  );
}
