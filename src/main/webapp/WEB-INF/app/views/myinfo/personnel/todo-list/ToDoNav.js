import React from 'react';
import { Route, BrowserRouter, Routes } from 'react-router-dom';
import AcknowledgmentAssignment from "app/views/myinfo/personnel/todo-list/AcknowledgmentAssignment";
// import VideoTask from './VideoTask';
// import AcknowledgmentTask from './AcknowledgmentTask';
// import EthicsCourseTask from './EthicsCourseTask';

const ToDoNav = () => {
  return (
  <BrowserRouter>
    <Routes>
      <Route path="/myinfo/personnel/todo/acknowledgment/:taskId" element={<AcknowledgmentAssignment />} />
      {/*< />*/}
    </Routes>
  </BrowserRouter>
  );
};

export default ToDoNav;
