import React, { useEffect, useRef, useState } from 'react';
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { loadAuth } from "app/contexts/Auth/authStorage";
import LoadingIndicator from "app/components/LoadingIndicator";
import { isoToLongDate } from "app/utils/dateUtils";
import VideoCodeEntryForm
  from "app/views/myinfo/personnel/task-assignments/assignment-item/video-assignment/VideoCodeEntryForm";
import ModalNotice from "app/components/ModalNotice";

export default function VideoAssignment({ assignment }) {
  const [ isSuccessModalOpen, setIsSuccessModalOpen ] = useState(false);
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
      <Hero>{assignment.task.title}</Hero>
      <Card className="mt-3">
        {assignment.completed
         ? <Card.Header>
           <p>
             Records indicate you have already watched this video on or
             before {isoToLongDate(assignment.timestamp)}.<br/>
             If you would like to review the video, you may still view it below.
           </p>
         </Card.Header>
         : <Card.Header>
           {/*// TODO is this text right?*/}
           <p>Please take some time to watch the video below.<br/>
             Codes will appear on-screen at various points during the video.<br/>
             Record these codes as they appear.<br/>
             <span className={"font-semibold"}>You can scroll backwards in case you miss a code. You CANNOT scroll forwards</span><br/>
             When the video is finished, use the form below the video to enter the codes to confirm your viewing.
           </p>
         </Card.Header>
        }

        <div className="mx-2 text-left">
          <Link to="/myinfo/personnel/tasks/assignments">
            Return to Personnel To-Do List
          </Link>
        </div>
        <div className="p-5 w-full">
          <RenderVideo src={assignment.task.path} allowSeeking={assignment.completed}/>
        </div>

        {!assignment.completed &&
          <>
            <div className="border-t-1 border-solid border-teal-400">
              <Card.Header>
                <div className="text-teal-700 font-semibold">
                  In the form below, please enter the codes from the video and then click "Submit".
                </div>
              </Card.Header>
            </div>
            <VideoCodeEntryForm taskId={assignment.taskId} onSuccess={onCodeEntrySuccess}/>
          </>
        }
      </Card>

      <ModalNotice isOpen={isSuccessModalOpen}
                   onResolve={onSuccessModalResolved}
                   title="Code Submission Complete"
                   body="Video codes were successfully submitted"/>
    </>
  )
}

function RenderVideo({ src, allowSeeking }) {
  const videoRef = useRef(null);
  const [ supposedCurrentTime, setSupposedCurrentTime ] = useState(0);
  const [ videoLoaded, setVideoLoaded ] = useState(false);

  useEffect(() => {
    const video = videoRef.current;
    if (video) {
      const handleLoadedData = () => {
        setVideoLoaded(true);
      };
      video.addEventListener('loadeddata', handleLoadedData);
      return () => {
        video.removeEventListener('loadeddata', handleLoadedData);
      };
    }
  }, []);

  useEffect(() => {
    if (!videoLoaded) return;
    const video = videoRef.current;

    const handleTimeUpdate = () => {
      if (!video.seeking) {
        setSupposedCurrentTime(video.currentTime);
      }
    };

    const handleSeeking = () => {
      if (video.currentTime - supposedCurrentTime > 0.01) {
        video.currentTime = supposedCurrentTime;
      }
    };

    if (!allowSeeking) {
      video.addEventListener('timeupdate', handleTimeUpdate);
      video.addEventListener('seeking', handleSeeking);

      return () => {
        video.removeEventListener('timeupdate', handleTimeUpdate);
        video.removeEventListener('seeking', handleSeeking);
      };
    }
  }, [ videoLoaded, supposedCurrentTime ]);

  return (
    <video src={src} ref={videoRef} controlsList={"nodownload"} controls>
    </video>
  )
}