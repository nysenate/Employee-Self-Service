import React from "react";
import { useNavigate } from "react-router-dom";
import Hero from "app/components/Hero";
import Controls from "app/components/Controls";
import {
  useDrafts,
  useMutateDraft,
} from "app/views/travel/application/drafts/useDrafts";
import LoadingIndicator from "app/components/LoadingIndicator";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "app/components/ui/alert-dialog";
import {
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
  Table,
} from "app/components/ui/table";
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

  const handleContinue = (draft) => {
    navigate(`/travel/applications/new/${draft.id}`);
  };

  const handleDelete = (draft) => {
    deleteDraft.mutate(draft.id);
  };

  return (
    <Card className="mt-6">
      <div className="p-4">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Travel Date</TableHead>
              <TableHead>Traveler</TableHead>
              <TableHead>Destination</TableHead>
              <TableHead>Allotted Funds</TableHead>
              <TableHead>Updated Date Time</TableHead>
              <TableHead></TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {rows.map((row) => (
              <TableRow key={row.id} className="hover:bg-transparent">
                <TableCell>
                  {isoToShortDate(row.amendment?.startDate)}
                </TableCell>
                <TableCell>{row.traveler?.fullName}</TableCell>
                <TableCell>{row.amendment?.destinationSummary}</TableCell>
                <TableCell numeric>
                  {toCurrency(row.amendment?.totalAllowance)}
                </TableCell>
                <TableCell>{isoToShortDateTime(row.updatedDateTime)}</TableCell>
                <TableCell className="flex justify-end gap-2">
                  <Button
                    variant="outlined"
                    size="sm"
                    onClick={() => handleContinue(row)}
                    disabled={deleteDraft.isPending}
                  >
                    Continue
                  </Button>
                  <AlertDialog>
                    <AlertDialogTrigger asChild>
                      <Button
                        variant="outlined"
                        size="icon-sm"
                        aria-label="Delete draft"
                        disabled={deleteDraft.isPending}
                      >
                        <Trash2 />
                      </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>Delete draft</AlertDialogTitle>
                        <AlertDialogDescription>
                          This action cannot be undone.
                        </AlertDialogDescription>
                        <div className="text-muted-foreground">
                          Travel Date:{" "}
                          {isoToShortDate(row.amendment?.startDate)}
                          <br />
                          Traveler: {row.traveler?.fullName}
                          <br />
                          {row.amendment?.destinationSummary && (
                            <>
                              Destination: {row.amendment?.destinationSummary}
                            </>
                          )}
                        </div>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogAction
                          variant="destructive"
                          onClick={() => handleDelete(row)}
                          disabled={deleteDraft.isPending}
                        >
                          Delete
                        </AlertDialogAction>
                        <AlertDialogCancel disabled={deleteDraft.isPending}>
                          Cancel
                        </AlertDialogCancel>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </Card>
  );
}
