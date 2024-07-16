import React, { useEffect, useState } from 'react';
import EmployeeTaskDetails from "app/views/myinfo/personnel/to-do-reporting/EmployeeTaskDetails";

export default function Employees({ finalData, allTasks, params, onChildDataChange }) {
  const [ selectedEmployee, setSelectedEmployee ] = useState(false);
  const [ personnel, setPersonnel ] = useState([]);

  const taskMap = new Map(allTasks.map(task => [ task.taskId, task ]));

  const orderByMap = {
    name: [ 'NAME', 'OFFICE' ],
    office: [ 'OFFICE', 'NAME' ],
    completed: [ 'COMPLETED', 'NAME' ]
  };

  useEffect(() => {
    if (finalData && finalData.result) {
      const employees = finalData.result.map(result => result.employee);
      const tasks = finalData.result.map(result => result.tasks);
      buildEmployeeDetails(employees, tasks);
    }
  }, [ finalData ]);

  function buildEmployeeDetails(employees, tasks) {
    const newPersonnel = [];
    employees.forEach(employee => {
      const contServiceDate = employee.contServiceDate;
      const contSrvDate = new Date(contServiceDate);
      const options = { year: 'numeric', month: 'short', day: 'numeric' }
      contSrvDate.setDate(contSrvDate.getDate() + 1);
      const newEmployeeData = {
        name: employee.fullName ?? '',
        office: employee.respCtr?.respCenterHead?.name ?? '',
        empId: employee.employeeId ?? -1,
        email: employee.email ?? '',
        contSrvDate: contSrvDate?.toLocaleDateString('en-US', options) ?? '',
        assignedCnt: (getAssignedDetails(tasks, employee.employeeId)[0] ?? []).length,
        completedCnt: (getAssignedDetails(tasks, employee.employeeId)[1] ?? []).length,
        completedTasks: getAssignedDetails(tasks, employee.employeeId)[1] ?? [],
        inCompleteTasks: getAssignedDetails(tasks, employee.employeeId)[2] ?? [],
      };
      newPersonnel.push(newEmployeeData);
    });
    setPersonnel(newPersonnel);
    console.log(newPersonnel);
  }

  function getAssignedDetails(allTasks, empId) {
    const employeeTasks = allTasks.find(tasks => tasks.some(task => task.empId === empId));
    if (employeeTasks) {
      const completedTasks = employeeTasks.filter(task => task.completed);
      const inCompleteTasks = employeeTasks.filter(task => !task.completed);
      return [ employeeTasks, completedTasks, inCompleteTasks ];
    }
    return [];
  }

  const toggleDetails = (empId) => {
    setSelectedEmployee(selectedEmployee === empId ? null : empId);
  };

  const handleSort = (e) => {
    e.preventDefault();
    let value = e.target.dataset.columnKey;
    let { keysArray, sortOrder } = params.sort.reduce((acc, item) => {
      const [key, order] = item.split(':');
      acc.keysArray.push(key);
      if (!acc.sortOrder) {
        acc.sortOrder = order;
      }
      return acc;
    }, { keysArray: [], sortOrder: null });
    const key = getKeyFromValue(orderByMap, keysArray);
    if (key === value){
      sortOrder = sortOrder === 'ASC' ? 'DESC' : 'ASC';
    }
    const orderBys = orderByMap[value];
    const sortOrders = [];
    for (let i in orderBys) {
      sortOrders.push(orderBys[i]+':'+sortOrder);
    }
    params.sort = sortOrders;
    onChildDataChange(params);
  }

  const getKeyFromValue = (object, value) => {
    return Object.keys(object).find(key => object[key].toString() === value.toString());
  }

  return (
    <div className="mt-2 mr-10">
      <table className={"table table-fixed table-striped"}>
        <thead className="text-center border-b-1 border-gray-300">
        <tr className={"text-center text-sm font-semibold"}>
          <th className="w-1/6 text-center cursor-pointer h-6 hover:bg-gray-50" data-column-key="completed"
              onClick={(e) => handleSort(e)}>Completed/ Assigned
          </th>
          <th className="w-2/6 text-center cursor-pointer h-6 hover:bg-gray-50" data-column-key="name"
              onClick={(e) => handleSort(e)}>Name
          </th>
          <th className="w-3/6 text-center cursor-pointer h-6 hover:bg-gray-50" data-column-key="office"
              onClick={(e) => handleSort(e)}>Office
          </th>
        </tr>
        </thead>
        <tbody className="border-t-1 border-gray-300">
        {personnel.map(person => (
          <React.Fragment key={person.empId}>
            <tr
              className={`cursor-pointer h-7 hover:bg-gray-100 ${selectedEmployee === person.empId ? 'border-1 border-black-500' : ''} `}
              onClick={() => toggleDetails(person.empId)}
              key={person.empId}>
              <td className={"text-center"}>
                <div
                  className={`flex items-center space-x-2 rounded-full`}>
                  {person.completedCnt === person.assignedCnt && (
                    <svg className="h-4 w-4 text-green-900 font-bold" width="12" height="12" viewBox="0 0 24 24"
                         strokeWidth="2"
                         stroke="currentColor" fill="none" strokeLinecap="round" strokeLinejoin="round">
                      <path stroke="none" d="M0 0h24v24H0z"/>
                      <path d="M5 12l5 5l10 -10"/>
                    </svg>
                  )}
                  {person.completedCnt === 0 && (
                    <svg className="h-4 w-4 text-red-600 font-bold" width="12" height="12" viewBox="0 0 24 24"
                         strokeWidth="2"
                         stroke="currentColor" fill="none" strokeLinecap="round" strokeLinejoin="round">
                      <path stroke="none" d="M0 0h24v24H0z"/>
                      <line x1="18" y1="6" x2="6" y2="18"/>
                      <line x1="6" y1="6" x2="18" y2="18"/>
                    </svg>
                  )}
                  {person.completedCnt > 0 && person.completedCnt < person.assignedCnt && (
                    <svg className="h-5 w-4 text-yellow-600" width="12" height="12" viewBox="0 0 24 24" strokeWidth="2"
                         stroke="currentColor" fill="none" strokeLinecap="round" strokeLinejoin="round">
                      <path stroke="none" d="M0 0h24v24H0z"/>
                      <line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                  )}
                  <span> {person.completedCnt}/{person.assignedCnt}</span>
                </div>
              </td>
              <td>{person.name}</td>
              <td>{person.office}</td>
            </tr>
            {selectedEmployee === person.empId && (
              <tr className={'border-1 border-black-500'}>
                <td colSpan="3">
                  <EmployeeTaskDetails person={person} taskMap={taskMap}/>
                </td>
              </tr>
            )}
          </React.Fragment>
        ))}
        </tbody>
      </table>
    </div>
  );
}