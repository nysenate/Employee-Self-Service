import React, { useMemo } from "react";
import LoadingCircle from "app/components/LoadingCircle";
import { isoToShortDate } from "app/utils/dateUtils";
import { toCurrency } from "app/utils/textUtils";
import { cn } from "app/utils/cn";
import { Info, Paperclip } from "lucide-react";
import LoadingIndicator from "app/components/LoadingIndicator";
import TransportationDetailsPopover from "app/views/travel/shared/components/TransportationDetailsPopover";
import MealDetailsPopover from "app/views/travel/shared/components/MealDetailsPopover";
import LodgingDetailsPopover from "app/views/travel/shared/components/LodgingDetailsPopover";
import TravelAppStatusBadge from "app/views/travel/shared/components/TravelAppStatusBadge";
import { useModesOfTransportation } from "app/views/travel/shared/hooks/useModesOfTransportation";

const NOT_AVAILABLE = "N/A";

export default function TravelAppForm({ app, showStatus = false, className }) {
  const amendment = app?.activeAmendment ?? {};
  return (
    <div className={cn("mx-auto p-3", className)}>
      <div className="mx-auto w-[816px]">
        <Header />
        <EmployeeInfo app={app} />
        <div className="my-3 h-1 bg-gray-500" />
        <AppInfo amendment={amendment} />
        <div className="mt-3 grid grid-cols-2 place-items-center">
          <ModeOfTransportationBox amendment={amendment} />
          <AllowancesBox amendment={amendment} />
        </div>
        <div className="my-3 h-1 bg-gray-500" />
        <Attachments amendment={amendment} />
        {showStatus && <Status status={app.status} />}
      </div>
    </div>
  );
}

function Header() {
  return (
    <div className="mb-6 space-y-6">
      <header className="space-y-1 text-center">
        <h2 className="text-2xl font-semibold tracking-wide">
          NEW YORK STATE SENATE
        </h2>
        <h3 className="text-base font-semibold">Secretary of the Senate</h3>
        <h2 className="text-3xl">APPLICATION FOR TRAVEL APPROVAL</h2>
        <h3 className="text-lg font-semibold">
          Prior Approval for all travel must be obtained from the Secretary of
          the Senate
        </h3>
      </header>
    </div>
  );
}

function EmployeeInfo({ app }) {
  const traveler = app?.traveler ?? {};
  return (
    <div className="grid grid-cols-[125px_380px_125px_150px] gap-x-3 gap-y-1">
      <LabelText>Date:</LabelText>
      <div>{formatDate(app.submittedDateTime)}</div>
      <LabelText>NYS EMPLID#:</LabelText>
      <div>{traveler.nid}</div>
      <LabelText>Name/Title:</LabelText>
      <div>
        {formatValue(traveler.fullName)} - {formatValue(traveler.jobTitle)}
      </div>
      <LabelText>Phone:</LabelText>
      <div>{formatValue(traveler.workPhone)}</div>
      <LabelText>Office:</LabelText>
      <div>{formatValue(traveler.respCtr.respCenterHead.name)}</div>
      <LabelText>Agency Code:</LabelText>
      <div>{formatValue(traveler.respCtr.agencyCode)}</div>
      <LabelText>Office Address:</LabelText>
      <div className="col-span-3">
        {formatValue(
          traveler.empWorkLocation.address.formattedAddressWithCounty,
        )}
      </div>
    </div>
  );
}

function AppInfo({ amendment }) {
  return (
    <div className="grid grid-cols-[125px_1fr] gap-x-3 gap-y-1">
      <LabelText>Departure:</LabelText>
      <div>
        {formatValue(amendment.route.origin.formattedAddressWithCounty)}
      </div>
      {amendment.route.destinations.map((dest, index) => (
        <React.Fragment key={dest.id}>
          {index === 0 ? (
            <LabelText>Destination:</LabelText>
          ) : (
            <LabelText>&nbsp;</LabelText>
          )}
          <div>{formatValue(dest.address.formattedAddressWithCounty)}</div>
        </React.Fragment>
      ))}
      <LabelText>Dates of Travel:</LabelText>
      <div>
        {formatDate(amendment.startDate)} to {formatDate(amendment.endDate)}
      </div>
      <LabelText>Purpose:</LabelText>
      <div>
        {formatValue(amendment.purposeOfTravel.summary)}
        {amendment.purposeOfTravel.additionalPurpose && (
          <>
            <br /> {amendment.purposeOfTravel.additionalPurpose}
          </>
        )}
      </div>
    </div>
  );
}

function Attachments({ amendment }) {
  const attachments = amendment.attachments ?? [];
  if (attachments.length === 0) {
    return null;
  }

  return (
    <div className="grid grid-cols-[125px_1fr] gap-x-3 gap-y-1">
      {attachments.map((attachment, index) => (
        <React.Fragment key={attachment.filename}>
          {index === 0 ? (
            <LabelText>Attachments:</LabelText>
          ) : (
            <LabelText>&nbsp;</LabelText>
          )}
          <div>
            <a
              href={`/api/v1/travel/applications/attachment/${attachment.filename}`}
              target="_blank"
              rel="noreferrer"
              className="inline-flex items-center gap-2 text-teal-700 underline-offset-2 hover:underline"
            >
              <Paperclip className="h-4 w-4" />
              <span>{attachment.originalName}</span>
            </a>
          </div>
        </React.Fragment>
      ))}
    </div>
  );
}

function Status({ status }) {
  if (!status) {
    return null;
  }

  return (
    <div className="grid grid-cols-[125px_1fr] gap-x-3 gap-y-1">
      <LabelText>Status:</LabelText>
      <div>
        <TravelAppStatusBadge status={status} />
      </div>
      {status.isDisapproved && (
        <>
          <LabelText>Reason:</LabelText>
          <div className="whitespace-pre-wrap">{status.note}</div>
        </>
      )}
    </div>
  );
}

function LabelText({ children }) {
  return <div className="font-semibold">{children}</div>;
}

function ModeOfTransportationBox({ amendment }) {
  const { data: modesOfTransportation = [], isLoading: isMotLoading } =
    useModesOfTransportation();

  const selectedModes = useMemo(
    () =>
      new Set(
        amendment.route?.outboundLegs
          ?.map((leg) => leg?.methodOfTravelDisplayName)
          .filter(Boolean),
      ),
    [amendment.route],
  );

  return (
    <div className="min-h-32 w-56 border-1 p-3">
      <div className="flex justify-center font-semibold">
        Mode of Transportation
      </div>
      {isMotLoading ? (
        <div className="mx-auto my-3">
          <LoadingCircle textColor="text-teal-600" />
        </div>
      ) : (
        <div className="mt-3 grid grid-cols-2 gap-0.5">
          {modesOfTransportation.map((mot) => (
            <React.Fragment key={mot.methodOfTravel}>
              <div className="text-muted-foreground">{mot.displayName}</div>
              <div className="text-right">
                <input
                  type="checkbox"
                  checked={selectedModes.has(mot.displayName)}
                  aria-label={mot.displayName}
                  readOnly
                />
              </div>
            </React.Fragment>
          ))}
        </div>
      )}
    </div>
  );
}

function AllowancesBox({ amendment }) {
  const labelClasses = "text-muted-foreground";
  const valueClasses = "text-right font-mono tabular-nums text-base";

  return (
    <div className="min-h-32 w-80 border-1 p-3">
      <div className="flex justify-center font-semibold">
        Estimated Travel Costs
      </div>
      <div className="mt-3 grid grid-cols-[1fr_78px_16px] items-center gap-0.5">
        <div className={labelClasses}>
          Transportation ({amendment.mileagePerDiems.totalMileage}) Miles
        </div>
        <div className={valueClasses}>
          {formatCurrency(amendment.transportationAllowance)}
        </div>
        <TransportationDetailsPopover amendment={amendment} />

        <div className={labelClasses}>Food</div>
        <div className={valueClasses}>
          {formatCurrency(amendment.mealAllowance)}
        </div>
        <MealDetailsPopover amendment={amendment} />

        <div className={labelClasses}>Lodging</div>
        <div className={valueClasses}>
          {formatCurrency(amendment.lodgingAllowance)}
        </div>
        <LodgingDetailsPopover amendment={amendment} />

        <div className={labelClasses}>Parking/Tolls</div>
        <div className={valueClasses}>
          {formatCurrency(amendment.tollsAndParkingAllowance)}
        </div>
        <div>&nbsp;</div>

        <div className={labelClasses}>Taxi/Bus/Subway</div>
        <div className={valueClasses}>
          {formatCurrency(amendment.alternateTransportationAllowance)}
        </div>
        <div>&nbsp;</div>

        <div className={labelClasses}>Registration Fee</div>
        <div className={valueClasses}>
          {formatCurrency(amendment.registrationAllowance)}
        </div>
        <div>&nbsp;</div>

        <div className={labelClasses}>TOTAL</div>
        <div className={valueClasses}>
          {formatCurrency(amendment.totalAllowance)}
        </div>
        <div>&nbsp;</div>
      </div>
    </div>
  );
}

function formatDate(value) {
  const formatted = isoToShortDate(value);
  return formatted || NOT_AVAILABLE;
}

function formatCurrency(value) {
  const formatted = toCurrency(value);
  return formatted || NOT_AVAILABLE;
}

function formatValue(value) {
  if (value === null || value === undefined) return NOT_AVAILABLE;
  if (typeof value === "string" && value.trim() === "") return NOT_AVAILABLE;
  return value;
}
