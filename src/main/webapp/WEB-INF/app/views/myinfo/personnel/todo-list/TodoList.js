import React from 'react';
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import Assignments from "./Assignments";

export default function TodoList() {
  return (
    <div>
      <Hero>Personnel To-Do List</Hero>
      <Card className="mt-5">
        <div className="py-4 mx-[10em] text-left">
          Listed below are personnel tasks that require your attention. <br/>
          Click on a task link to take action on that task. <br/>
          <b>FAILURE TO RESPOND MAY RESULT IN THE HOLDING OF YOUR PAYCHECK.</b><br/>
          Contact the Personnel Office (518-455-3376) if you have any questions.
        </div>
        <hr/>
        <Assignments/>
      </Card>
    </div>
  );
}