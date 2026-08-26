import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import OutsideConusModal from "./OutsideConusModal";

describe("outside-CONUS warning", () => {
  it("matches the legacy guidance and opens both resources safely", () => {
    const onClose = vi.fn();
    render(<OutsideConusModal isOpen onClose={onClose} />);

    expect(
      screen.getByRole("dialog", {
        name: "Your destination is outside the continental U.S.",
      }),
    ).toHaveTextContent(
      "ESS Travel can only calculate housing and other costs for the 48 continental U.S. states",
    );
    const links = screen.getAllByRole("link", { name: "here" });
    expect(links[0]).toHaveAttribute(
      "href",
      "https://my.nysenate.gov/department/secretary-senate/travel",
    );
    expect(links[1]).toHaveAttribute(
      "href",
      "https://www.travel.dod.mil/Travel-Transportation-Rates/Per-Diem/Per-Diem-Rate-Lookup/",
    );
    links.forEach((link) => {
      expect(link).toHaveAttribute("target", "_blank");
      expect(link).toHaveAttribute("rel", "noopener noreferrer");
    });

    fireEvent.click(screen.getByRole("button", { name: "Okay" }));
    expect(onClose).toHaveBeenCalledOnce();
  });
});
