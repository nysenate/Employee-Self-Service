import React from 'react';
import Hero from "app/components/Hero";
import Card from "app/components/Card";

export default function TodoList() {
  return (
    <div>
      <Hero>Personnel To-Do List</Hero>
      <Card className="mt-5">
        <div className="py-3 px-10 mb-3 ml-11 text-left border-b-1 border-solid border-gray-400">
          Listed below are personnel tasks that require your attention. <br/>
          Click on a task link to take action on that task. <br/>
          <b>FAILURE TO RESPOND MAY RESULT IN THE HOLDING OF YOUR PAYCHECK.</b><br/>
          Contact the Personnel Office (518-455-3376) if you have any questions.
        </div>
      </Card>
    </div>
  );
}