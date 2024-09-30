import React, { useEffect, useRef, useState } from 'react';
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { loadAuth } from "app/contexts/Auth/authStorage";
import LoadingIndicator from "app/components/LoadingIndicator";

export default function VideoCourse() {
  const location = useLocation();
  const [ firstCode, setFirstCode ] = useState('');
  const [ secondCode, setSecondCode ] = useState('');
  const videoRef = useRef(null);
  const [ isLoading, setLoading ] = useState(null);
  const [ supposedCurrentTime, setSupposedCurrentTime ] = useState(0);
  const [ videoLoaded, setVideoLoaded ] = useState(false);
  const { task, completed, timestamp } = location.state;
  const { empId } = loadAuth();

  const [ formData, setFormData ] = useState({
    codes: [],
    empId: empId,
    taskId: task.taskId,
  });

  const [ error, setError ] = useState(null); // State to manage error state
  const isFormValid = firstCode && secondCode;
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!firstCode || !secondCode) {
      setError('Please fill in all fields.');
      return;
    }

    // Prepare form data
    const updatedFormData = {
      ...formData,
      codes: [ firstCode, secondCode ]
    };

    setFormData(updatedFormData);

    try {
      const url = `/api/v1/personnel/task/video/code`;
      const init = {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Accept": "application/json",
        },
        body: JSON.stringify(updatedFormData),
      };

      const response = await fetch(url, init);

      if (!response.ok) {
        setError(true)
        throw new Error('Network response was not ok');
      }
      setLoading(true);
      setError(false);
      console.log('Form submitted successfully');
      setTimeout(function () {
        setLoading(true)
        navigate('/myinfo/personnel/todo');
      }, 2000);
    } catch (error) {
      console.error('Error submitting the form:', error);
      setError('Failed to submit the form. Please try again later.');
    } finally {
      console.log("OKOK")
      setLoading(false)
    }
  };

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

    video.addEventListener('timeupdate', handleTimeUpdate);
    video.addEventListener('seeking', handleSeeking);

    return () => {
      video.removeEventListener('timeupdate', handleTimeUpdate);
      video.removeEventListener('seeking', handleSeeking);
    };
  }, [ videoLoaded, supposedCurrentTime ]);

  return (
    <>
      <Hero>{task.title}</Hero>
      <Card className="mt-5 py-3">
        {!completed ?
         (
           <>
             <div className="mx-[10em] text-left py-3 text-gray-800">
               <p>Please take some time to watch the video below.<br/>
                 Codes will appear on-screen at various points during the video.<br/>
                 Record these codes as they appear.<br/>
                 <span className={"font-semibold"}>You can scroll backwards in case you miss a code. You CANNOT scroll forwards</span><br/>
                 When the video is finished, use the form below the video to enter the codes to confirm your viewing.
               </p>
             </div>
             <hr/>
             <div className="mx-[10em] text-left">
               <Link to="/myinfo/personnel/todo"
                     className={"text-normal text-teal-600 py-3"}>
                 Return to Personnel To-Do List
               </Link>
               <br/>
             </div>
             <div className="p-5 w-[100%]">
               <video src={task.path} ref={videoRef} id="video" controlsList={"nodownload"} controls muted>
                 No Video
               </video>
             </div>
             <hr/>
             <span className={"mx-[10em] text-center py-3"}>
               In the form below, please enter the codes from the video and then click "Submit".
             </span>
             <hr/>
             <>
               <div className={"mx-[10em] my-2 text-base text-justify"}>
                 {error ? (
                   <div className="bg-red-600 text-white p-1 rounded mb-4 text-center">
                     <p className={"text-3xl"}>Incorrect Codes</p><br/>
                     <p className={"text-md"}>One or more of the submitted codes were incorrect. Please double check
                       them
                       and resubmit.</p>
                   </div>
                 ):
                 <>
                   {console.log(isLoading)}
                   {isLoading &&
                   <LoadingIndicator/>
                   }
                 </>}
               </div>
               <div className="flex items-center justify-center">
                 <form onSubmit={handleSubmit} className="bg-white p-1 rounded w-80">
                   <div className="mb-4">
                     <label htmlFor="firstCode" className="block text-left text-md mb-2">First Code:
                       <input
                         type="text"
                         id="firstCode"
                         value={firstCode}
                         onChange={(e) => setFirstCode(e.target.value)}
                         className="border border-gray-300 p-1 rounded ml-11"
                         required
                       />
                     </label>
                   </div>
                   <div className="mb-4">
                     <label htmlFor="secondCode" className="block text-left text-md mb-2">Second Code:
                       <input
                         type="text"
                         id="secondCode"
                         value={secondCode}
                         onChange={(e) => setSecondCode(e.target.value)}
                         className="border border-gray-300 p-1 rounded ml-7"
                         required
                       />
                     </label>
                   </div>
                   <button
                     type="submit"
                     disabled={!isFormValid}
                     className={`w-[6em] py-2 rounded text-white font-semibold ${
                       isFormValid ? 'bg-green-500 hover:bg-green-600' : 'bg-green-200 cursor-not-allowed'}`}>
                     Submit
                   </button>
                   <span className="ml-2 text-black-75">You must enter all codes to submit.</span>
                 </form>
               </div>
             </>
           </>
         ) : (
           <>
             <div className="mx-[10em] text-left py-3 text-gray-800">
               <p>Records indicate you watched the <span className={"font-semibold underline"}>{task.title} </span> on
                 or before {timestamp}.</p>
             </div>
             <hr/>
             <div className="mx-2 text-left">
               <Link to="/myinfo/personnel/todo"
                     className={"text-normal text-teal-600 py-3"}>
                 Return to Personnel To-Do List
               </Link>
             </div>
             <div className="p-5 w-[100%]">
               <video src={task.path} controlsList={"nodownload"} controls muted>
                 No Video
               </video>
             </div>
           </>
         )
        }
      </Card>
    </>
  )
}