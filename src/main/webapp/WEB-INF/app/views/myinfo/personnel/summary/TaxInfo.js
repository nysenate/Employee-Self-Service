import React from "react"
import SummarySection from "app/views/myinfo/personnel/summary/SummarySection";
import { toCurrency } from "app/utils/textUtils";

export function FederalTax({ transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>
        Federal Tax
      </SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Exemptions</SummarySection.Cell>
          <SummarySection.Cell>{transactions["NUFEDTAXEX"].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Reported Marital Status</SummarySection.Cell>
          <SummarySection.Cell>{transactions["CDMARITALFED"].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Additional Withheld</SummarySection.Cell>
          <SummarySection.Cell>{toCurrency(transactions["MOADDFEDTAX"].value || 0)}</SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  )
}

export function StateTax({ transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>
        State Tax
      </SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Exemptions</SummarySection.Cell>
          <SummarySection.Cell>{transactions["NUSTATTAXEX"].value || 0}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Reported Marital Status</SummarySection.Cell>
          <SummarySection.Cell>{transactions["CDMARITALST"].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Additional Withheld</SummarySection.Cell>
          <SummarySection.Cell>{toCurrency(transactions["MOADDSTATTAX"].value || 0)}</SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  )
}

export function NewYorkCityTax({ transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>
        New York City Tax
      </SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Exemptions</SummarySection.Cell>
          <SummarySection.Cell>{transactions["NUCITYTAXEX"].value || 0}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Reported Marital Status</SummarySection.Cell>
          <SummarySection.Cell>{transactions["CDMARITALNYC"].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Additional Withheld</SummarySection.Cell>
          <SummarySection.Cell>{toCurrency(transactions["MOADDCITYTAX"].value || 0)}</SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  )
}

export function YonkersTax({ transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>
        Yonkers Tax
      </SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Exemptions</SummarySection.Cell>
          <SummarySection.Cell>{transactions["NUYONTAXEX"].value || 0}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Reported Marital Status</SummarySection.Cell>
          <SummarySection.Cell>{transactions["CDMARITALYON"].value}</SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Additional Withheld</SummarySection.Cell>
          <SummarySection.Cell>{toCurrency(transactions["MOADDYONTAX"].value || 0)}</SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  )
}
