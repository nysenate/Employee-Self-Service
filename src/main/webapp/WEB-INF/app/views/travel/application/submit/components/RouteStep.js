import React from "react";
import { Plus, Trash2 } from "lucide-react";
import Button from "app/components/Button";
import Card from "app/components/Card";
import { useModesOfTransportation } from "app/views/travel/shared/hooks/useModesOfTransportation";
import CountyPromptModal from "./CountyPromptModal";
import FormErrorSummary from "./FormErrorSummary";
import RouteSegmentFields from "./RouteSegmentFields";

export default function RouteStep({
  title,
  description,
  legs,
  errors,
  errorSummaryRef,
  segmentIdPrefix,
  addSegmentLabel,
  onAddSegment,
  onRemoveLastSegment,
  onUpdateSegment,
  onDestinationSelect,
  firstLegQualifier,
  onCountySubmit,
  pendingCounty,
  onCountyCancel,
  actions,
}) {
  const { data: modes = [], isError: modesFailed } = useModesOfTransportation();

  return (
    <Card>
      <Card.Content className="space-y-6 p-5 sm:p-6">
        <div className="border-l-4 border-teal-600 pl-4">
          <h1 className="text-2xl font-semibold text-teal-900">{title}</h1>
          <p className="mt-1 text-sm text-gray-600">{description}</p>
        </div>
        <FormErrorSummary
          ref={errorSummaryRef}
          errors={errors}
          fieldIdPrefix=""
        />
        {modesFailed && (
          <p role="alert" className="font-medium text-red-700">
            Modes of transportation could not be loaded. Please try again.
          </p>
        )}
        <div className="space-y-5">
          {legs.map((leg, index) => (
            <section
              key={index}
              aria-labelledby={`${segmentIdPrefix}-segment-${index}`}
              className="border border-gray-200 p-4 sm:p-5"
            >
              <div className="mb-4 flex items-center justify-between">
                <h2
                  id={`${segmentIdPrefix}-segment-${index}`}
                  className="text-lg font-semibold"
                >
                  Segment {index + 1}
                </h2>
                {index === legs.length - 1 && index > 0 && (
                  <Button
                    variant="quiet"
                    aria-label={`Remove segment ${index + 1}`}
                    onPress={onRemoveLastSegment}
                  >
                    <Trash2 aria-hidden="true" className="h-4 w-4" /> Remove
                  </Button>
                )}
              </div>
              <RouteSegmentFields
                index={index}
                leg={leg}
                errors={errors}
                modes={modes}
                onChange={(changes) => onUpdateSegment(index, changes)}
                onDestinationSelect={onDestinationSelect}
              />
              {index === 0 && firstLegQualifier && (
                <label className="mt-4 flex items-center gap-2">
                  <input
                    type="checkbox"
                    checked={firstLegQualifier.checked}
                    onChange={(event) =>
                      firstLegQualifier.onChange(event.target.checked)
                    }
                  />
                  {firstLegQualifier.label}
                </label>
              )}
            </section>
          ))}
        </div>
        <div className="text-center">
          <Button variant="secondary" onPress={onAddSegment}>
            <Plus aria-hidden="true" className="h-4 w-4" /> {addSegmentLabel}
          </Button>
        </div>
      </Card.Content>
      <Card.Footer className="mt-0 justify-end bg-gray-50 px-5 py-4 sm:px-6">
        {actions}
      </Card.Footer>
      <CountyPromptModal
        pending={pendingCounty}
        onSubmit={onCountySubmit}
        onCancel={onCountyCancel}
      />
    </Card>
  );
}
