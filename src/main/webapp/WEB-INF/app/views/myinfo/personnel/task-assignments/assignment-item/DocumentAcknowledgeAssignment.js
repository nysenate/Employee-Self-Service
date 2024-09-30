import React, { useEffect, useRef, useState } from 'react';
import { Link, useLocation } from "react-router-dom";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import * as pdfjsLib from "pdfjs-dist";
import { isoToLongDate } from "app/utils/dateUtils";
import useScrollDetection from "app/views/myinfo/personnel/task-assignments/useScrollDetection";
import LoadingIndicator from "app/components/LoadingIndicator";


export default function DocumentAcknowledgeAssignment({ assignment }) {
  const [ doc, setDoc ] = useState(null)
  const [ pageNumbers, setPageNumbers ] = useState([])
  const isScrolledToBottom = useScrollDetection()

  useEffect(() => {
    (async () => {
      if (assignment) {
        const [ doc, pageNums ] = await loadPdf(assignment.task.path)
        setDoc(doc)
        setPageNumbers(pageNums)
      }
    })()
  }, [ assignment ])

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

function RenderPdf({ pdfPath }) {

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

  const ctx = canvas.getContext("2d");
  const dpr = window.devicePixelRatio || 1
  const bsr = ctx.webkitBackingStorePixelRatio ||
    ctx.mozBackingStorePixelRatio ||
    ctx.msBackingStorePixelRatio ||
    ctx.oBackingStorePixelRatio ||
    ctx.backingStorePixelRatio || 1
  const ratio = dpr / bsr

  const viewport = page.getViewport({ scale: 1.4, });
  canvas.width = viewport.width * ratio
  canvas.height = viewport.height * ratio
  ctx.setTransform(ratio, 0, 0, ratio, 0, 0)

  const renderContext = {
    canvasContext: ctx,
    viewport: viewport
  };
  await page.render(renderContext);
}
