import React from "react"

const SummarySection = ({ children }) => {
  return (
    <div className="mb-6">
      {children}
    </div>
  )
}

const Title = ({ children }) => {
  return (
    <h3 className="text-lg font-medium text-teal-700 text-center">
      {children}
    </h3>
  )
}

const Table = ({ children }) => {
  return (
    <table className="table w-full mt-2">
      <tbody className="table__body table__body--striped">
      {children}
      </tbody>
    </table>
  )
}

const Row = ({ children }) => {
  return (
    <tr className="table__row">
      {children}
    </tr>
  )
}

const Cell = ({ children }) => {
  return (
    <td className="table__cell table__cell--left">
      {children}
    </td>
  )
}

SummarySection.Title = Title
SummarySection.Table = Table
SummarySection.Row = Row
SummarySection.Cell = Cell

export default SummarySection