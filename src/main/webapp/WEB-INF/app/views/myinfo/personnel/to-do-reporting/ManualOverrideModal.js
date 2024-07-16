import React from 'react';
import Modal from '@mui/material/Modal';
import Box from "@mui/material/Box";

const ManualOverrideModal = ({ isOpen, closeModal, empName, taskTitle, empId, taskId, onSubmit }) => {
  const style = {
    position: 'absolute',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: 550,
    height: 170,
    bgcolor: 'background.paper',
    boxShadow: 24,
  };
  return (
    <Modal
      aria-labelledby="transition-modal-title"
      aria-describedby="transition-modal-description"
      open={isOpen}
      onClose={closeModal}
      closeAfterTransition
    >
      <Box sx={style}>
        <div className="border-b border-gray-300 py-1 mb-2">
          <p className="font-openSans text-center text-xl font-semibold">
            Personnel Task Override
          </p>
        </div>
        <div className="py-1 mb-6">
          <p className="font-openSans text-center text-md">
            <u>Warning:</u> You are attempting to submit a task <b>COMPLETION STATUS</b> override for
            <br/>
            <u>Employee:</u> <i>{empName}</i>
            <br/>
            <u>Task:</u> <i>{taskTitle}</i>
          </p>
        </div>
        <div className="border-t flex justify-center items-center border-gray-300 py-1 mb-1">
          <button onClick={() => onSubmit(true, empId, taskId)} className={'border shadow-lg w-16 h-6 bg-green-600 px-1 text-white'} type="button">
            Proceed
          </button>
          <button onClick={closeModal} className={'border shadow-lg bg-red-600 w-16 h-6 ml-2 px-1 text-white'} type="button">
            Cancel
          </button>
        </div>

      </Box>
    </Modal>
  );
};

export default ManualOverrideModal;
