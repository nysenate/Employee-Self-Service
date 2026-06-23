import React from "react";
import SummarySection from "app/views/myinfo/personnel/summary/SummarySection";
import { toCurrency } from "app/utils/textUtils";
import {
  formattedTxValue,
  txValue,
} from "app/views/myinfo/personnel/summary/summaryValues";

export function FederalTax({ transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>Federal Tax</SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Exemptions</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "NUFEDTAXEX")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Reported Marital Status</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "CDMARITALFED")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Additional Withheld</SummarySection.Cell>
          <SummarySection.Cell>
            {formattedTxValue(transactions, "MOADDFEDTAX", toCurrency)}
          </SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  );
}

export function StateTax({ transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>State Tax</SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Exemptions</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "NUSTATTAXEX")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Reported Marital Status</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "CDMARITALST")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Additional Withheld</SummarySection.Cell>
          <SummarySection.Cell>
            {formattedTxValue(transactions, "MOADDSTATTAX", toCurrency)}
          </SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  );
}

export function NewYorkCityTax({ transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>New York City Tax</SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Exemptions</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "NUCITYTAXEX")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Reported Marital Status</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "CDMARITALNYC")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Additional Withheld</SummarySection.Cell>
          <SummarySection.Cell>
            {formattedTxValue(transactions, "MOADDCITYTAX", toCurrency)}
          </SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  );
}

export function YonkersTax({ transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>Yonkers Tax</SummarySection.Title>
      <SummarySection.Table>
        <SummarySection.Row>
          <SummarySection.Cell>Exemptions</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "NUYONTAXEX")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Reported Marital Status</SummarySection.Cell>
          <SummarySection.Cell>
            {txValue(transactions, "CDMARITALYON")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Additional Withheld</SummarySection.Cell>
          <SummarySection.Cell>
            {formattedTxValue(transactions, "MOADDYONTAX", toCurrency)}
          </SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  );
}
