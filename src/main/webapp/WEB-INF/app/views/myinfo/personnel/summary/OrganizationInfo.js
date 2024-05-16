import React from "react"
import SummarySection from "app/views/myinfo/personnel/summary/SummarySection";

export default function OrganizationInfo({ emp, transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>
        Organization Info
      </SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Resp Center Head</SummarySection.Cell>
          <SummarySection.Cell>{emp.respCtr.respCenterHead.name}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Work Address</SummarySection.Cell>
          <SummarySection.Cell>{workAddress(emp)}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Negotiating Unit</SummarySection.Cell>
          <SummarySection.Cell>{transactions['CDNEGUNIT'].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Job Title</SummarySection.Cell>
          <SummarySection.Cell>{transactions['CDNEGUNIT'].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>T&A Supervisor</SummarySection.Cell>
          <SummarySection.Cell>{transactions['NAFIRSTSUP'].value} {transactions['NALASTSUP'].value}</SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  )
}

function workAddress(emp) {
  const addr = emp.workAddress
  return `${addr.addr1}, ${addr.addr2} ${addr.city}, ${addr.state} ${addr.zip5}`
}
