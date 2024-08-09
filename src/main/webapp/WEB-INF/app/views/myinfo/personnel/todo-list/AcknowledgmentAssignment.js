import React, { useEffect, useRef, useState } from 'react';
import { Link, useLocation, useNavigate } from "react-router-dom";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { pdfjs } from 'react-pdf';
import { PDFViewer } from "@react-pdf/renderer";


export default function AcknowledgmentAssignment() {

  const location = useLocation();
  const canvasRef = useRef(null);
  const { task, completed, timestamp } = location.state;
  const [ taskInfo, setTaskInfo ] = useState({});
  const [ windowScrollBound, setWindowScrollBound ] = useState(false);

  const [ ackDocParams, setAckDocParams ] = useState({
    taskId: null,
    document: null,
    assignment: null,
    assignmentFound: false,
    acknowledged: false,
    ackTimestamp: null,
    docFound: false,
    docReady: false,
    docRead: false,
    pages: [],
    request: {
      document: false,
      ackGet: false,
      ackPost: false
    }
  });
  useEffect(() => {
    (async function () {
      // We import this here so that it's only loaded during client-side rendering.
      setTaskInfo(task);
      // var pdfjsLib = window['pdfjs-dist/build/pdf'];
      pdfjs.GlobalWorkerOptions.workerSrc = 'node_modules/pdfjs-dist/build/pdf.worker.min.js';
      const pdf = await pdfjs.getDocument("/assets/ack_docs/2018_harassment_prevention_policy.pdf").promise;

      const page = await pdf.getPage(1);
      const viewport = page.getViewport({ scale: 1.5 });
      console.log(page);

      // Prepare canvas using PDF page dimensions.
      const canvas = canvasRef.current;
      const canvasContext = canvas.getContext('2d');
      canvas.height = viewport.height;
      canvas.width = viewport.width;

      // Render PDF page into canvas context.
      const renderContext = { canvasContext, viewport };
      page.render(renderContext);
    })();
  }, []);

  useEffect(() => {
    const bindWindowScrollHandler = () => {
      if (windowScrollBound) {
        return;
      }
      window.addEventListener('scroll', checkIfDocRead);
      setWindowScrollBound(true);
      return () => {
        window.removeEventListener('scroll', checkIfDocRead);
        setWindowScrollBound(false);
      };
    };

    const checkIfDocRead = () => {
      if (ackDocParams.docReady && windowAtBottom()) {
        console.log('Window is scrolled');
        setAckDocParams(prevState => ({ ...prevState, docRead: true }))

      }
    };

    const windowAtBottom = () => {
      const windowHeight = window.innerHeight || document.documentElement.offsetHeight;
      const body = document.body;
      const html = document.documentElement;
      const docHeight = Math.max(body.scrollHeight, body.offsetHeight, html.clientHeight, html.scrollHeight, html.offsetHeight);
      const windowBottom = windowHeight + window.pageYOffset;

      // Since these height values are rounded, allow a 3px tolerance.
      return docHeight - windowBottom < 3;
    };

    bindWindowScrollHandler();

    // Cleanup function
    return () => {
      window.removeEventListener('scroll', checkIfDocRead);
    };
  }, [ ackDocParams.docReady, windowScrollBound ]);

  function getTaskAssignment() {
    setAckDocParams(prevState => ({
      ...prevState,
      assignmentFound: false,
      request: {
        ...prevState.request,
        ackGet: true
      }
    }));
  }

  return (
    <>
      <Hero>{taskInfo.title}</Hero>
      <Card className="mt-5">
        <div className="mx-[10em] text-center font-semibold py-5 text-gray-800">
          <p>Records indicate you completed this course on or before {timestamp}.</p>
        </div>
        <hr/>
        <div style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
          fontFamily: "inherit",
          boxSizing: "border-box",
          marginTop: "1em"
        }}>
          <Link to="/myinfo/personnel/todo"
                className={"text-base text-teal-500 flex-1 ml-6 p-1 text-left"}>
            Return to Personnel To-Do List</Link>
          <span className={"flex-1"}></span>
          <a
            className={"text-base text-teal-500 mr-6 flex-1 p-1 float-right text-right"}
            href={taskInfo.path}
            target="_blank"
            rel="noopener noreferrer"
          >
            Open Printable view
          </a>
        </div>
        <PDFViewer>
        </PDFViewer>
      </Card>
    </>
  );
}