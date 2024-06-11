import React from "react"
import { format, parseISO } from "date-fns";
import { toCurrency } from "app/utils/textUtils";

export default function Paycheck({ summary }) {

  return (
    <div className="p-3">
      <table className="table table--sticky">
        <thead className="">
        <tr className="table__head__row">
          <th className="table__head__cell">Check Date</th>
          <th className="table__head__cell">Pay Period</th>
          <th className="table__head__cell">Gross</th>
          {summary.deductions.map(
            d => <th key={d.code} className="table__head__cell">{formatDeductionHeader(d.description)}</th>)}
          {displayDirectDepositColumn(summary) && <th className="table__head__cell">Direct Deposit</th>}
          {displayCheckColumn(summary) && <th className="table__head__cell">Check</th>}
        </tr>
        </thead>
        <tbody className="table__body table__body--striped table__body--highlight">
        {summary.paychecks.map((p, i) =>
          <tr key={p.payPeriod} className="table__row">
            <td className="table__cell">{format(parseISO(p.checkDate), 'M/dd/yyyy')}</td>
            <td className="table__cell">{p.payPeriod}</td>
            <td className={`table__cell table__cell--right ${isSignificantChange(p.grossIncome,
              summary.paychecks[i - 1]?.grossIncome)}`}>{toCurrency(p.grossIncome)}</td>
            {p.deductions.map((d, ix) =>
              <td key={d.code}
                  className={`table__cell table__cell--right ${isSignificantChange(
                    d.amount,
                    summary.paychecks[i - 1]?.deductions[ix].amount)}`}>
                {toCurrency(d.amount)}
              </td>
            )}
            {displayDirectDepositColumn(summary) &&
              <td className={`table__cell table__cell--right ${isSignificantChange(p.directDepositAmount,
                summary.paychecks[i - 1]?.directDepositAmount)}`}>
                {toCurrency(p.directDepositAmount)}
              </td>}
            {displayCheckColumn(summary) &&
              <td className={`table__cell table__cell--right ${isSignificantChange(p.checkAmount,
                summary.paychecks[i - 1]?.checkAmount)}`}>
                {toCurrency(p.checkAmount)}
              </td>}
          </tr>
        )}
        <tr className="table__totals">
          <td colSpan="2" className="table__cell table__cell--left">Annual Totals</td>
          <td className="table__cell table__cell--right">{toCurrency(summary.grossIncomeTotal)}</td>
          {summary.deductions.map(
            d => <td key={d.code} className="table__cell table__cell--right">{toCurrency(
                summary.deductionTotals[d.code])
              || toCurrency(0)}</td>)}
          {displayDirectDepositColumn(summary) &&
            <td className="table__cell table__cell--right">{toCurrency(summary.directDepositTotal)}</td>}
          {displayCheckColumn(summary) &&
            <td className="table__cell table__cell--right">{toCurrency(summary.checkAmountTotal)}</td>}
        </tr>
        </tbody>
      </table>
    </div>
  )
}

const displayDirectDepositColumn = summary => {
  return summary.directDepositTotal > 0
}

const displayCheckColumn = summary => {
  return summary.checkAmountTotal > 0
}

const formatDeductionHeader = input => {
  if (input !== null) {
    return input.replace(/\w\S*/g, function (txt) {
      txt = txt.replace(":", "");
      return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    });
  }
}

// Style a cell bold if there was a significant change from the previous row.
const isSignificantChange = (curr, prev) => {
  if (typeof prev !== 'undefined') {
    if (Math.abs(curr - prev) > 0.03) {
      return 'font-semibold'
    }
  }
  return ''
}
