import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import {
  useDrafts,
  useMutateDraft,
} from "app/views/travel/application/drafts/useDrafts";
import LoadingIndicator from "app/components/LoadingIndicator";
import Modal from "app/components/Modal";
import { isoToShortDate, isoToShortDateTime } from "app/utils/dateUtils";
import { FilePenLine, LoaderCircle, Trash2 } from "lucide-react";
import { toCurrency } from "app/utils/textUtils";
import Card from "app/components/Card";
import Button from "app/components/Button";
import { cn } from "app/utils/cn";

export default function Drafts() {
  const { data, isPending, isFetching } = useDrafts();

  return (
    <div>
      <Hero>Travel Application Drafts</Hero>
      <Controls className="flex justify-center px-4 py-3">
        Resume or remove your saved travel applications.
      </Controls>

      {isPending ? (
        <div className="mt-6">
          <LoadingIndicator />
        </div>
      ) : (
        <DraftResults
          drafts={data?.result}
          isUpdating={isFetching && !isPending}
        />
      )}
    </div>
  );
}

function DraftResults({ drafts, isUpdating }) {
  const navigate = useNavigate();
  const rows = Array.isArray(drafts) ? drafts : [];

  if (!rows.length) {
    return (
      <Card className="mt-6">
        <div className="flex flex-col items-center px-4 py-10 text-center">
          <FilePenLine
            aria-hidden="true"
            className="mb-3 h-10 w-10 text-gray-400"
          />
          <h2 className="text-xl font-semibold">No saved drafts</h2>
          <p className="mt-1 text-gray-600">
            Applications you save before submitting will appear here.
          </p>
          <Button
            variant="theme"
            className="mt-4"
            onPress={() => navigate("/travel/applications/new")}
          >
            Start a travel application
          </Button>
        </div>
      </Card>
    );
  }

  return <DraftTable drafts={rows} isUpdating={isUpdating} />;
}

function DraftTable({ drafts, isUpdating }) {
  const navigate = useNavigate();
  const rows = Array.isArray(drafts) ? drafts : [];
  const deleteDraft = useMutateDraft();
  const [draftToDelete, setDraftToDelete] = useState(null);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  const closeDeleteModal = () => {
    setIsDeleteModalOpen(false);
    setDraftToDelete(null);
    deleteDraft.reset();
  };

  const openDeleteModal = (draft) => {
    deleteDraft.reset();
    setDraftToDelete(draft);
    setIsDeleteModalOpen(true);
  };

  const handleDeleteModalChange = (isOpen) => {
    if (!isOpen && !deleteDraft.isPending) {
      closeDeleteModal();
    }
  };

  const handleContinue = (draft) => {
    navigate(`/travel/applications/new/${draft.id}`);
  };

  const handleDelete = (draft) => {
    if (!draft) {
      return;
    }
    deleteDraft.mutate(draft.id, {
      onSuccess: closeDeleteModal,
    });
  };

  return (
    <>
      <Card className="mt-6">
        <div className="p-4">
          <DraftResultsHeader count={rows.length} isUpdating={isUpdating} />
          <div
            aria-busy={isUpdating}
            className={cn("transition-opacity", isUpdating && "opacity-60")}
          >
            <table className="table table-fixed">
              <colgroup>
                <col className="w-24" />
                <col className="w-28" />
                <col className="w-44" />
                <col />
                <col className="w-24" />
                <col className="w-36" />
              </colgroup>
              <thead>
                <tr className="table__head__row">
                  <th className="table__head__cell">Last Updated</th>
                  <th className="table__head__cell">Travel Date</th>
                  <th className="table__head__cell">Traveler</th>
                  <th className="table__head__cell">Destination</th>
                  <th className="table__head__cell cell--number">
                    Allotted Funds
                  </th>
                  <th className="table__head__cell">
                    <span className="sr-only">Actions</span>
                  </th>
                </tr>
              </thead>
              <tbody className="table__body table__body--striped">
                {rows.map((row) => (
                  <tr key={row.id} className="table__row">
                    <td className="table__cell">
                      {displayValue(isoToShortDateTime(row.updatedDateTime))}
                    </td>
                    <td className="table__cell">
                      {displayValue(isoToShortDate(row.amendment?.startDate))}
                    </td>
                    <td className="table__cell">
                      {displayValue(row.traveler?.fullName)}
                    </td>
                    <td className="table__cell">
                      {displayValue(row.amendment?.destinationSummary)}
                    </td>
                    <td className="table__cell cell--number">
                      {displayValue(toCurrency(row.amendment?.totalAllowance))}
                    </td>
                    <td className="table__cell">
                      <div className="flex justify-end gap-2">
                        <Button
                          variant="theme"
                          onPress={() => handleContinue(row)}
                          isDisabled={deleteDraft.isPending}
                        >
                          Continue
                        </Button>
                        <Button
                          variant="destructive"
                          aria-label={getDeleteDraftLabel(row)}
                          className="h-8 w-8 p-0"
                          onPress={() => openDeleteModal(row)}
                          isDisabled={deleteDraft.isPending}
                        >
                          <Trash2 aria-hidden="true" className="h-4 w-4" />
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </Card>
      <Modal
        isOpen={isDeleteModalOpen}
        onOpenChange={handleDeleteModalChange}
        isDismissable={!deleteDraft.isPending}
      >
        <Modal.Title>Delete this draft?</Modal.Title>
        <Modal.Body>
          <div className="max-w-lg space-y-3">
            <p>
              The selected travel application draft will be permanently deleted.
            </p>
            {draftToDelete && (
              <dl className="grid grid-cols-[auto_1fr] gap-x-3 gap-y-1 text-gray-600">
                <dt className="font-medium">Travel date</dt>
                <dd>
                  {displayValue(
                    isoToShortDate(draftToDelete.amendment?.startDate),
                  )}
                </dd>
                <dt className="font-medium">Traveler</dt>
                <dd>{displayValue(draftToDelete.traveler?.fullName)}</dd>
                <dt className="font-medium">Destination</dt>
                <dd>
                  {displayValue(draftToDelete.amendment?.destinationSummary)}
                </dd>
              </dl>
            )}
            {deleteDraft.isError && (
              <p role="alert" className="text-sm font-medium text-red-700">
                We couldn&apos;t delete this draft. Please try again.
              </p>
            )}
          </div>
        </Modal.Body>
        <Modal.Buttons>
          <Button
            variant="destructive"
            onPress={() => handleDelete(draftToDelete)}
            isDisabled={!draftToDelete}
            isPending={deleteDraft.isPending}
          >
            Delete draft
          </Button>
          <Button
            variant="secondary"
            onPress={closeDeleteModal}
            isDisabled={deleteDraft.isPending}
          >
            Cancel
          </Button>
        </Modal.Buttons>
      </Modal>
    </>
  );
}

function DraftResultsHeader({ count, isUpdating }) {
  return (
    <div className="mb-3 flex min-h-9 items-center gap-3 border-b border-gray-200 pb-3">
      <div aria-live="polite" className="flex items-center gap-3">
        <span className="font-semibold">
          {count} saved {count === 1 ? "draft" : "drafts"}
        </span>
        {isUpdating && (
          <span className="inline-flex items-center gap-1.5 text-sm text-gray-500">
            <LoaderCircle aria-hidden="true" className="h-4 w-4 animate-spin" />
            Refreshing
          </span>
        )}
      </div>
    </div>
  );
}

function displayValue(value) {
  return value === null || value === undefined || value === "" ? "—" : value;
}

function getDeleteDraftLabel(draft) {
  const traveler = draft.traveler?.fullName?.trim();
  const travelDate = isoToShortDate(draft.amendment?.startDate);

  if (traveler && travelDate) {
    return `Delete ${traveler}'s travel draft for ${travelDate}`;
  }
  if (traveler) {
    return `Delete ${traveler}'s travel draft`;
  }
  if (travelDate) {
    return `Delete travel draft for ${travelDate}`;
  }

  return "Delete travel draft";
}
