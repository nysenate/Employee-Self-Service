import React from "react";
import SummarySection from "app/views/myinfo/personnel/summary/SummarySection";
import { txValue } from "app/views/myinfo/personnel/summary/summaryValues";

export default function PersonnelInfo({ emp, transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>Personnel Info</SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Email</SummarySection.Cell>
          <SummarySection.Cell>{emp?.email ?? ""}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Work Phone</SummarySection.Cell>
          <SummarySection.Cell>{emp?.workPhone ?? ""}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Home Phone</SummarySection.Cell>
          <SummarySection.Cell>{emp?.homePhone ?? ""}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Address Line 1</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "ADSTREET1")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Address Line 2</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "ADSTREET2")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>City</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "ADCITY")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>State</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "ADSTATE")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Zip</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "ADZIPCODE")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Marital Status</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "CDMARITAL")}
          </SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  );
}
