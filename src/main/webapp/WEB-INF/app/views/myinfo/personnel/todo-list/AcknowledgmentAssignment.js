import React, { useEffect, useRef, useState } from 'react';
import { Link, useLocation } from "react-router-dom";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import * as pdfjsLib from "pdfjs-dist";
import { isoToLongDate } from "app/utils/dateUtils";
import useScrollDetection from "app/hooks/useScrollDetection";


export default function AcknowledgmentAssignment() {
  const [ doc, setDoc ] = useState(null)
  const [ pageNumbers, setPageNumbers ] = useState([])
  const assignment = TEST_ASSIGNMENT // TODO load assignment from id in url
  const isScrolledToBottom = useScrollDetection()

  useEffect(() => {
    (async () => {
      const [ doc, pageNums ] = await loadPdf(assignment.task.path)
      setDoc(doc)
      setPageNumbers(pageNums)
    })()
  }, [])

  useEffect(() => {
    if (doc && pageNumbers) {
      for (let pageNum = pageNumbers[0]; pageNum <= pageNumbers.at(-1); pageNum++) {
        renderPage(doc, pageNum)
      }
    }
  }, [ doc, pageNumbers ])

  return (
    <>
      <Hero>{assignment.task.title}</Hero>
      <Card className="mt-5">
        <Card.Header>
          You acknowledged this policy/document on {isoToLongDate(assignment.timestamp)}
        </Card.Header>
        <div className="m-5 flex justify-between">
          <Link to="/myinfo/personnel/todo">
            Return to Personnel To-Do List
          </Link>
          <a href={assignment.task.path} target="_blank" rel="noopener noreferrer">
            Open Printable View
          </a>
        </div>
        <div className="">
          {pageNumbers.map((i) => <canvas id={`pdf-canvas-${i}`} className="w-full" width="880" key={i}></canvas>)}
        </div>
      </Card>
    </>
  )
}

async function loadPdf(url) {
  const doc = await pdfjsLib.getDocument(url).promise
  const numPages = doc.numPages
  const pages = []
  for (let i = 1; i <= numPages; i++) {
    pages.push(i)
  }
  return [ doc, pages ]
}

async function renderPage(doc, pageNum) {
  const page = await doc.getPage(pageNum)
  const canvasId = `pdf-canvas-${pageNum}`
  const canvas = document.getElementById(canvasId);

  const viewport = page.getViewport({ scale: 2.0 });
  canvas.width = viewport.width;
  canvas.height = viewport.height;
  const ctx = canvas.getContext("2d");
  const renderTask = page.render({
    canvasContext: ctx,
    viewport,
  });
}

const TEST_ASSIGNMENT = {
  "empId": 11168,
  "taskId": 1,
  "timestamp": "2019-10-29T15:08:01.903",
  "updateUserId": 11168,
  "completed": true,
  "active": true,
  "task": {
    "taskId": 1,
    "taskType": "DOCUMENT_ACKNOWLEDGMENT",
    "title": "2019 NYS Senate Harassment and Discrimination Policy",
    "effectiveDateTime": "2019-10-28T18:27:55.001187",
    "endDateTime": null,
    "active": false,
    "notifiable": false,
    "url": null,
    "resource": "2019_harassment_prevention_policy.pdf",
    "path": "/assets/ack_docs/2019_harassment_prevention_policy.pdf"
  }
}