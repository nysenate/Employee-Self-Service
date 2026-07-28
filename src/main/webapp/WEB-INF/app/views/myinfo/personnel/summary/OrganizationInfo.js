import React from "react";
import SummarySection from "app/views/myinfo/personnel/summary/SummarySection";
import { txValue } from "app/views/myinfo/personnel/summary/summaryValues";

export default function OrganizationInfo({ emp, transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>Organization Info</SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Resp Center Head</SummarySection.Cell>
          <SummarySection.Cell>
            {emp?.respCtr?.respCenterHead?.name ?? ""}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Work Address</SummarySection.Cell>
          <SummarySection.Cell>{workAddress(emp)}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Negotiating Unit</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "CDNEGUNIT")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Job Title</SummarySection.Cell>
          <SummarySection.Cell>
            {emp?.jobTitle ?? ""}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>T&A Supervisor</SummarySection.Cell>
          <SummarySection.Cell>
            {supervisorName(transactions)}
          </SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  );
}

function workAddress(emp) {
  const addr = emp?.workAddress;
  if (!addr) {
    return "";
  }

  const street = [addr.addr1, addr.addr2].filter(Boolean).join(", ");
  const cityStateZip = [addr.city, addr.state, addr.zip5]
    .filter(Boolean)
    .join(" ");

  return [street, cityStateZip].filter(Boolean).join(", ");
}

function supervisorName(transactions) {
  return [
    txValue(transactions, "NAFIRSTSUP"),
    txValue(transactions, "NALASTSUP"),
  ]
    .filter(Boolean)
    .join(" ");
}
