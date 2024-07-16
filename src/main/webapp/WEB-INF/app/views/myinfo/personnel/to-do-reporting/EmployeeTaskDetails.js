import React, { useState } from 'react';
import ManualOverrideModal from "app/views/myinfo/personnel/to-do-reporting/ManualOverrideModal";
import DeactivateTaskModal from "app/views/myinfo/personnel/to-do-reporting/DeactivateTaskModal";
import { loadAuth } from "app/contexts/Auth/authStorage";

export default function EmployeeTaskDetails({ person, taskMap }) {

  const [ manualOverrideModalOpen, setManualOverrideModalOpen ] = useState(false);
  const [ deactivateTaskModalOpen, setDeactivateTaskModalOpen ] = useState(false);
  const [ empName, setEmpName ] = useState('');
  const [ taskTitle, setTaskTitle ] = useState('');
  const [ taskId, setTaskId ] = useState('');
  const [ empId, setEmpId ] = useState('');

  const convertTimestampToLocalDateString = (timestamp) => {
    const date = new Date(timestamp);
    const options = { year: 'numeric', month: 'short', day: 'numeric' }
    return date.toLocaleString('en-US', options);
  };

  const openManualOverrideModal = (name, empId, title, taskId) => {
    setEmpName(name);
    setTaskTitle(title);
    setTaskId(taskId);
    setEmpId(empId);
    setManualOverrideModalOpen(true);
  };

  const openDeactivateTaskModal = (name, empId, title, taskId) => {
    setEmpName(name);
    setTaskTitle(title);
    setTaskId(taskId);
    setEmpId(empId);
    setDeactivateTaskModalOpen(true);
  };

  const closeManualOverrideModal = () => {
    setManualOverrideModalOpen(false);
  };

  const closeDeactivateTaskModal = () => {
    setDeactivateTaskModalOpen(false);
  };

  const handleManualOverrideSubmit = async (isCompletionOverride, empId, taskId) => {
    // Handle submit logic based on isCompletionOverride
    console.log('Submitting Manual Override:', isCompletionOverride, empId, taskId);
    // Close modal
    const updateEmpID = loadAuth().empId;
    // Close modal
    try {
      const url = `/api/v1/admin/personnel/task/overrride/${updateEmpID}/${taskId}/true/${empId}`;

      const init = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json",
        },
        cache: 'no-store',
      };

      const response = await fetch(url, init);
      if (!response.ok) {
        throw new Error('Failed to fetch data');
      } else {
        // Reload the page after API call is successful
        window.location.reload();
      }
      const data = await response;
      console.log(data);// Set received data after successful fetch
    } catch (error) {
      console.error('Error fetching task details:', error);
    } finally {
      console.log("Done")
    }
    setManualOverrideModalOpen(false);
  };

  const handleDeactivateTaskSubmit = async (isActiveStatusOverride, empId, taskId) => {
    // Handle submit logic based on isActiveStatusOverride
    console.log('Submitting Deactivate Task:', isActiveStatusOverride, empId, taskId);
    const updateEmpID = loadAuth().empId;
    // Close modal
    try {
      const url = `/api/v1/admin/personnel/task/overrride/activation/${updateEmpID}/${taskId}/false/${empId}`;

      const init = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json",
        },
        cache: 'no-store',
      };

      const response = await fetch(url, init);
      if (!response.ok) {
        throw new Error('Failed to fetch data');
      } else {
        // Reload the page after API call is successful
        window.location.reload();
      }
      const data = await response;
      console.log(data);// Set received data after successful fetch
    } catch (error) {
      console.error('Error fetching task details:', error);
    } finally {
      console.log("Done")
    }
    setDeactivateTaskModalOpen(false);
  };

  const handleDownloadPdf = async (empId, taskId) => {
    try {
      const url = `/api/v1/personnel/task/acknowledgment/download?taskId=${taskId}&empId=${empId}`;

      const init = {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json",
        },
        cache: 'no-store',
      };

      const response = await fetch(url, init);
      if (!response.ok) {
        throw new Error('Failed to fetch data');
      } else {
        // Reload the page after API call is successful
        window.location.reload();
      }
      const data = await response;
      console.log(data);// Set received data after successful fetch
    } catch (error) {
      console.error('Error fetching task details:', error);
    } finally {
      console.log("Done")
    }
  }

  return (
    <div>
      <span className={"font-semibold"}>Email:</span> {person.email}<br/>
      <span className={"font-semibold"}>Cont. Service From:</span> {person.contSrvDate}<br/>
      {person.inCompleteTasks.length > 0 && (
        <React.Fragment>
          <span className={"font-semibold"}>Incomplete Trainings:</span><br/>
          <ul className="list-disc ml-6 mb-4">
            {person.inCompleteTasks.map((task) => (
              <React.Fragment key={task.taskId}>
                <li className={'mb-1.5 mt-1.5'}>{taskMap.get(task.taskId).title}</li>
                <button className={'border border-black shadow rounded-sm bg-yellow-500 px-1 text-black-900'}
                        onClick={() => openManualOverrideModal(person.name, person.empId, taskMap.get(task.taskId).title, task.taskId)}>Manual
                  Override
                </button>
                <button className={'border border-black rounded-sm shadow bg-red-300 ml-2 px-1 mb-1 text-black-900'}
                        onClick={() => openDeactivateTaskModal(person.name, person.empId, taskMap.get(task.taskId).title, task.taskId)}>Deactivate
                  Task
                </button>
              </React.Fragment>
            ))}
          </ul>
        </React.Fragment>
      )}
      {person.completedCnt > 0 && (
        <React.Fragment>
          <span className={"font-semibold"}>Completed Trainings:</span><br/>
          <ul className="list-disc ml-6 mb-4">
            {person.completedTasks.map((task) => (
              <React.Fragment key={task.taskId}>
                <li className={'mb-1.5 mt-1.5'}>{taskMap.get(task.taskId).title}</li>
                <button className={'border border-black rounded-sm shadow bg-green-500 px-1 text-black-900'}
                        onClick={() => handleDownloadPdf(person.empId, task.taskId)}>Download Signed pdf
                </button>
                <span
                  className={"text-gray-400 ml-3"}>completed on {task.timestamp !== null ? convertTimestampToLocalDateString(task.timestamp) : task.contSrvDate}</span>
              </React.Fragment>
            ))}
          </ul>
        </React.Fragment>
      )}
      <ManualOverrideModal
        isOpen={manualOverrideModalOpen}
        closeModal={closeManualOverrideModal}
        empName={empName}
        empId={empId}
        taskTitle={taskTitle}
        taskId={taskId}
        onSubmit={handleManualOverrideSubmit}
      />

      {/* Deactivate Task Modal */}
      <DeactivateTaskModal
        isOpen={deactivateTaskModalOpen}
        closeModal={closeDeactivateTaskModal}
        empName={empName}
        empId={empId}
        taskTitle={taskTitle}
        taskId={taskId}
        onSubmit={handleDeactivateTaskSubmit}
      />
    </div>
  );
}