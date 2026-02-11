import React, { useEffect, useMemo } from "react";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import { endOfDay, formatISO, startOfDay, subMonths } from "date-fns";
import { useSearchParams } from "react-router-dom";
import { UTCDate } from "@date-fns/utc";
import InputDebounced from "app/components/InputDebounced";
import { useReviewHistory } from "app/views/travel/reviewer/history/useReviewHistory";
import NoMatchesFound from "app/components/NoMatchesFound";
import LoadingIndicator from "app/components/LoadingIndicator";
import TravelAppSummaryTable from "app/views/travel/shared/components/TravelAppSummaryTable";
import TravelAppReviewForm from "app/views/travel/shared/components/TravelAppReviewForm";
import { useTravelReview } from "app/views/travel/shared/hooks/useTravelReview";
import Modal from "app/components/Modal";
import Button from "app/components/Button";
import Pagination from "app/components/Pagination";
import Card from "app/components/Card";

const initialState = {
  fromDate: formatISO(subMonths(new Date(), 1), { representation: "date" }),
  toDate: formatISO(new Date(), { representation: "date" }),
  limit: 12,
  offset: 1,
};

function fromSearchParams(searchParams) {
  return {
    fromDate: searchParams.get("fromDate") ?? initialState.fromDate,
    toDate: searchParams.get("toDate") ?? initialState.toDate,
    limit: Number(searchParams.get("limit") ?? initialState.limit),
    offset: Number(searchParams.get("offset") ?? initialState.offset),
  };
}

export default function ReviewHistory() {
  const [searchParams, setSearchParams] = useSearchParams();
  const state = useMemo(() => fromSearchParams(searchParams), [searchParams]);

  // Push default values to the URL if not present.
  useEffect(() => {
    const params = new URLSearchParams(searchParams);
    let changed = false;

    [
      ["fromDate", state.fromDate],
      ["toDate", state.toDate],
      ["limit", state.limit],
      ["offset", state.offset],
    ].forEach(([key, value]) => {
      if (!params.get(key) && value != null) {
        params.set(key, value.toString());
        changed = true;
      }
    });

    if (changed) {
      setSearchParams(params, { replace: true });
    }
  }, [searchParams, setSearchParams, state]);

  const updateSearchParams = (updates) => {
    const params = new URLSearchParams(searchParams);
    Object.entries(updates).forEach(([key, value]) => {
      if (value === undefined || value === null || value === "") {
        params.delete(key);
      } else {
        params.set(key, value.toString());
      }
    });
    setSearchParams(params, { replace: true });
  };

  const historyQuery = useReviewHistory({
    from: formatISO(startOfDay(new UTCDate(state.fromDate))),
    to: formatISO(endOfDay(new UTCDate(state.toDate))),
    limit: state.limit,
    offset: state.offset,
  });

  return (
    <div>
      <Hero>Review History</Hero>
      <Controls>
        <div className="flex gap-3 p-4">
          <div className="grid gap-1">
            <label className="text-sm font-semibold" htmlFor="fromDate">
              From Date
            </label>
            <InputDebounced
              id="fromDate"
              type="date"
              value={state.fromDate}
              onChange={(value) =>
                updateSearchParams({
                  fromDate: value,
                  offset: 1,
                })
              }
            />
          </div>
          <div className="grid gap-1">
            <label className="text-sm font-semibold" htmlFor="toDate">
              To Date
            </label>
            <InputDebounced
              id="toDate"
              type="date"
              value={state.toDate}
              onChange={(value) =>
                updateSearchParams({
                  toDate: value,
                  offset: 1,
                })
              }
            />
          </div>
        </div>
      </Controls>
      {historyQuery.isPending ? (
        <LoadingIndicator />
      ) : (
        <Results
          data={historyQuery.data}
          state={state}
          updateSearchParams={updateSearchParams}
        />
      )}
    </div>
  );
}

function Results({ data, state, updateSearchParams }) {
  const [selectedReview, setSelectedReview] = React.useState(null);
  const appReviews = data?.result ?? [];
  const total = data.total;

  const appIdToReview = new Map();
  appReviews.forEach((review, index) =>
    appIdToReview.set(review.application.id, review),
  );

  const apps = appReviews.map((review) => review.application);

  const handleRowClick = (app) => {
    setSelectedReview(appIdToReview.get(app.id));
  };

  const handleRowKeyDown = (event, app) => {
    if (event.key === "Enter" || event.key === " ") {
      event.preventDefault();
      setSelectedReview(appIdToReview.get(app.id));
    }
  };

  const handleDialogChange = (open) => {
    if (!open) {
      setSelectedReview(null);
    }
  };

  if (!appReviews || appReviews.length === 0) {
    return <NoMatchesFound className="mt-6" />;
  }

  return (
    <>
      <Card className="mt-6">
        <div className="p-3">
          <TravelAppSummaryTable
            apps={apps}
            handleRowClick={handleRowClick}
            handleRowKeyDown={handleRowKeyDown}
          />
          <Pagination
            limit={state.limit}
            offset={state.offset}
            total={total}
            onPageChange={(offset) => updateSearchParams({ offset: offset })}
          />
        </div>
      </Card>

      {selectedReview && (
        <TravelAppReviewModal
          reviewSummary={selectedReview}
          onOpenChange={handleDialogChange}
        />
      )}
    </>
  );
}

function TravelAppReviewModal({ reviewSummary, onOpenChange }) {
  const { data, isPending } = useTravelReview(reviewSummary?.appReviewId);
  const review = data?.result;

  if (isPending || !review) {
    return (
      <Modal
        isOpen={Boolean(reviewSummary)}
        onSoftReject={() => onOpenChange(false)}
      >
        <Modal.Body>
          <LoadingIndicator />
        </Modal.Body>
      </Modal>
    );
  }

  const pdfHref = `${window.location.origin}/api/v1/travel/applications/${review?.travelApplication.id}.pdf`;

  return (
    <Modal
      isOpen={Boolean(reviewSummary)}
      onSoftReject={() => onOpenChange(false)}
    >
      <Modal.Body>
        <TravelAppReviewForm appReview={review} />
      </Modal.Body>
      <Modal.Controls>
        <div className="flex items-center gap-6 px-3 py-1.5">
          <a href={pdfHref} target="_blank" rel="noopener noreferrer">
            Print
          </a>
          <Button
            color="secondary"
            className="w-20"
            onClick={() => onOpenChange(false)}
          >
            Close
          </Button>
        </div>
      </Modal.Controls>
    </Modal>
  );
}
