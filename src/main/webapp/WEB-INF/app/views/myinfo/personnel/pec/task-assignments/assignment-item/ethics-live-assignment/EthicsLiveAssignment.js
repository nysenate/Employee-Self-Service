import React, { useState } from 'react'
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { isoToLongDate } from "app/utils/dateUtils";
import { Link, useNavigate } from "react-router-dom";
import EthicsLiveCodeEntryForm
  from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/ethics-live-assignment/EthicsLiveCodeEntryForm";
import ModalNotice from "app/components/ModalNotice";


export default function EthicsLiveAssignment({ assignment }) {
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);
  const navigate = useNavigate()

  const onCodeEntrySuccess = () => {
    setIsSuccessModalOpen(true)
  }

  const onSuccessModalResolved = () => {
    setIsSuccessModalOpen(false)
    navigate('/myinfo/personnel/tasks/assignments')
  }

  return (
    <>
      <Hero>Mandatory LIVE in-person and Online Ethics Training Instructions</Hero>
      <Card className="mt-t pb-5">
        {assignment.completed
         ? <Card.Header>
           Records indicate you completed this Ethics live training on or before {isoToLongDate(assignment.timestamp)}
         </Card.Header>
         : <Card.Header>
           Please follow the instructions below to complete your training.
           <br/>
           Once complete, enter and submit the codes you received to confirm your completion.
         </Card.Header>
        }

        <div className="m-5 mb-8">
          <Link to="/myinfo/personnel/tasks/assignments">
            Return to Personnel To-Do List
          </Link>
        </div>

        {!assignment.completed &&
          <div>
            <div className="w-3/4 m-auto">
              <h2 className="text-2xl my-2">
                Training Instructions
              </h2>
              <p>
                Existing employees must attend a LIVE in-person or online ethics training within the calendar year.
                New employees must attend a LIVE in-person or online ethics training within 90 days of their employment
                (pursuant to Chapter 56 of the Laws of 2022).
                Trainings will be held in Albany for employees who can attend in-person, and also streamed live online
                at
                the same time.
                (Albany-based employees are highly encouraged to participate in the in-person training)
              </p>
              <p className="mt-2">
                More details on dates and times for in-person training and live broadcasts can be found at:
              </p>
              <div className="mt-2">
                <a href={assignment.task.url} target="_blank" rel="noopener noreferrer">
                  LINK TO COURSE URL
                </a>
              </div>

              <div className="w-3/4 m-auto my-5">
                <hr/>
              </div>

              <h2 className="text-2xl my-2">Code Submission</h2>
              <p>
                Once you have completed the course, enter the codes from the presenters below to confirm your
                completion.
              </p>
            </div>

            <div>
              <EthicsLiveCodeEntryForm taskId={assignment.taskId} onSuccess={onCodeEntrySuccess}/>
            </div>

          </div>
        }
      </Card>

      <ModalNotice isOpen={isSuccessModalOpen}
                   onResolve={onSuccessModalResolved}
                   title="Code Submission Complete"
                   body="Course codes were successfully submitted"/>
    </>
  )
}