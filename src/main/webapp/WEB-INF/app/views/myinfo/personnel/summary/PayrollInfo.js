import React from "react";
import SummarySection from "app/views/myinfo/personnel/summary/SummarySection";
import { isoToShortDate } from "app/utils/dateUtils";
import { toCurrency } from "app/utils/textUtils";
import {
  formattedTxValue,
  txValue,
} from "app/views/myinfo/personnel/summary/summaryValues";

export default function PayrollInfo({ emp, transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>Payroll Info</SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Pay Type</SummarySection.Cell>
          <SummarySection.Cell>{emp?.payType ?? ""}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>EMPLID</SummarySection.Cell>
          <SummarySection.Cell>{emp?.nid ?? ""}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Continuous Service From</SummarySection.Cell>
          <SummarySection.Cell>
            {formattedTxValue(transactions, "DTCONTSERV", isoToShortDate)}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>
            {emp?.payType === "TE" ? "Hourly Rate" : "Bi Weekly Salary"}
          </SummarySection.Cell>
          <SummarySection.Cell>
            {formattedTxValue(transactions, "MOSALBIWKLY", toCurrency)}
          </SummarySection.Cell>
        </SummarySection.Row>
        {emp?.payType === "TE" && (
          <SummarySection.Row>
            <SummarySection.Cell>Annual Allowance</SummarySection.Cell>
            <SummarySection.Cell>
              {formattedTxValue(transactions, "MOAMTEXCEED", toCurrency)}
            </SummarySection.Cell>
          </SummarySection.Row>
        )}
        <SummarySection.Row>
          <SummarySection.Cell>Direct Deposit</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "CDDIRECTDEPF")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Agency Code</SummarySection.Cell>
          <SummarySection.Cell>{emp?.respCtr?.agencyCode ?? ""}</SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  );
}
