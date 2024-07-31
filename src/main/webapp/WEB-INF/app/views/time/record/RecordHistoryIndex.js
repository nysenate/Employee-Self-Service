import RecordHistoryDirective from "app/views/time/record/RecordHistoryDirective";
import React, { useState } from "react";
import Hero from "app/components/Hero";
import useAuth from "app/contexts/Auth/useAuth";
import { RecordDetailsPopup } from "app/views/time/record/RecordDetailsPopup";


const RecordHistoryIndex = () => {
  // Connected Components' State Variables + setter/renderer functions
  const { userData } = useAuth();
  const [ isModalOpen, setIsModalOpen ] = useState(false);
  const [ selectedRecord, setSelectedRecord ] = useState(null);
  const viewDetails = (selectedRecord) => {
    console.log(selectedRecord);
    setSelectedRecord(selectedRecord);
    setIsModalOpen(true);
  }
  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedRecord(null);
  }

  return (
    <div>
      <Hero>Attendance History</Hero>
      {userData().employee && (<RecordHistoryDirective
        viewDetails={viewDetails}
        user={userData().employee}
        empSupInfo={userData().employee}
        linkToEntryPage={true}
      />)}
      {selectedRecord && (<RecordDetailsPopup
        record={selectedRecord}
        isModalOpen={isModalOpen}
        closeModal={closeModal}
      />)}
    </div>
  );
};

export default RecordHistoryIndex;