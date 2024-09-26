import React, { useEffect, useRef, useState } from 'react';
import { Link, useLocation } from "react-router-dom";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import * as pdfjsLib from "pdfjs-dist";


const TEST_PDF_URL = "/assets/ack_docs/2018_harassment_prevention_policy.pdf"

export default function AcknowledgmentAssignment() {
  const [ doc, setDoc ] = useState(null);
  const [ pageNumbers, setPageNumbers ] = useState([]);

  useEffect(() => {
    (async () => {
      const [ doc, pageNums ] = await loadPdf(TEST_PDF_URL)
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
    <Card className="">
      <div className="w-full">
        {pageNumbers.map((i) => <canvas id={`pdf-canvas-${i}`} className="w-full" width="880" key={i}></canvas>)}
      </div>
    </Card>
  )
}

async function loadPdf(url) {
  const doc = await pdfjsLib.getDocument(url).promise
  const numPages = doc.numPages
  const pages = []
  for (let i = 1; i <= numPages; i++) { // TODO ensure numPages is updated...
    pages.push(i)
  }
  return [ doc, pages ]
}

async function renderPage(doc, pageNum) {
  const page = await doc.getPage(pageNum)
  const canvasId = `pdf-canvas-${pageNum}`
  const canvas = document.getElementById(canvasId);

  const viewport = page.getViewport({ scale: 4.0 });
  canvas.width = viewport.width;
  canvas.height = viewport.height;
  const ctx = canvas.getContext("2d");
  const renderTask = page.render({
    canvasContext: ctx,
    viewport,
  });
}
