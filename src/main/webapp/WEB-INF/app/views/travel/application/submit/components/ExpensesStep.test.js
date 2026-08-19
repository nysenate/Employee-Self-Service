import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { fireEvent, render, screen } from "@testing-library/react";
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
    const breakfasts = screen.getAllByRole("checkbox", { name: "Breakfast" });
    fireEvent.click(breakfasts[0]);
    expect(
      onDraftChange.mock.calls[0][0].amendment.mealPerDiems.allMealPerDiems,
    ).toEqual([
      expect.objectContaining({ id: 0, isBreakfastRequested: false }),
      expect.objectContaining({ id: 0, isBreakfastRequested: true }),
    ]);
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

  it("disables only the lodging address whose rate is being calculated", () => {
    const lodgingDraft = {
      amendment: {
        lodgingPerDiems: {
          allLodgingPerDiems: [
            { id: 10, date: "2026-08-10", address: address("Albany") },
            { id: 11, date: "2026-08-11", address: address("Buffalo") },
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
  });
});
