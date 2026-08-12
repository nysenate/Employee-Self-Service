import React from "react";

const SummarySection = ({ children }) => {
  return <div className="mb-6">{children}</div>;
};

const Title = ({ children }) => {
  return (
    <h3 className="text-center text-lg font-medium text-teal-700">
      {children}
    </h3>
  );
};

const Table = ({ children, className = "" }) => {
  return (
    <table className={`mt-2 table w-full ${className}`}>
      <tbody className="table__body table__body--striped">{children}</tbody>
    </table>
  );
};

const Row = ({ children }) => {
  return <tr className="table__row">{children}</tr>;
};

const Cell = ({ children, className = "" }) => {
  return (
    <td className={`table__cell table__cell--left ${className}`}>{children}</td>
  );
};

SummarySection.Title = Title;
SummarySection.Table = Table;
SummarySection.Row = Row;
SummarySection.Cell = Cell;

export default SummarySection;
