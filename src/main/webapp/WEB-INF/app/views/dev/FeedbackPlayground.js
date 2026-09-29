import React, { useEffect, useRef, useState } from "react";
import Button from "app/components/Button";
import Card from "app/components/Card";
import BusyRegion from "app/components/BusyRegion";
import ErrorAlert from "app/components/ErrorAlert";
import LoadingStatus from "app/components/LoadingStatus";
import { useNotifySuccess } from "app/components/NotificationProvider";
import { ThemeContext, themes } from "app/ThemeContext";
import {
  ApplicationSubmissionDialog,
  SubmissionSuccessModal,
} from "app/views/travel/application/submit/components/SubmissionModals";
import TravelResultsHeader from "app/views/travel/shared/components/TravelResultsHeader";
import TravelResultsContent from "app/views/travel/shared/components/TravelResultsContent";

export default function FeedbackPlayground() {
  const [duration, setDuration] = useState(3);
  const [outcome, setOutcome] = useState("success");
  const [segments, setSegments] = useState(1);
  const options = { duration, outcome };
  return (
    <ThemeContext.Provider value={themes.travel}>
      <main className="mx-auto max-w-5xl space-y-6 px-6 pb-12">
        <header className="border-l-4 border-orange-600 pl-5">
          <p className="text-sm font-semibold tracking-wide text-orange-800 uppercase">
            Development tools
          </p>
          <h1 className="mt-1 text-3xl font-semibold">Feedback playground</h1>
          <p className="mt-2 text-gray-600">
            Try the real ESS loading and notification components. All actions on
            this page are simulations; they do not save, submit, or delete
            application data.
          </p>
        </header>

        <Card className="sticky top-12 z-20">
          <Card.Content className="flex flex-wrap items-end gap-6 p-5">
            <label className="flex flex-col gap-1 font-medium">
              Simulated delay
              <select
                className="input"
                value={duration}
                onChange={(event) => setDuration(Number(event.target.value))}
              >
                <option value={1}>1 second</option>
                <option value={3}>3 seconds</option>
                <option value={8}>8 seconds</option>
              </select>
            </label>
            <label className="flex flex-col gap-1 font-medium">
              Simulated result
              <select
                className="input"
                value={outcome}
                onChange={(event) => setOutcome(event.target.value)}
              >
                <option value="success">Success</option>
                <option value="error">Failure</option>
              </select>
            </label>
            <label className="flex flex-col gap-1 font-medium">
              Route card length
              <select
                className="input"
                value={segments}
                onChange={(event) => setSegments(Number(event.target.value))}
              >
                <option value={1}>1 segment</option>
                <option value={3}>3 segments</option>
                <option value={6}>6 segments</option>
              </select>
            </label>
            <p className="text-sm text-gray-600">
              Delay and result apply to the next operation you start.
            </p>
          </Card.Content>
        </Card>

        <NotificationExample />
        <div className="grid gap-6 md:grid-cols-2">
          <InitialLoadingExample options={options} />
          <ButtonExample options={options} />
        </div>
        <RefreshExample options={options} />
        <RegionExample options={options} segments={segments} />
        <SubmissionExample options={options} />
        <DeveloperReference />
      </main>
    </ThemeContext.Provider>
  );
}

// Simulations intentionally use local timers rather than application query hooks.
// Each run captures its settings and cancels its timer on reset or unmount.
function useSimulation({ duration, outcome }, onSuccess) {
  const [status, setStatus] = useState("idle");
  const timer = useRef(null);
  useEffect(() => () => window.clearTimeout(timer.current), []);
  function reset() {
    window.clearTimeout(timer.current);
    timer.current = null;
    setStatus("idle");
  }
  function start() {
    if (timer.current !== null) return;
    setStatus("pending");
    timer.current = window.setTimeout(() => {
      timer.current = null;
      setStatus(outcome);
      if (outcome === "success") onSuccess?.();
    }, duration * 1000);
  }
  return { status, isPending: status === "pending", start, reset };
}

function Example({ title, description, children }) {
  return (
    <section aria-label={title}>
      <Card>
        <Card.Content className="space-y-4 p-6">
          <div>
            <h2 className="text-xl font-semibold">{title}</h2>
            <p className="mt-1 text-gray-600">{description}</p>
          </div>
          {children}
        </Card.Content>
      </Card>
    </section>
  );
}

function NotificationExample() {
  const notify = useNotifySuccess();
  const [message, setMessage] = useState("Draft saved");
  return (
    <Example
      title="Success notifications"
      description="A brief confirmation that leaves you free to continue. Hover or focus the toast to pause its timer."
    >
      <label className="flex max-w-lg flex-col gap-1 font-medium">
        Notification message
        <input
          className="input"
          value={message}
          maxLength={240}
          onChange={(event) => setMessage(event.target.value)}
        />
      </label>
      <div className="flex flex-wrap gap-3">
        <Button
          onPress={() => notify(message.trim())}
          isDisabled={!message.trim()}
        >
          Show success toast
        </Button>
        <Button
          variant="secondary"
          onPress={() => {
            notify("Draft saved");
            notify("Application approved");
            notify("Changes saved to application #1234");
          }}
        >
          Queue three messages
        </Button>
      </div>
      <p className="text-sm text-gray-600">
        Repeated identical messages refresh the same notification. Distinct
        messages appear one at a time.
      </p>
    </Example>
  );
}

function InitialLoadingExample({ options }) {
  const demo = useSimulation(options);
  return (
    <Example
      title="Initial content loading"
      description="Use when the requested content is not available yet."
    >
      <div className="min-h-36 rounded border border-gray-200 p-4">
        {demo.isPending ? (
          <LoadingStatus
            message="Loading travel applications…"
            layout="centered"
            size="lg"
            className="min-h-28"
          />
        ) : demo.status === "error" ? (
          <DemoError
            message="The example applications could not be loaded."
            retry={demo.start}
          />
        ) : (
          <p>
            {demo.status === "success"
              ? "Example applications are ready to view."
              : "Start a load to see the placeholder."}
          </p>
        )}
      </div>
      <Button onPress={demo.start} isDisabled={demo.isPending}>
        Load example content
      </Button>
    </Example>
  );
}

function ButtonExample({ options }) {
  const notify = useNotifySuccess();
  const demo = useSimulation(options, () => notify("Demo changes saved"));
  return (
    <Example
      title="Button pending state"
      description="Use for quick saves, followed by success or error feedback. Disable conflicting inputs when needed; preserve their appearance without an overlay."
    >
      <label className="flex flex-col gap-1 font-medium">
        Example note
        <input className="input" defaultValue="This field remains available." />
      </label>
      {demo.status === "error" && (
        <DemoError message="The example changes could not be saved." />
      )}
      <Button onPress={demo.start} isPending={demo.isPending}>
        Save demo changes
      </Button>
    </Example>
  );
}

function RefreshExample({ options }) {
  const [mode, setMode] = useState("refreshing");
  const demo = useSimulation(options);
  const status = demo.isPending ? mode : "ready";
  return (
    <Example
      title="Background results refresh"
      description="Keep existing results visible. Put progress in the existing header without changing its height. Fade old results during filter transitions; show background refresh progress only when it helps explain the wait."
    >
      <TravelResultsHeader
        count={2}
        total={2}
        offset={1}
        itemLabel={{ singular: "example", plural: "examples" }}
        status={status}
      />
      <TravelResultsContent status={status}>
        <ul className="divide-y divide-gray-200 border border-gray-200">
          <li className="p-3">Example application #1234 · Pending review</li>
          <li className="p-3">Example application #1235 · Approved</li>
        </ul>
      </TravelResultsContent>
      {demo.status === "error" && (
        <DemoError message="The refresh failed. The previous results remain available." />
      )}
      <div className="flex flex-wrap gap-3">
        <Button
          onPress={() => {
            setMode("refreshing");
            demo.start();
          }}
          isDisabled={demo.isPending}
        >
          Refresh results
        </Button>
        <Button
          variant="secondary"
          onPress={() => {
            setMode("transitioning");
            demo.start();
          }}
          isDisabled={demo.isPending}
        >
          Simulate filter change
        </Button>
      </div>
    </Example>
  );
}

function RegionExample({ options, segments }) {
  const demo = useSimulation(options);
  return (
    <section aria-label="Card loading overlay" className="space-y-3">
      <div className="flex items-center justify-between gap-3">
        <p className="text-gray-600">
          Try one segment for centering, or six segments to check scrolling.
        </p>
        <Button variant="quiet" onPress={demo.reset}>
          Reset route demo
        </Button>
      </div>
      <BusyRegion message={demo.isPending ? "Calculating your route…" : null}>
        <Card>
          <Card.Content className="space-y-5 p-6">
            <div>
              <h2 className="text-xl font-semibold">Card loading overlay</h2>
              <p className="mt-1 text-gray-600">
                Use for blocking operations where a prominent explanation of the
                wait helps. The affected form stays visible. Quick saves can
                disable edits without this overlay.
              </p>
            </div>
            {Array.from({ length: segments }, (_, index) => (
              <div
                key={index}
                className="grid gap-4 border-t border-gray-200 py-6 sm:grid-cols-2"
              >
                <label className="flex flex-col gap-1 font-medium">
                  Segment {index + 1} departure
                  <input className="input" defaultValue="Albany, NY" />
                </label>
                <label className="flex flex-col gap-1 font-medium">
                  Segment {index + 1} arrival
                  <input className="input" defaultValue="New York, NY" />
                </label>
              </div>
            ))}
            {demo.status === "error" && (
              <DemoError message="The example route could not be calculated. Your entries are still available." />
            )}
            {demo.status === "success" && (
              <p className="font-medium text-green-800">
                Example route calculated. The application would now advance to
                Expenses.
              </p>
            )}
            <div className="flex justify-end">
              <Button onPress={demo.start} isPending={demo.isPending}>
                Calculate route
              </Button>
            </div>
          </Card.Content>
        </Card>
      </BusyRegion>
    </section>
  );
}

function SubmissionExample({ options }) {
  const [confirming, setConfirming] = useState(false);
  const demo = useSimulation(options);
  function reset() {
    setConfirming(false);
    demo.reset();
  }
  return (
    <Example
      title="Submission dialog"
      description="Confirmation becomes progress, then completion or a recoverable error. The page is unavailable while the dialog is open."
    >
      <Button onPress={() => setConfirming(true)}>Try submission dialog</Button>
      <p className="text-sm text-gray-600">
        Both completion choices reset this example. They do not navigate away or
        log you out.
      </p>
      <ApplicationSubmissionDialog
        isOpen={confirming || demo.isPending || demo.status === "error"}
        title="Submit demo application?"
        body="This simulates submission without sending an application."
        actionLabel="Submit demo application"
        onCancel={reset}
        onConfirm={() => {
          setConfirming(false);
          demo.start();
        }}
        isPending={demo.isPending}
        pendingText="Submitting demo application…"
        error={
          demo.status === "error" ? (
            <DemoError message="The example submission failed. Return to the application and try again." />
          ) : null
        }
      />
      <SubmissionSuccessModal
        isOpen={demo.status === "success"}
        onReturn={reset}
        onLogout={reset}
      />
    </Example>
  );
}

function DemoError({ message, retry }) {
  return (
    <ErrorAlert title="Simulated failure">
      <p>{message}</p>
      {retry && (
        <Button className="mt-3" onPress={retry}>
          Try again
        </Button>
      )}
    </ErrorAlert>
  );
}

function DeveloperReference() {
  const patterns = [
    [
      "Spinner",
      "Decorative indicator inside a labeled control. Never the only accessible feedback.",
    ],
    [
      "LoadingStatus",
      "Use centered loading when content is unavailable. Keep refresh progress inside the existing header with a stable height; avoid temporary rows that shift content.",
    ],
    [
      "Button isPending",
      "Use for quick saves and prevent repeat actions. Conflicting controls may also be disabled without changing appearance. The button owns its accessible label; its Spinner is decorative.",
    ],
    [
      "BusyRegion",
      "Use when a blocking operation needs prominent progress feedback. Disabling edits alone does not require an overlay. Blocks only the wrapped content and owns its live announcement.",
    ],
    [
      "Modal + LoadingStatus",
      "Use inside an existing dialog when the task needs page-wide focus containment. ApplicationSubmissionDialog is Travel’s confirmation/progress/error flow.",
    ],
    [
      "NotificationProvider / useNotifySuccess",
      "One provider in EssLayout. Send confirmed, non-actionable successes; notifications persist across page navigation.",
    ],
    [
      "ErrorAlert",
      "Persistent errors near affected content. Preserve entered data and provide recovery; do not turn these into disappearing toasts.",
    ],
  ];
  return (
    <details className="border border-gray-200 bg-white p-6">
      <summary className="cursor-pointer text-lg font-semibold">
        Developer reference: choosing a feedback pattern
      </summary>
      <p className="my-4 text-gray-600">
        Request state stays in the existing query or mutation hook. These
        components present it; there is no global loading counter. Use one live
        announcement per operation. Set LoadingStatus announce={"{false}"}{" "}
        inside an existing live region.
      </p>
      <p className="my-4 text-gray-600">
        A save-triggered background refresh usually needs no second loading
        message. Keep the form and edits visible, and confirm the successful
        save. Avoid reserving empty space for feedback that adds little value. A
        filter transition means the user selected different results, such as
        another paycheck year; retain and fade the previous results while
        loading.
      </p>
      <table className="w-full text-left">
        <thead>
          <tr className="border-b border-gray-200">
            <th className="py-2 pr-4">Component</th>
            <th className="py-2">When to use it</th>
          </tr>
        </thead>
        <tbody>
          {patterns.map(([name, guidance]) => (
            <tr key={name} className="border-b border-gray-100">
              <td className="py-3 pr-4 align-top">
                <code>{name}</code>
              </td>
              <td className="py-3">{guidance}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <pre className="mt-4 overflow-x-auto bg-gray-50 p-4 text-sm">
        <code>{`import LoadingStatus from "app/components/LoadingStatus";
import BusyRegion from "app/components/BusyRegion";
import { useNotifySuccess } from "app/components/NotificationProvider";

<LoadingStatus message="Loading applications…" layout="centered" className="min-h-48" />
<BusyRegion message={isPending ? "Calculating your route…" : null}>
  <RouteForm />
</BusyRegion>

const notifySuccess = useNotifySuccess();
// After the server confirms the operation:
notifySuccess("Draft saved");`}</code>
      </pre>
    </details>
  );
}
