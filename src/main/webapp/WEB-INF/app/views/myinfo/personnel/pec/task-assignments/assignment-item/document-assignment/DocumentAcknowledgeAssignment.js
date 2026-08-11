import React, { useEffect, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Hero from "app/components/Hero";
import Card from "app/components/Card";
import { isoToLongDate } from "app/utils/dateUtils";
import useScrollDetection from "app/views/myinfo/personnel/pec/task-assignments/assignment-item/document-assignment/useScrollDetection";
import Button from "app/components/Button";
import Modal from "app/components/Modal";
import ModalNotice from "app/components/ModalNotice";
import { useAcknowledgeDocument } from "app/views/myinfo/personnel/pec/useTaskAssignment";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";

export default function DocumentAcknowledgeAssignment({ assignment }) {
  const { data: user } = useRequireAuthedUser();
  const navigate = useNavigate();
  const acknowledgeDocumentApi = useAcknowledgeDocument();
  const isScrolledToBottom = useScrollDetection();
  const [isConfirmationModalOpen, setIsConfirmationModalOpen] = useState(false);
  const [isSuccessModalOpen, setIsSuccessModalOpen] = useState(false);
  const [isErrorModalOpen, setIsErrorModalOpen] = useState(false);
  const [isAcknowledgmentInProgress, setIsAcknowledgmentInProgress] =
    useState(false);

  const onAcknowledge = () => {
    setIsErrorModalOpen(false);
    setIsConfirmationModalOpen(true);
  };

  const onAcknowledgeConfirmation = () => {
    setIsAcknowledgmentInProgress(true);
    acknowledgeDocumentApi
      .mutateAsync({
        empId: user.employeeId,
        taskId: assignment.taskId,
      })
      .then(() => {
        setIsConfirmationModalOpen(false);
        setIsSuccessModalOpen(true);
      })
      .catch(() => {
        setIsConfirmationModalOpen(false);
        setIsErrorModalOpen(true);
      })
      .finally(() => {
        setIsAcknowledgmentInProgress(false);
      });
  };

  const onSuccessModalResolved = () => {
    setIsConfirmationModalOpen(false);
    navigate("/myinfo/personnel/tasks/assignments");
  };

  return (
    <>
      <Hero>{assignment.task.title}</Hero>
      <Card className="mt-5">
        {assignment.completed ? (
          <Card.Header>
            You acknowledged this policy/document on{" "}
            {isoToLongDate(assignment.timestamp)}
          </Card.Header>
        ) : (
          <Card.Header>
            Please review this policy/document and click the button to
            acknowledge it.
            <br />
            <span className="font-bold">
              You must scroll to the end of the page for the button to become
              available.
            </span>
            <br />
            If desired, click "Open Printable View" to open a separate tab to
            print the document.
          </Card.Header>
        )}

        <div className="m-5 flex justify-between">
          <Link to="/myinfo/personnel/tasks/assignments">
            Return to Personnel To-Do List
          </Link>
          <a
            href={assignment.task.path}
            target="_blank"
            rel="noopener noreferrer"
          >
            Open Printable View
          </a>
        </div>

        <div className="pb-5">
          <RenderPdf pdfPath={assignment.task.path} />
        </div>

        {!assignment.completed && (
          <AcknowledgeBanner
            isScrolledToBottom={isScrolledToBottom}
            onAcknowledged={onAcknowledge}
            isAcknowledgmentInProgress={isAcknowledgmentInProgress}
          />
        )}
      </Card>

      <ConfirmationModal
        assignment={assignment}
        isOpen={isConfirmationModalOpen}
        onResolve={onAcknowledgeConfirmation}
        onReject={() => {
          setIsConfirmationModalOpen(false);
        }}
        isAcknowledgmentInProgress={isAcknowledgmentInProgress}
      />
      <ModalNotice
        isOpen={isSuccessModalOpen}
        onClose={onSuccessModalResolved}
        title="Acknowledgement Complete"
        body="You have successfully acknowledged this policy/document."
      />
      <ModalNotice
        isOpen={isErrorModalOpen}
        onClose={() => setIsErrorModalOpen(false)}
        title="Acknowledgement Failed"
        body="We were unable to record your acknowledgement. Please try again."
      />
    </>
  );
}

function ConfirmationModal({
  assignment,
  isOpen,
  onResolve,
  onReject,
  isAcknowledgmentInProgress,
}) {
  return (
    <Modal isOpen={isOpen}>
      <Modal.Title>Acknowledge Policy/Document</Modal.Title>
      <Modal.Body>
        <div className="max-w-lg">
          <p>
            I hereby acknowledge receipt of the New York State Senate
            <span className="font-bold"> {assignment.task.title} </span>
            and state that I have read the same. I understand that compliance is
            a condition of employment and that violation of any policy could
            subject me to penalties including, but not limited to, loss of
            privileges to use Senate technologies, demotion, suspension or
            termination.
          </p>
          <p className="mt-3">
            In addition for purposes of submitting this acknowledgment, the
            username and password is the electronic signature of the employee.
            As liability attaches, the employee should ensure that his or her
            username and password is securely kept and used.
          </p>
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button
          variant="primary"
          onPress={onResolve}
          isPending={isAcknowledgmentInProgress}
        >
          I Agree
        </Button>
        <Button
          variant="secondary"
          onPress={onReject}
          isDisabled={isAcknowledgmentInProgress}
        >
          Cancel
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

/**
 * Displays a fixed banner at the bottom of the page containing an Acknowledge button and
 * directions to read the entire document.
 */
function AcknowledgeBanner({
  isScrolledToBottom,
  onAcknowledged,
  isAcknowledgmentInProgress,
}) {
  return (
    <div className="fixed bottom-0 w-[880px] border-x-4 border-t-4 border-green-600">
      <Card className="py-5">
        <div className="flex justify-between align-baseline">
          <div className="w-5/12"></div>
          <div className="w-2/12 text-center">
            <Button
              type="submit"
              variant="primary"
              isDisabled={!isScrolledToBottom || isAcknowledgmentInProgress}
              onPress={onAcknowledged}
            >
              Acknowledge
            </Button>
          </div>
          <div className="w-5/12">
            {!isScrolledToBottom && (
              <div className="font-semibold text-red-600">
                You must read the entire document to acknowledge
              </div>
            )}
          </div>
        </div>
      </Card>
    </div>
  );
}

/**
 * PDF.js is large enough that it should not be part of any page's initial download.
 * It is imported on demand the first time a document is rendered, and the resulting
 * promise is cached so later renders reuse the already loaded module.
 */
let pdfjsPromise = null;

function loadPdfjs() {
  if (!pdfjsPromise) {
    pdfjsPromise = import("pdfjs-dist").then((pdfjsLib) => {
      // Run parsing in a real worker instead of on the main thread. The worker file is
      // emitted by the "pdf.worker" webpack entry; __webpack_public_path__ resolves to
      // "/" under the dev server and "/assets/dist/" in a production build.
      pdfjsLib.GlobalWorkerOptions.workerSrc = `${__webpack_public_path__}pdf.worker.bundle.js`;
      return pdfjsLib;
    });
  }
  return pdfjsPromise;
}

function RenderPdf({ pdfPath }) {
  const [doc, setDoc] = useState(null);
  const [pages, setPages] = useState([]);

  useEffect(() => {
    let isMounted = true;
    (async () => {
      if (pdfPath) {
        const pdfjsLib = await loadPdfjs();
        if (!isMounted) return;

        const loadingTask = pdfjsLib.getDocument(pdfPath);
        const pdf = await loadingTask.promise;
        if (!isMounted) return;

        setDoc(pdf);
        setPages(Array.from({ length: pdf.numPages }, (_, i) => i + 1));
      }
    })();
    return () => {
      isMounted = false;
    };
  }, [pdfPath]);

  return (
    <div>
      {pages.map((pageNum) => (
        <PdfPage key={pageNum} doc={doc} pageNum={pageNum} />
      ))}
    </div>
  );
}

function PdfPage({ doc, pageNum }) {
  const canvasRef = useRef(null);

  useEffect(() => {
    if (!doc) return;
    let renderTask;

    (async () => {
      const page = await doc.getPage(pageNum);

      const viewport = page.getViewport({
        scale: 1.5,
        rotation: page.rotate, // respect PDF metadata
      });

      const canvas = canvasRef.current;
      const ctx = canvas.getContext("2d");

      canvas.width = viewport.width;
      canvas.height = viewport.height;

      renderTask = page.render({ canvasContext: ctx, viewport });
      await renderTask.promise;
    })();

    return () => {
      if (renderTask) renderTask.cancel();
    };
  }, [doc, pageNum]);

  return <canvas ref={canvasRef} className="w-full" />;
}
