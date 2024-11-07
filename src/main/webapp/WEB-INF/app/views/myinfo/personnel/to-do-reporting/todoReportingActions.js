// --- Models used by the ToDoReporting filterReducer ---

// Action Types
export const TOGGLE_INACTIVE_TRAININGS = "TOGGLE_INACTIVE_TRAININGS"
export const TOGGLE_TRAINING = "TOGGLE_TRAINING"
export const CLEAR_TRAININGS = "CLEAR_TRAININGS"
export const COMPLETION_STATUS = "COMPLETION_STATUS"
export const PAGE = "PAGE"

// Actions
export const toggleInactiveTrainings = (checked, inactiveTrainingIds) => ({
  type: TOGGLE_INACTIVE_TRAININGS,
  payload: { checked, inactiveTrainingIds }
})

export const toggleTraining = (taskId, checked) => ({
  type: TOGGLE_TRAINING,
  payload: { taskId, checked }
})

export const clearTrainings = () => ({
  type: CLEAR_TRAININGS
})

export const updateCompletionStatus = (completionStatus) => ({
  type: COMPLETION_STATUS,
  payload: { completionStatus }
})
