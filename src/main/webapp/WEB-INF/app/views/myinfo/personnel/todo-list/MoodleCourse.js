import React from 'react';
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { Link, useLocation } from "react-router-dom";

export default function MoodleCourse() {

  const location = useLocation();
  const { task, completed, timestamp } = location.state;

  return (
    <>
      <Hero>{task.title}</Hero>
      <Card className="mt-5 py-3">
        {!completed ?
         (
           <>
             <div className="mx-[10em] text-left py-3">
               <p>As mandated by law, all new employees are required to complete an interactive Ethics Orientation.<br/>
                 Please follow all instructions below to complete the course.</p>
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
             <div className={"mx-[10em] my-2  text-md text-justify"}>
               <ul className={"list-disc ml-10"}>
                 <li>The interactive course can be accessed using the link below.</li>
                 <li>You will need to create a new user account for the course.</li>
                 <strong>You must use your Senate email address for account registration.</strong>
                 <li>The online course includes questions which all need to be answered correctly to proceed through the
                   material.
                 </li>
               </ul>
               <br/>
               <a className={"text-normal text-teal-600 py-3"}
                  href="http://training.legethics.com/login/index.php"
                  target="_blank"
                  rel="noopener noreferrer">Introduction to Ethics Training Course</a>
             </div>
           </>
         ) : (
           <>
             <div className="mx-[10em] text-left py-3 text-gray-800">
               <p>Records indicate you completed the <span className={"font-semibold underline"}>{task.title} </span> on
                 or before {timestamp}.</p>
             </div>
             <hr/>
             <div className="mx-2 text-left">
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