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
import { Trash2 } from "lucide-react";
import { toCurrency } from "app/utils/textUtils";
import Card from "app/components/Card";
import Button from "app/components/Button";

export default function Drafts() {
  const { data, isPending } = useDrafts();
  return (
    <div>
      <Hero>Travel Application Drafts</Hero>
      <Controls className="flex justify-center p-3">
        Continue work on a saved draft.
      </Controls>

      {isPending ? <LoadingIndicator /> : <DraftTable drafts={data.result} />}
    </div>
  );
}

function DraftTable({ drafts }) {
  const navigate = useNavigate();
  let rows = Array.isArray(drafts) ? drafts : [];
  const deleteDraft = useMutateDraft();
  const [draftToDelete, setDraftToDelete] = useState(null);

  const handleContinue = (draft) => {
    navigate(`/travel/applications/new/${draft.id}`);
  };

  const handleDelete = (draft) => {
    if (!draft) {
      return;
    }
    deleteDraft.mutate(draft.id);
    setDraftToDelete(null);
  };

  return (
    <>
      <Card className="mt-6">
        <div className="p-4">
          <table className="table">
            <thead>
              <tr className="table__head__row">
                <th className="table__head__cell">Travel Date</th>
                <th className="table__head__cell">Traveler</th>
                <th className="table__head__cell">Destination</th>
                <th className="table__head__cell cell--number">
                  Allotted Funds
                </th>
                <th className="table__head__cell">Updated Date Time</th>
                <th className="table__head__cell"></th>
              </tr>
            </thead>
            <tbody className="table__body table__body--striped">
              {rows.map((row) => (
                <tr key={row.id} className="table__row">
                  <td className="table__cell">
                    {isoToShortDate(row.amendment?.startDate)}
                  </td>
                  <td className="table__cell">{row.traveler?.fullName}</td>
                  <td className="table__cell">
                    {row.amendment?.destinationSummary}
                  </td>
                  <td className="table__cell cell--number">
                    {toCurrency(row.amendment?.totalAllowance)}
                  </td>
                  <td className="table__cell">
                    {isoToShortDateTime(row.updatedDateTime)}
                  </td>
                  <td className="table__cell">
                    <div className="flex justify-end gap-2">
                      <Button
                        variant="secondary"
                        size="sm"
                        onPress={() => handleContinue(row)}
                        isDisabled={deleteDraft.isPending}
                      >
                        Continue
                      </Button>
                      <Button
                        variant="secondary"
                        size="icon-sm"
                        aria-label="Delete draft"
                        onPress={() => setDraftToDelete(row)}
                        isDisabled={deleteDraft.isPending}
                      >
                        <Trash2 />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
      <Modal
        isOpen={Boolean(draftToDelete)}
        onSoftReject={() => setDraftToDelete(null)}
      >
        <Modal.Title>Delete draft</Modal.Title>
        <Modal.Body>
          <div className="space-y-2">
            <p>This action cannot be undone.</p>
            {draftToDelete && (
              <div className="text-muted-foreground">
                Travel Date:{" "}
                {isoToShortDate(draftToDelete.amendment?.startDate)}
                <br />
                Traveler: {draftToDelete.traveler?.fullName}
                <br />
                {draftToDelete.amendment?.destinationSummary && (
                  <>
                    Destination: {draftToDelete.amendment?.destinationSummary}
                  </>
                )}
              </div>
            )}
          </div>
        </Modal.Body>
        <Modal.Buttons>
          <Button
            variant="destructive"
            onPress={() => handleDelete(draftToDelete)}
            isDisabled={deleteDraft.isPending || !draftToDelete}
          >
            Delete
          </Button>
          <Button
            variant="secondary"
            onPress={() => setDraftToDelete(null)}
            isDisabled={deleteDraft.isPending}
          >
            Cancel
          </Button>
        </Modal.Buttons>
      </Modal>
    </>
  );
}
