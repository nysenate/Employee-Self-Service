import React from "react"
import SummarySection from "app/views/myinfo/personnel/summary/SummarySection";

export default function PersonnelInfo({ emp, transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>
        Personnel Info
      </SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Email</SummarySection.Cell>
          <SummarySection.Cell>{emp.email}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Work Phone</SummarySection.Cell>
          <SummarySection.Cell>{emp.workPhone}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Home Phone</SummarySection.Cell>
          <SummarySection.Cell>{emp.homePhone}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Address Line 1</SummarySection.Cell>
          <SummarySection.Cell>{transactions['ADSTREET1'].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Address Line 2</SummarySection.Cell>
          <SummarySection.Cell>{transactions['ADSTREET2'].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>City</SummarySection.Cell>
          <SummarySection.Cell>{transactions['ADCITY'].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>State</SummarySection.Cell>
          <SummarySection.Cell>{transactions['ADSTATE'].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Zip</SummarySection.Cell>
          <SummarySection.Cell>{transactions['ADZIPCODE'].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Marital Status</SummarySection.Cell>
          <SummarySection.Cell>{transactions['CDMARITAL'].value}</SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  )
}