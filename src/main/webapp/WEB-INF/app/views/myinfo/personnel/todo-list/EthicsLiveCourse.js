import React, { useState } from 'react';
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { Link, useLocation } from "react-router-dom";
import CodeForm from "app/views/myinfo/personnel/todo-list/CodeForm";
import LoadingIndicator from "app/components/LoadingIndicator";

export default function EthicsLiveCourse() {

  const location = useLocation();
  const [ error, setError ] = useState(false);
  const { task, completed, timestamp } = location.state;
  const [ isLoading, setLoading ] = useState(false);

  const handleError = (e) => {
    setError(e);
  }
  const handleLoading = (l) => {
    setLoading(true);
  }

  return (
    <>
      <Hero>{task.title}</Hero>
      <Card className="mt-5 py-3">
        {!completed ?
         (
           <>
             <div className="mx-[10em] text-left py-3">
               <p>Please follow the instructions below to complete your training.<br/>
                 Once complete, enter and submit the codes you received to confirm your completion.</p>
             </div>
             <hr/>
             <div className="mx-[10em] text-left">
               <Link to="/myinfo/personnel/todo"
                     className={"text-normal text-teal-600 py-3"}>
                 Return to Personnel To-Do List
               </Link>
               <br/>
             </div>
             <div className={"mx-[6.4em] my-2 text-left text-3xl"}>
               <p>Training Instructions</p>
             </div>
             <div className={"mx-[10em] my-2  text-base text-justify"}>
               <br/>
               <p>Existing employees must attend a LIVE in-person or online ethics training within the calendar
                 year.
                 New employees must attend a LIVE in-person or online ethics training within 90 days of their employment
                 (pursuant to Chapter 56 of the Laws of 2022). Trainings will be held in Albany for employees who can
                 attend in-person, and also streamed live online at the same time. (Albany-based employees are highly
                 encouraged to participate in the in-person training)
                 <br/><br/>
                 More details on dates and times for in-person training and live broadcasts can be found at:
               </p>
               <br/>
               <a className={"text-normal text-teal-600 py-3"}
                  href="https://my.nysenate.gov/department/personnel/training"
                  target="_blank"
                  rel="noopener noreferrer">LINK TO COURSE URL</a>
               <hr className={"mx-10 my-4"}/>
             </div>
             <div className={"mx-[6.4em] my-2 text-left text-3xl"}>
               <p>Code Submission</p>
             </div>
             <p className={"mx-[10em] my-2 text-base text-justify"}>
               Once you have completed the course, enter the codes from the presenters below to confirm your completion.
             </p>
             <div className={"mx-[10em] my-2 text-base text-justify"}>
               {error ? (
                        <div className="bg-red-600 text-white p-1 rounded mb-4 text-center">
                          <p className={"text-3xl"}>Incorrect Codes</p><br/>
                          <p className={"text-md"}>One or more of the submitted codes were incorrect. Please double check them
                            and resubmit.</p>
                        </div>
                      ) :
                <>
                  {isLoading &&
                    <LoadingIndicator/>
                  }
                </>}
             </div>
             <div className="mx-[20em] my-2 text-base text-justify">
               <CodeForm taskId={task.taskId} onError={handleError} onLoading={handleLoading}/>
             </div>
           </>
         ) : (
           <>
             <div className="mx-[10em] text-center py-3">
               <p>Records indicate you completed this Ethics training on or before {timestamp}.</p>
             </div>
             <hr/>
             <div className="mx-[10em] text-left">
               <Link to="/myinfo/personnel/todo"
                     className={"text-normal text-teal-600 py-3"}>
                 Return to Personnel To-Do List
               </Link>
             </div>
           </>
         )
        }
      </Card>
    </>
  );
}