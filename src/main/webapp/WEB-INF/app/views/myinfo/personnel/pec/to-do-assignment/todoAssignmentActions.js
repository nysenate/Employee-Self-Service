// Action Types
export const CLEAR_TRAININGS = "CLEAR_TRAININGS";
export const TOGGLE_TRAINING = "TOGGLE_TRAINING";
export const TOGGLE_EXCLUDE_MEMBERS = "TOGGLE_EXCLUDE_MEMBERS"
export const SET_RESP_CTR_HEADS = "SET_RESP_CTR_HEADS";
export const SET_EMP_NAME = "SET_EMP_NAME";
export const SET_OFFSET = "SET_OFFSET";

// Actions
export const clearTrainings = () => ({
  type: CLEAR_TRAININGS
});

export const toggleTraining = (taskId, checked) => ({
  type: TOGGLE_TRAINING,
  payload: { taskId, checked }
})

export const toggleExcludeMembers = (checked) => ({
  type: TOGGLE_EXCLUDE_MEMBERS,
  payload: { checked },
})

export const setRespCtrHeads = (respCtrHead) => ({
  type: SET_RESP_CTR_HEADS,
  payload: { respCtrHead },
})

export const setEmpName = (name) => ({
  type: SET_EMP_NAME,
  payload: { name }
})

export const setOffset = (offset) => ({
  type: SET_OFFSET,
  payload: { offset }
})
