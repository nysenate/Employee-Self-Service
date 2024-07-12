import React, { useEffect, useState } from 'react';
import CancelIcon from '@mui/icons-material/Cancel';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import RemoveCircleIcon from '@mui/icons-material/RemoveCircle';

export default function Employees({finalData}) {
  const [isSelected, setIsSelected] = useState(false);
  const [personnel, setPersonnel] = useState([]);

  useEffect(() => {
    if (finalData && finalData.result) {
      const employees = finalData.result.map(result => result.employee);
      const tasks = finalData.result.map(result => result.tasks);
      buildEmployeeDetails(employees, tasks);
    }
  }, [finalData]);

  function buildEmployeeDetails(employees, tasks) {
    const newPersonnel = [];

    employees.forEach(employee => {
      const newEmployeeData = {
        name: employee.fullName,
        office: employee.respCtr.respCenterHead.name,
        empId: employee.employeeId,
        assigned: getAssignedDetails(tasks, employee.employeeId)[1],
        completed: getAssignedDetails(tasks, employee.employeeId)[0],
      };
      newPersonnel.push(newEmployeeData);
    });

    setPersonnel(newPersonnel);
    console.log(personnel);
  }

  function getAssignedDetails(allTasks, empId) {
    const employeeTasks = allTasks.find(tasks => tasks.some(task => task.empId === empId));
    if (employeeTasks) {
      const completedTasks = employeeTasks.filter(task => task.completed);
      return [completedTasks.length, employeeTasks.length];
    }
    return [0, 0];
  }

  return (
    <div className="mt-2 mr-10">
      <table className={"table table-fixed table-striped"}>
        <thead className="text-center">
        <tr className={"text-center text-sm font-semibold"}>
          <th className="w-1/6 text-left">Completed/ Assigned</th>
          <th className="w-2/6 text-left">Name</th>
          <th className="w-3/6 text-left">Office</th>
        </tr>
        </thead>
        <tbody>

        {personnel.map(person => (
          <tr className="cursor-pointer hover:bg-gray-80 mt-10" onClick={(e) => {
            setIsSelected(true)
            console.log(e)
          }} key={person.empId}>
            <td>
              <div
                className={`flex items-center space-x-2 rounded-full`}>
                {person.completed === person.assigned && (
                  <svg className="h-4 w-4 text-green-900 font-bold" width="12" height="12" viewBox="0 0 24 24" strokeWidth="2"
                       stroke="currentColor" fill="none" strokeLinecap="round" strokeLinejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z"/>
                    <path d="M5 12l5 5l10 -10"/>
                  </svg>
                )}
                {person.completed === 0 && (
                  <svg className="h-4 w-4 text-red-600 font-bold" width="12" height="12" viewBox="0 0 24 24" strokeWidth="2"
                       stroke="currentColor" fill="none" strokeLinecap="round" strokeLinejoin="round">
                  <path stroke="none" d="M0 0h24v24H0z"/>
                  <line x1="18" y1="6" x2="6" y2="18"/>
                  <line x1="6" y1="6" x2="18" y2="18"/>
                </svg>
                )}
                {person.completed > 0 && person.completed < person.assigned && (
                  <svg className="h-5 w-3 text-yellow-600" width="12" height="12" viewBox="0 0 24 24" strokeWidth="2"
                       stroke="currentColor" fill="none" strokeLinecap="round" strokeLinejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z"/>
                    <line x1="5" y1="12" x2="19" y2="12"/>
                  </svg>
                )}
                <span> {person.completed}/{person.assigned}</span>
              </div>
            </td>
            <td>{person.name}</td>
            <td>{person.office}</td>
          </tr>
        ))}
        </tbody>
      </table>
    </div>
  );
}