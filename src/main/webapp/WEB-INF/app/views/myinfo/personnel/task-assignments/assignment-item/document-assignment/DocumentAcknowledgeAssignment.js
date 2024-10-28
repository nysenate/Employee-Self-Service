import React, { useEffect, useRef, useState } from 'react';
import { Link, useLocation, useNavigate } from "react-router-dom";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import * as pdfjsLib from "pdfjs-dist";
import { isoToLongDate } from "app/utils/dateUtils";
import useScrollDetection
  from "app/views/myinfo/personnel/task-assignments/assignment-item/document-assignment/useScrollDetection";
import LoadingIndicator from "app/components/LoadingIndicator";
import { Button } from "app/components/Button";
import Modal from "app/components/Modal";
import ModalNotice from "app/components/ModalNotice";
import { useAcknowledgeDocument } from "app/api/taskAssignmentApi";
import useAuth from "app/contexts/Auth/useAuth";
import ModalLoading from "app/components/ModalLoading";


export default function DocumentAcknowledgeAssignment({ assignment }) {
  const auth = useAuth()
  const navigate = useNavigate()
  const acknowledgeDocumentApi = useAcknowledgeDocument()
  const isScrolledToBottom = useScrollDetection()
  const [ isConfirmationModalOpen, setIsConfirmationModalOpen ] = useState(false);
  const [ isSuccessModalOpen, setIsSuccessModalOpen ] = useState(false);
  const [ isAcknowledgmentInProgress, setIsAcknowledgmentInProgress ] = useState(false);

  const onAcknowledge = () => {
    setIsConfirmationModalOpen(true)
    setIsAcknowledgmentInProgress(true)
  }

  const onAcknowledgeConfirmation = () => {
    setIsConfirmationModalOpen(false)
    acknowledgeDocumentApi.mutateAsync({
      empId: auth.empId(),
      taskId: assignment.taskId
    }).then(() => {
      setIsSuccessModalOpen(true)
      setIsAcknowledgmentInProgress(false)
    })
  }

  const onSuccessModalResolved = () => {
    setIsConfirmationModalOpen(false)
    navigate('/myinfo/personnel/tasks/assignments')
  }

  return (
    <>
      <Hero>{assignment.task.title}</Hero>
      <Card className="mt-5">
        {assignment.completed
         ? <Card.Header>You acknowledged this policy/document on {isoToLongDate(assignment.timestamp)}</Card.Header>
         : <Card.Header>
           Please review this policy/document and click the button to acknowledge it.
           <br/>
           <span className="font-bold">You must scroll to the end of the page for the button to become available.</span>
           <br/>
           If desired, click "Open Printable View" to open a separate tab to print the document.
         </Card.Header>
        }

        <div className="m-5 flex justify-between">
          <Link to="/myinfo/personnel/tasks/assignments">
            Return to Personnel To-Do List
          </Link>
          <a href={assignment.task.path} target="_blank" rel="noopener noreferrer">
            Open Printable View
          </a>
        </div>

        <div className="pb-5">
          <RenderPdf pdfPath={assignment.task.path}/>
        </div>

        {!assignment.completed &&
          <AcknowledgeBanner isScrolledToBottom={isScrolledToBottom}
                             onAcknowledged={onAcknowledge}
                             isAcknowledgmentInProgress={isAcknowledgmentInProgress}/>
        }
      </Card>

      <ConfirmationModal assignment={assignment}
                         isOpen={isConfirmationModalOpen}
                         onResolve={onAcknowledgeConfirmation}
                         onReject={() => {
                           setIsConfirmationModalOpen(false)
                           setIsAcknowledgmentInProgress(false)
                         }}/>
      <ModalNotice isOpen={isSuccessModalOpen}
                   onResolve={onSuccessModalResolved}
                   title="Acknowledgement Complete"
                   body="You have successfully acknowledged this policy/document."/>
    </>
  )
}

function ConfirmationModal({ assignment, isOpen, onResolve, onReject }) {
  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>
        Acknowledge Policy/Document
      </Modal.Title>
      <Modal.Body>
        <div className="max-w-lg">
          <p>
            I hereby acknowledge receipt of the New York State Senate
            <span className="font-bold"> {assignment.task.title} </span>
            and state that I have read the same. I understand that compliance is a condition of employment and that
            violation of any policy could subject me to penalties including, but not limited to, loss of privileges to
            use Senate technologies, demotion, suspension or termination.
          </p>
          <p className="mt-3">
            In addition for purposes of submitting this acknowledgment, the username and password is the electronic
            signature of the employee. As liability attaches, the employee should ensure that his or her username and
            password is securely kept and used.
          </p>
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button color="success" onClick={onResolve}>
          I Agree
        </Button>
        <Button color="secondary" onClick={onReject}>
          Cancel
        </Button>
      </Modal.Buttons>
    </Modal>

  )
}

/**
 * Displays a fixed banner at the bottom of the page containing an Acknowledge button and
 * directions to read the entire document.
 */
function AcknowledgeBanner({ isScrolledToBottom, onAcknowledged, isAcknowledgmentInProgress }) {
  return (
    <div className="fixed bottom-0 w-[880px] border-x-4 border-t-4 border-green-600">
      <Card className="py-5">
        <div className="flex justify-between align-baseline">
          <div className="w-5/12"></div>
          <div className="w-2/12 text-center">
            <Button type="submit"
                    color="success"
                    disabled={!isScrolledToBottom || isAcknowledgmentInProgress}
                    onClick={onAcknowledged}
            >
              Acknowledge
            </Button>
          </div>
          <div className="w-5/12">
            {!isScrolledToBottom &&
              <div className="font-semibold text-red-600">
                You must read the entire document to acknowledge
              </div>
            }
          </div>
        </div>
      </Card>
    </div>
  )
}

function RenderPdf({ pdfPath }) {
  const [ doc, setDoc ] = useState(null)
  const [ pageNumbers, setPageNumbers ] = useState([])

  useEffect(() => {
    (async () => {
      if (pdfPath) {
        const [ doc, pageNums ] = await loadPdf(pdfPath)
        setDoc(doc)
        setPageNumbers(pageNums)
      }
    })()
  }, [ pdfPath ])

  useEffect(() => {
    if (doc && pageNumbers) {
      for (let pageNum = pageNumbers[0]; pageNum <= pageNumbers.at(-1); pageNum++) {
        renderPage(doc, pageNum)
      }
    }
  }, [ doc, pageNumbers ])

  return (
    <div className="">
      {pageNumbers.map((i) => <canvas id={`pdf-canvas-${i}`} className="w-full" width="880" key={i}></canvas>)}
    </div>
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
