import React, { useState } from "react";
import Hero from "app/components/Hero";
import Button from "app/components/Button";
import Modal from "app/components/Modal";
import useRequireAuthedUser from "app/hooks/useRequireAuthedUser";
import {
  useDonationHistory,
  useDonationInfo,
  useSubmitDonation,
} from "app/views/time/accrual/useDonation";
import styles from "app/views/time/accrual/accrual.module.css";

/** Donations have only been recorded in ESS since 2023. */
const FIRST_DONATION_YEAR = 2023;

/**
 * Lets an employee donate accrued sick time, and lists what they have already donated.
 * Ported from the legacy DonationCtrl
 * (assets/js/src/time/accrual/sick-time-donation-ctrl.js).
 */
export default function SickLeaveDonationIndex() {
  const { data: user } = useRequireAuthedUser();
  const empId = user?.employeeId;

  const donationInfo = useDonationInfo(empId);
  const submitDonation = useSubmitDonation();

  const [hoursToDonate, setHoursToDonate] = useState("");
  // "continue" is the summary of the donation, "confirm" is the irrevocable acknowledgement.
  const [openDialog, setOpenDialog] = useState(null);

  const maxDonation = donationInfo.data?.maxDonation;

  const handleSubmit = () => {
    setOpenDialog(null);
    submitDonation.mutate({ empId, hoursToDonate });
    setHoursToDonate("");
  };

  return (
    <div>
      <Hero>Sick Leave Donation</Hero>

      <div className="text-center text-[120%]">
        {donationInfo.isPending ? (
          <p>Loading...</p>
        ) : maxDonation === 0 ? (
          <p>You are ineligible to donate sick leave at this time.</p>
        ) : (
          maxDonation >= 0.5 && (
            <div className="mt-5 bg-white shadow">
              <p className="pt-2.5">
                You may donate up to {maxDonation} hours in half-hour
                increments.
              </p>
              <form onSubmit={(e) => e.preventDefault()}>
                <label htmlFor="donation">Donation amount:</label>
                <input
                  type="number"
                  id="donation"
                  name="donation"
                  className="input m-1.5 w-24"
                  min={0.5}
                  max={maxDonation}
                  step={0.5}
                  value={hoursToDonate}
                  onChange={(e) => setHoursToDonate(e.target.value)}
                />
                <Button
                  // The legacy model was numeric, so a zeroed field left Continue disabled.
                  isDisabled={!parseFloat(hoursToDonate)}
                  onPress={() => setOpenDialog("continue")}
                >
                  Continue
                </Button>
              </form>
              <br />
            </div>
          )
        )}
      </div>

      <DonationHistory empId={empId} />

      <ContinueModal
        isOpen={openDialog === "continue"}
        hoursToDonate={hoursToDonate}
        accruedSickTime={donationInfo.data?.accruedSickTime}
        onContinue={() => setOpenDialog("confirm")}
        onCancel={() => setOpenDialog(null)}
      />
      <ConfirmModal
        isOpen={openDialog === "confirm"}
        lastName={user?.lastName}
        onSubmit={handleSubmit}
        onCancel={() => setOpenDialog(null)}
      />
    </div>
  );
}

function DonationHistory({ empId }) {
  const [year, setYear] = useState(new Date().getFullYear());
  const history = useDonationHistory(empId, year);
  const donations = history.data || [];

  return (
    <div className="mt-5 bg-white p-2.5">
      <p className="pb-2.5">
        <label className="font-semibold text-teal-700" htmlFor="year">
          Filter By Year&nbsp;
        </label>
        <select
          id="year"
          name="year"
          className="select"
          value={year}
          onChange={(e) => setYear(parseInt(e.target.value))}
        >
          {donationYears().map((donationYear) => (
            <option value={donationYear} key={donationYear}>
              {donationYear}
            </option>
          ))}
        </select>
      </p>

      {donations.length === 0 ? (
        <div className="text-center">
          You have no donations for this year yet.
        </div>
      ) : (
        <table className={styles.donationTable}>
          <thead>
            <tr>
              <th>Date</th>
              <th>Donation Amount</th>
            </tr>
          </thead>
          <tbody>
            {donations.map((donation) => {
              // The server sends each donation as a "M/D: hours" string.
              const [date, amount] = donation.split(":");
              return (
                <tr key={donation}>
                  <td>{date}</td>
                  <td>{amount}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
}

/** A summary of the donation, shown before the acknowledgement. */
function ContinueModal({
  isOpen,
  hoursToDonate,
  accruedSickTime,
  onContinue,
  onCancel,
}) {
  return (
    <Modal isOpen={isOpen} onOpenChange={(open) => !open && onCancel()}>
      <Modal.Title>Donation Confirmation</Modal.Title>
      <Modal.Body>
        <p className="text-center text-[120%]">
          You will donate {hoursToDonate} out of {accruedSickTime} accrued sick
          hours.
        </p>
      </Modal.Body>
      <Modal.Buttons>
        <Button onPress={onContinue}>Continue</Button>
        <Button variant="destructive" onPress={onCancel}>
          Go Back
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

/**
 * The acknowledgement that a donation cannot be taken back. The employee has to type their
 * own last name before the donation can be submitted.
 */
function ConfirmModal({ isOpen, lastName, onSubmit, onCancel }) {
  const [inputLastName, setInputLastName] = useState("");

  const close = () => {
    setInputLastName("");
    onCancel();
  };

  return (
    <Modal isOpen={isOpen} onOpenChange={(open) => !open && close()}>
      <Modal.Title>Donation Confirmation</Modal.Title>
      <Modal.Body>
        <div className="text-center text-[120%]">
          <p>
            Once you donate your sick leave, it is irrevocable and forfeited
            permanently.
            <br />
            The donated sick leave will not be returned to you.
          </p>
          <form
            className="my-3 inline-block text-left"
            onSubmit={(e) => e.preventDefault()}
          >
            <div>
              <label className="mr-2 font-semibold">Last Name</label>
              <span>{lastName}</span>
            </div>
            <div className="mt-2.5">
              <label className="mr-2 font-semibold" htmlFor="inputLastName">
                Confirm Last Name
              </label>
              <input
                type="text"
                id="inputLastName"
                className="input"
                value={inputLastName}
                onChange={(e) => setInputLastName(e.target.value)}
              />
            </div>
          </form>
        </div>
      </Modal.Body>
      <Modal.Buttons>
        <Button
          isDisabled={inputLastName !== lastName}
          onPress={() => {
            setInputLastName("");
            onSubmit();
          }}
        >
          Submit
        </Button>
        <Button variant="destructive" onPress={close}>
          Cancel
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}

function donationYears() {
  const years = [];
  for (
    let year = FIRST_DONATION_YEAR;
    year <= new Date().getFullYear();
    year++
  ) {
    years.push(year);
  }
  return years;
}
