import React, { useState } from "react";
import Modal from "app/components/Modal";
import { Button } from "app/components/Button";

const StatisticsSummary = ({ statistics }) => {
  const [open, setOpen] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);

  const handleRowClick = (row) => {
    console.log(row);
    setSelectedRow(row);
    setOpen(true);
  };

  return (
    <div className="mx-4">
      {/* Total Applications and Expenses */}
      {statistics?.result?.count > 0 ? (
        <div className="py-3">
          <div className="flex items-center justify-between">
            <h3 className="flex-1 font-bold p-2">Total Applications: {statistics.result.count}</h3>
            <h3 className="flex-3 font-bold p-2">Total Expenses: $ {statistics.result.totalExpenses}</h3>
          </div>
          {/* Main Table */}

          <table className="table">
            <thead className="table__head__row bg-orange-600">

            <tr className="text-white">
              <th>#</th>
              <th>Employee Name</th>
              <th>Count</th>
              <th>Total Expenses</th>
            </tr>
            </thead>
            <tbody className="table__body table__body--striped table__body--highlight">
            {statistics.result.appStatuses.map((app, index) => (
              <tr key={index} onClick={() => handleRowClick(app)} style={{ cursor: "pointer" }}>
                <td>{index + 1}</td>
                <td>{app.employeeView.fullName}</td>
                <td>{app.count}</td>
                <td>$ {app.totalExpenses}</td>
              </tr>
            ))}
            </tbody>
          </table>
        </div>) : (
         <p className="text-center text-gray-600">No data available.</p>
       )}


      {/* Modal for Status Summary List */}
      <Modal isOpen={open} onClose={() => setOpen(false)}>
        <Modal.Title>Status Summary for {selectedRow?.employeeView.fullName}</Modal.Title>
        <Modal.Body>
          <div className="text-center">
            <table className="table-auto">
              <thead className="table__head__row bg-orange-600">
              <tr className="text-white">
                <th className="px-6 py-1">#</th>
                <th className="px-6 py-1">Status</th>
                <th className="px-6 py-1">Count</th>
                <th className="px-6 py-1">Total Expenses</th>
              </tr>
              </thead>
              <tbody className="table__body table__body--striped table__body--highlight">
              {selectedRow?.statusSummaryList.map((status, idx) => (
                <tr key={idx}>
                  <td>{idx + 1}</td>
                  <td>{status.status}</td>
                  <td>{status.count}</td>
                  <td>${status.totalExpenses}</td>
                </tr>
              ))}
              </tbody>
            </table>
          </div>
        </Modal.Body>
        <Modal.Buttons>
          <Button color="success" onClick={() => setOpen(false)}>Close</Button>
        </Modal.Buttons>
      </Modal>
    </div>
  );
};

export default StatisticsSummary;
