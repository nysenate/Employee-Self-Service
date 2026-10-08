import React, { lazy, Suspense } from "react";
import { useConfig } from "app/hooks/useConfig";
import LoadingStatus from "app/components/LoadingStatus";
import NotFound from "app/views/NotFound";

const FeedbackPlayground = lazy(
  () =>
    import(
      /* webpackChunkName: "feedback-playground" */ "./FeedbackPlayground"
    ),
);

/** Do not mount simulations or load their bundle outside an explicitly dev runtime. */
export default function FeedbackPlaygroundRoute() {
  const config = useConfig();
  const loading = (
    <LoadingStatus
      message="Loading feedback playground…"
      layout="centered"
      className="min-h-48"
    />
  );
  if (config.isPending) return loading;
  if (config.isError || config.data?.runtimeLevel !== "dev")
    return <NotFound />;
  return (
    <Suspense fallback={loading}>
      <FeedbackPlayground />
    </Suspense>
  );
}
