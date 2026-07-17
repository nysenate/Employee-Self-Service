import React, { useEffect, useState } from "react";
import { pingApi } from "app/api/pingApi";
import { Link, useNavigate } from "react-router-dom";
import Modal from "app/components/Modal";
import Button from "app/components/Button";

// Values are in Seconds.
const PING_RATE = 15;
const WARNING_THRESHOLD = 80;
const PING_FAIL_TOLERANCE = 10;

export default function TimeoutChecker({ children }) {
  const navigate = useNavigate();
  // The length of time remaining before the user is logged out if they do not perform an action.
  const [remainingTime, setRemainingTime] = React.useState();
  const [isActive, setIsActive] = React.useState(true);
  const [failedPings, setFailedPings] = React.useState(0);
  const [showInactivityModal, setShowInactivityModal] = useState(false);

  const ping = (isActive) => {
    pingApi(isActive)
      .then((res) => {
        setFailedPings(0);
        setRemainingTime(res.remainingInactivity);
        setIsActive(false); // reset active flag.
      })
      .catch((err) => {
        console.log(err);
        if (err.status === 401) {
          navigate("/logout");
        } else {
          setFailedPings((failedPings) => failedPings + 1);
        }
      });
  };

  React.useEffect(() => {
    if (remainingTime < 0 || failedPings >= PING_FAIL_TOLERANCE) {
      console.log(
        "Ping api logout due to remaining time expiration or too many failed pings",
      );
      navigate("/logout");
    } else if (remainingTime <= WARNING_THRESHOLD) {
      setShowInactivityModal(true);
    }
  }, [remainingTime, failedPings]);

  const setActive = () => {
    setIsActive(true);
  };

  React.useEffect(() => {
    document.addEventListener("change", setActive);
    document.addEventListener("keydown", setActive);
    document.addEventListener("click", setActive);
    document.addEventListener("keydown", setActive);
    document.addEventListener("scroll", setActive);
    return () => {
      document.removeEventListener("change", setActive);
      document.removeEventListener("keydown", setActive);
      document.removeEventListener("click", setActive);
      document.removeEventListener("keydown", setActive);
      document.removeEventListener("scroll", setActive);
    };
  }, []);

  React.useEffect(() => {
    const interval = setInterval(() => ping(isActive), PING_RATE * 1000);
    return () => clearInterval(interval);
  }, [isActive]);

  return (
    <>
      {children}
      {showInactivityModal && (
        <InactivityModal
          onResolve={() => {
            setIsActive(true);
            setRemainingTime(WARNING_THRESHOLD * 4);
            setShowInactivityModal(false);
          }}
        />
      )}
    </>
  );
}

function InactivityModal({ onResolve }) {
  const navigate = useNavigate();
  const [count, setCount] = useState(60);

  useEffect(() => {
    const interval = setInterval(
      () => setCount((prevCount) => prevCount - 1),
      1000,
    );
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (count < 1) {
      navigate("/logout");
    }
  }, [count]);

  return (
    <Modal isOpen={true}>
      <Modal.Title className="text-red-600">
        Inactive Session Timeout
      </Modal.Title>
      <Modal.Body className="w-96">
        Due to inactivity, you will be logged out in {count} seconds.
        <br />
        Do you want to continue your work?
      </Modal.Body>
      <Modal.Buttons>
        <Link to="/logout" style={{ textDecoration: "none" }}>
          <Button variant="destructive">Log out of ESS</Button>
        </Link>
        <Button variant="primary" onPress={() => onResolve()}>
          Continue
        </Button>
      </Modal.Buttons>
    </Modal>
  );
}
