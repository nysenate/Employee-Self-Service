import React from "react";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import AssignmentsList from "./AssignmentsList";

export default function TaskAssignmentsListIndex() {
  return (
    <div>
      <Hero>Personnel To-Do List</Hero>
      <Card className="mt-5">
        <Card.Header>
          <p>
            Listed below are personnel tasks that require your attention. <br />
            Click on a task link to take action on that task. <br />
            <b>
              FAILURE TO RESPOND MAY RESULT IN THE HOLDING OF YOUR PAYCHECK.
            </b>
            <br />
            Contact the Personnel Office (518-455-3376) if you have any
            questions.
          </p>
        </Card.Header>
        <AssignmentsList />
      </Card>
    </div>
  );
}
