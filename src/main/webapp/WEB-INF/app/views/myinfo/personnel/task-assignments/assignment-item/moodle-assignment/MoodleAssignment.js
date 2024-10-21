import React from 'react'
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { isoToLongDate } from "app/utils/dateUtils";
import { Link } from "react-router-dom";


export default function MoodleAssignment({ assignment }) {
  return (
    <>
      <Hero>{assignment.task.title}</Hero>
      <Card className="mt-5 pb-5">
        {assignment.completed
         ? <Card.Header>
           Records indicate you completed the Introduction to Ethics training on or before
           {isoToLongDate(assignment.timestamp)}
         </Card.Header>
         : <Card.Header>
           As mandated by law, all new employees are required to complete an interactive Ethics Orientation.<br/>
           Please follow all instructions below to complete the course.
         </Card.Header>
        }

        <div className="m-5 mb-8">
          <Link to="/myinfo/personnel/tasks/assignments">
            Return to Personnel To-Do List
          </Link>
        </div>

        {!assignment.completed &&
          <div className="w-3/4 m-auto">
            <h2 className="text-2xl my-2">
              Training Instructions
            </h2>
            <ul className="list-disc mx-5">
              <li>The interactive course can be accessed using the link below.</li>
              <li>
                You will need to create a new user account for the course.
                <br/>
                <span className="font-semibold">
                You must use your Senate email address for account registration.
                </span>
              </li>
              <li>
                The online course includes questions which all need to be answered correctly to proceed through the
                material.
              </li>
            </ul>
            <div className="mt-2">
              <a href={assignment.task.url} target="_blank" rel="noopener noreferrer">
                Introduction to Ethics Training Course
              </a>
            </div>
          </div>
        }

      </Card>
    </>
  )
}