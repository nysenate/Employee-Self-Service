import React, { useState } from 'react';
import Hero from "app/components/Hero";
import EmployeeSearchDirective from "app/views/time/personnel/EmployeeSearchDirective";
import EssNotification from "app/components/EssNotification";
import { RecordDetailsPopup } from "app/views/time/record/RecordDetailsPopup";
import { AccrualDetailsPopup } from "app/views/time/accrual/AccrualDetailsPopup";
import TogglePanel from "app/components/TogglePanel";
import RecordHistoryDirective from "app/views/time/record/RecordHistoryDirective";
import useAuth from "app/contexts/Auth/useAuth";
import AccrualBar from "app/views/time/accrual/AccrualBar";
import AllowanceBar from "app/views/time/allowance/AllowanceBar";
import AllowanceHistoryDirective from "app/views/time/allowance/AllowanceHistoryDirective";


export default function PersonnelSearchIndex() {
  const { userData } = useAuth();
  const [selectedEmp, setSelectedEmp] = useState(null);

  const viewRecordDetails = (input) => {console.log("viewRecordDetails input: ", input);}
  const viewAccrualDetails = (input) => {console.log("viewAccrualDetails input: ", input);}

  console.log("Selected at Personnel: ", selectedEmp);

  return (
    <div>
      <Hero>Employee Search</Hero>
      <EmployeeSearchDirective selectedEmp={selectedEmp} setSelectedEmp={setSelectedEmp}/>
      {/*Testing ToggelPanel => Aye! this works!*/}
      {/*{selectedEmp && (<TogglePanel open={false} label={"Attendance History"}>*/}
      {/*  <RecordHistoryDirective*/}
      {/*    viewDetails={viewRecordDetails}*/}
      {/*    user={userData().employee}*/}
      {/*    empSupInfo={selectedEmp}*/}
      {/*    linkToEntryPage={true}*/}
      {/*    scopeHideTitle={true}*/}
      {/*  />*/}
      {/*</TogglePanel>)}*/}
      {selectedEmp && (<TogglePanel open={false} label={"Allowance History"}>
        <AllowanceHistoryDirective
          user={userData().employee}
          empSupInfo={selectedEmp}
          scopeHideTitle={true}
        />
      </TogglePanel>)}
      {selectedEmp && selectedEmp?.payType === 'TE' && <AllowanceBar empId={selectedEmp.empId} />}
      {/*{selectedEmp && selectedEmp.active && (<AccrualBar empId={selectedEmp.empId}/>)}*/}
    </div>
  );
};






// Needed before Uncomment:
//    -AccrualProjections

// export default function PersonnelSearchIndex() {
//   const [selectedEmp, setSelectedEmp] = useState();
//   // Popups:
//   const [isRecordModalOpen, setIsRecordModalOpen] = useState(false);
//   const [isAccrualModalOpen, setIsAccrualModalOpen] = useState(false);
//   const closeModal = () => {setIsRecordModalOpen(false);setIsAccrualModalOpen(false);}
//
//   return (
//     <div>
//       <Hero>Employee Search</Hero>
//       <EmployeeSearchDirective
//         // selectedEmp={selectedEmp}
//         // setSelectedEmp={setSelectedEmp}
//       />
//       {selectedEmp && (
//         <div>
//           {!(selectedEmp.active) && (
//             <EssNotification level="info" title={`${selectedEmp.fullName} is not a current Senate employee.`}/>
//           )}
//           {selectedEmp.senator && (
//             <EssNotification level="info" title={`${selectedEmp.fullName} is a Senator`}>
//               <p>
//                 They cannot use or project accruals.
//                 <br />
//                 They will not have any attendance or accrual history unless they were a non-senator employee in the past.
//               </p>
//             </EssNotification>
//           )}
//           {selectedEmp.active && (
//             <div>
//               {showAccruals && <AccrualBar />}
//               {selectedEmp.payType === 'TE' && <AllowanceBar />}
//             </div>
//           )}
//
//           <TogglePanel open={false} label={"Attendance History"} >
//             <RecordHistoryDirective
//               viewDetails={viewRecordDetails}
//               user={userData().employee}
//               empSupInfo={selectedEmp}
//               linkToEntryPage={true}
//               scopeHideTitle={true}
//             />
//           </TogglePanel>
//           <TogglePanel open={false} label={"Accrual History"} > //ng-if="showAccrualHistory"
//             <AccrualHistoryDirective
//               viewDetails={viewAccrualDetails}
//               user={userData().employee}
//               empSupInfo={selectedEmp}
//               scopeHideTitle={true}
//             />
//           </TogglePanel>
//           <TogglePanel open={false} label={"Accrual Projections"} > //ng-if="selectedEmp.payType !== 'TE' && !selectedEmp.senator
//             <AccrualProjections
//               empSupInfo={selectedEmp}
//               scopeHideTitle={true}
//             />
//           </TogglePanel>
//           <TogglePanel open={false} label={"Allowance History"} > //ng-if=showAllowanceHistory
//             <AllowanceHistory
//               empSupInfo={selectedEmp}
//               scopeHideTitle={true}
//             />
//           </TogglePanel>
//
//         </div>
//       )}
//       {selectedRecord && (<RecordDetailsPopup
//         record={selectedRecord}
//         isModalOpen={isRecordModalOpen}
//         closeModal={closeModal}
//       />)}
//       {selectedAccrual && (
//         <AccrualDetailsPopup
//           accruals={selectedAccrual}
//           isModalOpen={isAccrualModalOpen}
//           closeModal={closeModal}
//         />
//       )}
//     </div>
//   );
// };
