import {
  Table,
  TableBody,
  TableCell,
  TableFooter,
  TableHead,
  TableHeader,
  TableRow,
} from "app/components/ui/table";
import React from "react";
import { toCurrency } from "app/utils/textUtils";
import InfoPopover from "app/views/travel/shared/components/InfoPopover";

/**
 * Displays an Info icon which when clicked reveals a popover containing
 * transportation expense details.
 * If there is no info to display, an empty space is rendered instead of the Info icon.
 * @param amendment
 * @returns {JSX.Element}
 * @constructor
 */
export default function TransportationDetailsPopover({ amendment }) {
  const mileagePerDiem = amendment?.mileagePerDiems ?? {};
  const rows = mileagePerDiem.requestedPerDiems ?? [];

  if (rows.length === 0) {
    return <span>&nbsp;</span>;
  }

  return (
    <div className="flex items-center">
      <InfoPopover label="Transportation Summary">
        <Table className="[&_td]:px-3 [&_th]:px-3">
          <TableHeader>
            <TableRow>
              <TableHead>From</TableHead>
              <TableHead>To</TableHead>
              <TableHead numeric>Rate</TableHead>
              <TableHead numeric>Miles</TableHead>
              <TableHead numeric>Allowance</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {rows.map((row) => (
              <TableRow key={row.id}>
                <TableCell className="whitespace-normal">
                  {row.from.formattedAddressWithCounty}
                </TableCell>
                <TableCell className="whitespace-normal">
                  {row.to.formattedAddressWithCounty}
                </TableCell>
                <TableCell numeric>{row.mileageRate}</TableCell>
                <TableCell numeric>{row.miles}</TableCell>
                <TableCell numeric>
                  {toCurrency(row.requestedPerDiem) || "-"}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
          <TableFooter>
            <TableRow>
              <TableCell colSpan={3}>Total</TableCell>
              <TableCell numeric>{mileagePerDiem.totalMileage}</TableCell>
              <TableCell numeric>
                {toCurrency(mileagePerDiem.totalPerDiem) || "-"}
              </TableCell>
            </TableRow>
          </TableFooter>
        </Table>
      </InfoPopover>
    </div>
  );
}
