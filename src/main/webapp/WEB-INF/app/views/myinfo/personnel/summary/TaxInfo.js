import React from "react";
import SummarySection from "app/views/myinfo/personnel/summary/SummarySection";
import { toCurrency } from "app/utils/textUtils";
import {
  formattedTxValue,
  txValue,
} from "app/views/myinfo/personnel/summary/summaryValues";

function localTaxValue(transactions, key, formatter) {
  if (transactions?.[key]?.value === null) {
    return <span>N/A</span>;
  }

  return formatter
    ? formattedTxValue(transactions, key, formatter)
    : txValue(transactions, key);
}

export function FederalTax({ transactions }) {
  return (
    <SummarySection>
      <SummarySection.Title>Federal Tax</SummarySection.Title>
      <SummarySection.Table className="table-fixed">
        <SummarySection.Row>
          <SummarySection.Cell className="w-3/4">
            Exemptions
          </SummarySection.Cell>
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
      <SummarySection.Table className="table-fixed">
        <SummarySection.Row>
          <SummarySection.Cell className="w-3/4">
            Exemptions
          </SummarySection.Cell>
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
      <SummarySection.Table className="table-fixed">
        <SummarySection.Row>
          <SummarySection.Cell className="w-3/4">
            Exemptions
          </SummarySection.Cell>
          <SummarySection.Cell>
            {localTaxValue(transactions, "NUCITYTAXEX")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Reported Marital Status</SummarySection.Cell>
          <SummarySection.Cell>
            {localTaxValue(transactions, "CDMARITALNYC")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Additional Withheld</SummarySection.Cell>
          <SummarySection.Cell>
            {localTaxValue(transactions, "MOADDCITYTAX", toCurrency)}
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
      <SummarySection.Table className="table-fixed">
        <SummarySection.Row>
          <SummarySection.Cell className="w-3/4">
            Exemptions
          </SummarySection.Cell>
          <SummarySection.Cell>
            {localTaxValue(transactions, "NUYONTAXEX")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Reported Marital Status</SummarySection.Cell>
          <SummarySection.Cell>
            {localTaxValue(transactions, "CDMARITALYON")}
          </SummarySection.Cell>
        </SummarySection.Row>
        <SummarySection.Row>
          <SummarySection.Cell>Additional Withheld</SummarySection.Cell>
          <SummarySection.Cell>
            {localTaxValue(transactions, "MOADDYONTAX", toCurrency)}
          </SummarySection.Cell>
        </SummarySection.Row>
      </SummarySection.Table>
    </SummarySection>
  );
}
