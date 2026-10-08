import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen, within } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import ExpensesStep from "./ExpensesStep";
import { createEditableExpenses } from "../expenseModel";

const address = (name) => ({ formattedAddressWithCounty: name, zip5: "12207" });
const draft = {
  amendment: {
    allowances: {},
    mealPerDiems: {
      isAllowedMeals: true,
      allMealPerDiems: [
        {
          id: 0,
          date: "2026-08-10",
          address: address("Albany"),
          qualifiesForBreakfast: true,
          qualifiesForDinner: false,
          isBreakfastRequested: true,
        },
        {
          id: 0,
          date: "2026-08-11",
          address: address("Buffalo"),
          qualifiesForBreakfast: true,
          qualifiesForDinner: false,
          isBreakfastRequested: true,
        },
      ],
    },
    lodgingPerDiems: { allLodgingPerDiems: [] },
    mileagePerDiems: {
      doesTripQualifyForReimbursement: true,
      allPerDiems: [
        {
          id: 3,
          from: address("Albany"),
          to: address("Buffalo"),
          travelDate: "08/10/2026",
          qualifiesForReimbursement: true,
          isReimbursementRequested: false,
        },
        { id: 4, qualifiesForReimbursement: false },
      ],
    },
  },
};

describe("ExpensesStep", () => {
  it("shows eligible declined reimbursements and hides ineligible rows", () => {
    const onDraftChange = vi.fn();
    render(
      <ExpensesStep
        draft={draft}
        expenses={createEditableExpenses(draft)}
        errors={{}}
        lodgingErrors={{}}
        pendingLodgingRows={{}}
        onExpensesChange={() => {}}
        onDraftChange={onDraftChange}
        onLodgingSelect={() => {}}
        actions={<button>Next</button>}
      />,
    );
    expect(
      screen.getAllByRole("checkbox", { name: "Breakfast" })[0],
    ).toBeChecked();
    expect(
      screen.getByRole("checkbox", { name: /Albany to Buffalo/ }),
    ).not.toBeChecked();
    const mileageTable = screen.getByRole("table", {
      name: "Eligible mileage reimbursements",
    });
    expect(mileageTable).toBeVisible();
    expect(
      within(mileageTable).getByRole("columnheader", { name: "From" }),
    ).toBeVisible();
    expect(
      within(mileageTable).getByRole("columnheader", { name: "To" }),
    ).toBeVisible();
    expect(
      within(mileageTable).getByRole("columnheader", { name: "Date" }),
    ).toBeVisible();
    expect(
      within(mileageTable).getByRole("columnheader", { name: "Claim" }),
    ).toBeVisible();
    expect(within(mileageTable).getByText("Aug 10, 2026")).toBeVisible();
    const travelGuidelines = screen.getByRole("link", {
      name: "Senate Travel Guidelines",
    });
    expect(travelGuidelines).toHaveAttribute(
      "href",
      "https://my.nysenate.gov/department/secretary-senate/travel",
    );
    expect(travelGuidelines.closest("aside")).toHaveTextContent(
      "Refer to the Senate Travel Guidelines for current eligibility rules and rates.",
    );
    const mealTable = screen.getByRole("table", {
      name: "Eligible meal reimbursements",
    });
    expect(mealTable).toBeVisible();
    expect(
      within(mealTable).getByRole("columnheader", { name: "Address" }),
    ).toBeVisible();
    expect(
      within(mealTable).getByRole("columnheader", { name: "Date" }),
    ).toBeVisible();
    expect(
      within(mealTable).getByRole("columnheader", { name: "Breakfast" }),
    ).toBeVisible();
    expect(
      within(mealTable).getByRole("columnheader", { name: "Dinner" }),
    ).toBeVisible();
    expect(within(mealTable).getByText("Aug 10, 2026")).toBeVisible();
    const breakfasts = screen.getAllByRole("checkbox", { name: "Breakfast" });
    fireEvent.click(breakfasts[0]);
    expect(
      onDraftChange.mock.calls[0][0].amendment.mealPerDiems.allMealPerDiems,
    ).toEqual([
      expect.objectContaining({ id: 0, isBreakfastRequested: false }),
      expect.objectContaining({ id: 0, isBreakfastRequested: true }),
    ]);
  });

  it("hides mileage reimbursements when the overall trip is ineligible", () => {
    const ineligibleMileageDraft = {
      ...draft,
      amendment: {
        ...draft.amendment,
        mileagePerDiems: {
          ...draft.amendment.mileagePerDiems,
          doesTripQualifyForReimbursement: false,
        },
      },
    };

    render(
      <ExpensesStep
        draft={ineligibleMileageDraft}
        expenses={createEditableExpenses(ineligibleMileageDraft)}
        errors={{}}
        lodgingErrors={{}}
        pendingLodgingRows={{}}
        onExpensesChange={() => {}}
        onDraftChange={() => {}}
        onLodgingSelect={() => {}}
        actions={<button>Next</button>}
      />,
    );

    expect(
      screen.queryByRole("heading", { name: "Mileage reimbursements" }),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole("checkbox", { name: /Albany to Buffalo/ }),
    ).not.toBeInTheDocument();
  });

  it("uses shared invalid input styling and error summary", () => {
    render(
      <ExpensesStep
        draft={{ amendment: {} }}
        expenses={{ ...createEditableExpenses({}), tolls: "bad" }}
        errors={{ tolls: "Tolls must be numbers." }}
        lodgingErrors={{}}
        pendingLodgingRows={{}}
        onExpensesChange={() => {}}
        onDraftChange={() => {}}
        onLodgingSelect={() => {}}
        actions={null}
      />,
    );
    const tolls = screen.getByLabelText(/^Tolls/);
    expect(tolls).toHaveClass("input--invalid");
    expect(tolls).toHaveAttribute("aria-describedby", "expense-tolls-error");
    expect(document.getElementById("expense-tolls-error")).toHaveTextContent(
      "Tolls must be numbers.",
    );
    expect(screen.getByText("Please correct the following:")).toBeVisible();
  });

  it("links to the NYS Thruway toll calculator from expense guidance", () => {
    render(
      <ExpensesStep
        draft={{ amendment: {} }}
        expenses={createEditableExpenses({})}
        errors={{}}
        lodgingErrors={{}}
        pendingLodgingRows={{}}
        onExpensesChange={() => {}}
        onDraftChange={() => {}}
        onLodgingSelect={() => {}}
        actions={null}
      />,
    );

    const tollCalculator = screen.getByRole("link", {
      name: "NYS Thruway Toll Calculator",
    });
    expect(tollCalculator).toHaveAttribute(
      "href",
      "https://tollcalculator.thruway.ny.gov",
    );
    expect(tollCalculator).toHaveAttribute("target", "_blank");
    expect(tollCalculator).toHaveAttribute("rel", "noopener noreferrer");
  });

  it("disables only the lodging address whose rate is being calculated", () => {
    const lodgingDraft = {
      amendment: {
        lodgingPerDiems: {
          allLodgingPerDiems: [
            {
              id: 10,
              date: "2026-08-10",
              address: address("Albany"),
              rate: 121,
              requestedPerDiem: 121,
              isReimbursementRequested: true,
            },
            {
              id: 11,
              date: "2026-08-11",
              address: address("Buffalo"),
              rate: 121,
              requestedPerDiem: 121,
              isReimbursementRequested: false,
            },
          ],
        },
      },
    };
    render(
      <QueryClientProvider client={new QueryClient()}>
        <ExpensesStep
          draft={lodgingDraft}
          expenses={createEditableExpenses(lodgingDraft)}
          errors={{}}
          lodgingErrors={{}}
          pendingLodgingRows={{ 0: true }}
          onExpensesChange={() => {}}
          onDraftChange={() => {}}
          onLodgingSelect={() => {}}
          actions={<button>Next</button>}
        />
      </QueryClientProvider>,
    );

    expect(
      screen.getByLabelText("Hotel address for 2026-08-10"),
    ).toBeDisabled();
    expect(screen.getByLabelText("Hotel address for 2026-08-11")).toBeEnabled();
    expect(screen.getByLabelText(/^Tolls/)).toBeEnabled();
    expect(
      screen.getByRole("table", { name: "Eligible lodging reimbursements" }),
    ).toBeVisible();
    expect(
      screen.getByRole("columnheader", { name: "Hotel address" }),
    ).toBeVisible();
    expect(screen.getByRole("columnheader", { name: "Claim" })).toBeVisible();
    expect(
      screen.getByRole("checkbox", { name: "Claim lodging for Aug 10, 2026" }),
    ).toBeChecked();
    expect(
      screen.getByRole("checkbox", { name: "Claim lodging for Aug 11, 2026" }),
    ).not.toBeChecked();
    expect(screen.queryByText(/Rate:/)).not.toBeInTheDocument();
    expect(screen.queryByText(/Requested:/)).not.toBeInTheDocument();
  });
});
