import { fetchApiJson } from "app/api/fetchJson";


export function getTaskAssignment(empId, taskId) {
  return fetchApiJson(`/personnel/task/assignment/${empId}/${taskId}`)
}

export function getTaskAssignments(empId, detail = true, activeOnly = true) {
  return fetchApiJson(`/personnel/task/assignment/${empId}?detail=${detail}&activeOnly=${activeOnly}`)
}

export function searchTaskAssignments(queryParams) {
  return fetchApiJson(`/personnel/task/emp/search?${queryParams}`)
}

export function searchPotentialTaskAssignments(queryParams) {
  return fetchApiJson(`/personnel/task/emp/assignSearch?${queryParams}`)
}

export function acknowledgeDocument({ empId, taskId }) {
  return fetchApiJson(`/personnel/task/acknowledgment?empId=${empId}&taskId=${taskId}`, { method: "POST" })
}

export function submitVideoCodes(data) {
  return fetchApiJson(`/personnel/task/video/code`, { method: "POST", payload: data })
}

export function submitEthicsLiveForm(data) {
  return fetchApiJson(`/personnel/task/ethics/live/code`, { method: "POST", payload: data })
}

export function manuallyOverrideCompletionStatus(data) {
  return fetchApiJson(`/admin/personnel/task/overrride/${data.updatedByEmpId}/${data.taskId}/${data.isCompleted}/${data.assignedEmpId}`)
}

export function manuallyDeactivateTaskAssignment(data) {
  return fetchApiJson(`/admin/personnel/task/overrride/activation/${data.updatedByEmpId}/${data.taskId}/${data.isActive}/${data.assignedEmpId}`)
}

export function manuallyAssignTask(data) {
  return fetchApiJson(`/admin/personnel/task/overrride/${data.updatedByEmpId}/${data.taskId}/${data.assignedEmpId}`)
}