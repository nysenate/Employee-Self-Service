import { describe, expect, it } from "vitest";
import {
  applyLodgingCalculation,
  applyEditableExpenses,
  createEditableExpenses,
  updateExpenseRow,
  validateExpenses,
} from "./expenseModel";

describe("expense model", () => {
  it("accepts ordinary currency and treats blanks as zero", () => {
    const values = {
      tolls: "",
      parking: ".50",
      alternateTransportation: "1",
      trainAndPlane: "1.2",
      registration: "1.20",
    };
    expect(validateExpenses(values)).toEqual({});
    expect(
      applyEditableExpenses({ amendment: {} }, values).amendment.allowances,
    ).toEqual({
      tolls: 0,
      parking: 0.5,
      alternateTransportation: 1,
      trainAndPlane: 1.2,
      registration: 1.2,
    });
  });

  it.each(["words", "-1", "1.234", "1,000.00", "1e3", "+10"])(
    "rejects unsupported amount %s",
    (amount) => {
      expect(
        validateExpenses({ ...createEditableExpenses({}), tolls: amount }),
      ).toHaveProperty("tolls");
    },
  );

  it("updates one reimbursement without resetting declined rows", () => {
    const draft = {
      amendment: {
        mealPerDiems: {
          allMealPerDiems: [
            { id: 0, isDinnerRequested: false },
            { id: 0, isDinnerRequested: true },
          ],
        },
      },
    };
    const next = updateExpenseRow(draft, "mealPerDiems", 1, {
      isDinnerRequested: false,
    });
    expect(next.amendment.mealPerDiems.allMealPerDiems).toEqual([
      { id: 0, isDinnerRequested: false },
      { id: 0, isDinnerRequested: false },
    ]);
  });

  it("applies a lodging result without resetting the latest reimbursement choice", () => {
    const draft = {
      amendment: {
        lodgingPerDiems: {
          allLodgingPerDiems: [
            { id: 1, rate: 100, isReimbursementRequested: false },
          ],
        },
      },
    };

    const next = applyLodgingCalculation(draft, 0, {
      id: 99,
      rate: 175,
      isReimbursementRequested: true,
    });

    expect(next.amendment.lodgingPerDiems.allLodgingPerDiems[0]).toMatchObject({
      id: 1,
      rate: 175,
      isReimbursementRequested: false,
    });
  });
});
