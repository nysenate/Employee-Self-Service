// --- Models used by the ToDoReporting filterReducer ---

// Action Types
export const TOGGLE_INACTIVE_TRAININGS = "TOGGLE_INACTIVE_TRAININGS";
export const TOGGLE_TRAINING = "TOGGLE_TRAINING";
export const CLEAR_TRAININGS = "CLEAR_TRAININGS";
export const COMPLETION_STATUS = "COMPLETION_STATUS";
export const TOGGLE_INACTIVE_EMPLOYEES = "TOGGLE_INACTIVE_EMPLOYEES";
export const UPDATE_CONT_SERV_DATE = "UPDATE_CONT_SERV_DATE";
export const SET_RESP_CTR_HEADS = "SET_RESP_CTR_HEADS";
export const SET_EMP_NAME = "SET_EMP_NAME"

export const PAGE = "PAGE";

// Actions
export const toggleInactiveTrainings = (checked, inactiveTrainingIds) => ({
  type: TOGGLE_INACTIVE_TRAININGS,
  payload: { checked, inactiveTrainingIds },
});

export const toggleTraining = (taskId, checked) => ({
  type: TOGGLE_TRAINING,
  payload: { taskId, checked },
});

export const clearTrainings = () => ({
  type: CLEAR_TRAININGS,
});

export const updateCompletionStatus = (completionStatus) => ({
  type: COMPLETION_STATUS,
  payload: { completionStatus },
});

export const toggleInactiveEmployees = (checked) => ({
  type: TOGGLE_INACTIVE_EMPLOYEES,
  payload: { checked },
});

export const updateContServDate = (date) => ({
  type: UPDATE_CONT_SERV_DATE,
  payload: { date }
});

export const setRespCtrHeads = (respCtrHead) => ({
  type: SET_RESP_CTR_HEADS,
  payload: { respCtrHead },
});

export const setEmpName = (name) => ({
  type: SET_EMP_NAME,
  payload: { name }
});
