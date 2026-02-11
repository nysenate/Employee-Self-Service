import React from "react";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { isoToLongDate } from "app/utils/dateUtils";
import { Link } from "react-router-dom";

export default function EthicsCourseAssignment({ assignment }) {
  return (
    <>
      <Hero>{assignment.task.title}</Hero>
      <Card className="mt-5 pb-5">
        {assignment.completed ? (
          <Card.Header>
            Records indicate you completed this Ethics training on or before{" "}
            {isoToLongDate(assignment.timestamp)}
          </Card.Header>
        ) : (
          <Card.Header>
            As mandated by law, all current employees are required to complete
            this ethics course.
            <br />
            Please follow all instructions below to complete the course.
          </Card.Header>
        )}

        <div className="m-5 mb-8">
          <Link to="/myinfo/personnel/tasks/assignments">
            Return to Personnel To-Do List
          </Link>
        </div>

        {!assignment.completed && (
          <div className="m-auto w-3/4">
            <h2 className="my-2 text-2xl">
              Ethics Course Training Instructions
            </h2>
            <ul className="mx-5 list-disc">
              <li>
                The interactive course can be accessed using the link below.
              </li>
              <li>
                <span className="font-semibold">
                  You must use your Senate email address.
                </span>
              </li>
            </ul>
            <div className="mt-2">
              <a
                href={assignment.task.url}
                target="_blank"
                rel="noopener noreferrer"
              >
                Ethics Training Course
              </a>
            </div>
          </div>
        )}
      </Card>
    </>
  );
}
