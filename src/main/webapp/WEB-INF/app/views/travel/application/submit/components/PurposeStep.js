import React, { forwardRef } from "react";
import { Upload, X } from "lucide-react";
import Button from "app/components/Button";
import Card from "app/components/Card";
import { useTravelEventTypes } from "app/views/travel/shared/hooks/useTravelEventTypes";
import FormErrorSummary from "./FormErrorSummary";

const MAX_FILE_SIZE = 10 * 1024 * 1024;

const PurposeStep = forwardRef(function PurposeStep(
  {
    draft,
    errors,
    errorSummaryRef,
    uploadError,
    isUploading,
    onDraftChange,
    onUpload,
    actions,
  },
  ref,
) {
  const { data: eventTypes = [], isError: eventTypesFailed } =
    useTravelEventTypes();
  const purpose = draft.amendment?.purposeOfTravel ?? {};
  const attachments = draft.amendment?.attachments ?? [];
  const traveler = draft.traveler;
  const departmentHead = traveler?.department?.head;

  function updatePurpose(changes) {
    onDraftChange({
      ...draft,
      amendment: {
        ...draft.amendment,
        purposeOfTravel: { ...purpose, ...changes },
      },
    });
  }

  function updateAttachments(nextAttachments) {
    onDraftChange({
      ...draft,
      amendment: { ...draft.amendment, attachments: nextAttachments },
    });
  }

  return (
    <Card ref={ref} className="mx-auto max-w-5xl">
      <Card.Content className="space-y-6 p-5 sm:p-6">
        <div className="border-l-4 border-teal-600 pl-4">
          <h1 className="text-2xl font-semibold text-teal-900">Purpose</h1>
          <p className="mt-1 text-sm text-gray-600">
            Review the traveler information and provide details about the trip.
          </p>
        </div>

        <FormErrorSummary ref={errorSummaryRef} errors={errors} />

        <section
          aria-labelledby="traveler-heading"
          className="bg-teal-50 p-4 sm:p-5"
        >
          <h2 id="traveler-heading" className="mb-3 text-lg font-semibold">
            Traveler Information
          </h2>
          <dl className="grid gap-x-8 gap-y-4 sm:grid-cols-2">
            <ReadOnlyValue label="Traveler" value={traveler?.fullName} />
            <ReadOnlyValue label="Job title" value={traveler?.jobTitle} />
            <ReadOnlyValue
              label="Work address"
              value={
                traveler?.empWorkLocation?.address?.formattedAddressWithCounty
              }
            />
            {!traveler?.isDepartmentHead && departmentHead && (
              <ReadOnlyValue
                label="Department head"
                value={departmentHead.fullName}
              />
            )}
          </dl>
        </section>

        <section
          aria-labelledby="purpose-heading"
          className="border-t border-gray-200 pt-6"
        >
          <h2 id="purpose-heading" className="mb-3 text-lg font-semibold">
            Purpose of Travel
          </h2>
          {eventTypesFailed && (
            <p role="alert" className="mb-3 text-sm font-medium text-red-700">
              Purposes of travel could not be loaded. Please try again.
            </p>
          )}
          <label htmlFor="purpose-eventType" className="mb-1 block font-medium">
            Purpose
          </label>
          <select
            id="purpose-eventType"
            className={`select max-w-lg ${errors.eventType ? "input--invalid" : ""}`}
            aria-invalid={Boolean(errors.eventType)}
            aria-describedby={
              errors.eventType ? "purpose-eventType-error" : undefined
            }
            value={purpose.eventType?.name ?? ""}
            onChange={(event) => {
              const eventType = eventTypes.find(
                ({ name }) => name === event.target.value,
              );
              updatePurpose({
                eventType,
                eventName: "",
                additionalPurpose: "",
              });
            }}
          >
            <option value="" disabled hidden>
              Select a purpose
            </option>
            {eventTypes.map((eventType) => (
              <option key={eventType.name} value={eventType.name}>
                {eventType.displayName}
              </option>
            ))}
          </select>
          <FieldError id="purpose-eventType-error" message={errors.eventType} />

          {purpose.eventType?.requiresName && (
            <div className="mt-4">
              <label
                htmlFor="purpose-eventName"
                className="mb-1 block font-medium"
              >
                Name of the {purpose.eventType.displayName}
              </label>
              <input
                id="purpose-eventName"
                className={`input w-full max-w-sm ${errors.eventName ? "input--invalid" : ""}`}
                aria-invalid={Boolean(errors.eventName)}
                aria-describedby={
                  errors.eventName ? "purpose-eventName-error" : undefined
                }
                value={purpose.eventName ?? ""}
                onChange={(event) =>
                  updatePurpose({ eventName: event.target.value })
                }
              />
              <FieldError
                id="purpose-eventName-error"
                message={errors.eventName}
              />
            </div>
          )}

          {purpose.eventType && (
            <div className="mt-4">
              <label
                htmlFor="purpose-additionalPurpose"
                className="mb-1 block font-medium"
              >
                {purpose.eventType.requiresAdditionalPurpose
                  ? "Description of purpose"
                  : "Additional information (optional)"}
              </label>
              <textarea
                id="purpose-additionalPurpose"
                rows="3"
                className={`input w-full max-w-sm resize-y ${errors.additionalPurpose ? "input--invalid" : ""}`}
                aria-invalid={Boolean(errors.additionalPurpose)}
                aria-describedby={
                  errors.additionalPurpose
                    ? "purpose-additionalPurpose-error"
                    : undefined
                }
                value={purpose.additionalPurpose ?? ""}
                onChange={(event) =>
                  updatePurpose({ additionalPurpose: event.target.value })
                }
              />
              <FieldError
                id="purpose-additionalPurpose-error"
                message={errors.additionalPurpose}
              />
            </div>
          )}
        </section>

        <section
          aria-labelledby="documents-heading"
          className="border-t border-gray-200 pt-6"
        >
          <h2 id="documents-heading" className="text-lg font-semibold">
            Supporting Documents{" "}
            <span className="text-sm font-normal text-gray-600">
              (optional)
            </span>
          </h2>
          <p className="mt-1 text-sm text-gray-600">
            Attach relevant documents. Each file must be no larger than 10 MB.
          </p>
          {uploadError && (
            <p role="alert" className="mt-3 font-medium text-red-700">
              The upload failed. Each document must be no larger than 10 MB.
              Please try again.
            </p>
          )}
          {attachments.length > 0 && (
            <ul className="mt-4 divide-y rounded border">
              {attachments.map((attachment) => (
                <li
                  key={attachment.filename}
                  className="flex items-center justify-between gap-3 px-3 py-2"
                >
                  <span className="min-w-0 truncate">
                    {attachment.originalName}
                  </span>
                  <Button
                    variant="quiet"
                    aria-label={`Remove ${attachment.originalName}`}
                    onPress={() =>
                      updateAttachments(
                        attachments.filter(
                          ({ filename }) => filename !== attachment.filename,
                        ),
                      )
                    }
                  >
                    <X aria-hidden="true" className="h-4 w-4" />
                  </Button>
                </li>
              ))}
            </ul>
          )}
          <label
            className={`mt-4 inline-flex cursor-pointer items-center gap-2 border border-gray-600 px-2.5 py-1 font-semibold text-gray-600 ${isUploading ? "pointer-events-none opacity-50" : ""}`}
          >
            <Upload aria-hidden="true" className="h-4 w-4" />
            {isUploading ? "Uploading…" : "Attach documents"}
            <input
              type="file"
              multiple
              className="sr-only"
              disabled={isUploading}
              onChange={(event) => {
                const files = Array.from(event.target.files ?? []);
                onUpload(
                  files,
                  files.some(({ size }) => size > MAX_FILE_SIZE),
                );
                event.target.value = "";
              }}
            />
          </label>
        </section>
      </Card.Content>
      <Card.Footer className="mt-0 justify-end bg-gray-50 px-5 py-4 sm:px-6">
        {actions}
      </Card.Footer>
    </Card>
  );
});

export default PurposeStep;

function ReadOnlyValue({ label, value }) {
  return (
    <div>
      <dt className="text-sm font-semibold text-gray-700">{label}</dt>
      <dd className="mt-1">{value?.trim() || "N/A"}</dd>
    </div>
  );
}

function FieldError({ id, message }) {
  return message ? (
    <p id={id} className="mt-1 text-sm font-medium text-red-700">
      {message}
    </p>
  ) : null;
}
