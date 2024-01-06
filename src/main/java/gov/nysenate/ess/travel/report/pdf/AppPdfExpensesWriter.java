package gov.nysenate.ess.travel.report.pdf;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.travel.application.TravelApplication;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

public class AppPdfExpensesWriter implements AppPdfWriter {

    private final PdfConfig config;
    private final PDPageContentStream cs;
    // X and Y coordinates representing the top left of where we will start writing.
    private final float x, y;
    private final TravelApplication app;

    public AppPdfExpensesWriter(PdfConfig config, PDPageContentStream cs, float x, float y, TravelApplication app) {
        this.config = Preconditions.checkNotNull(config);
        this.cs = Preconditions.checkNotNull(cs);
        this.x = x;
        this.y = y;
        this.app = Preconditions.checkNotNull(app);
    }

    @Override
    public float write() throws IOException {
        float currentY = y;
        final float leading = config.leadingRatio * config.fontSize;
        float boxStartX = x + 295f;
        float boxTextStartX = boxStartX + 5f;
        float boxWidth = 213f;
        float boxHeight = 145f;
        float boxRightAlignEndX = boxStartX + boxWidth - 5f;
        float lineWidth = 1f;

        drawBox(boxStartX, currentY, boxWidth, boxHeight, lineWidth);
        currentY -= leading;
        drawCenteredText(boxStartX, boxStartX + boxWidth, currentY, config.fontBold, config.fontSize, "Estimated Travel Costs");
        currentY -= (1.5 * leading); // Add a little extra spacing under box title.

        // Transportation
        drawEstimatedTravelCostsRow(cs, boxTextStartX, boxRightAlignEndX, currentY,
                "Transportation (" + app.activeAmendment().route().mileagePerDiems().totalMileage() + " Miles)",
                app.activeAmendment().transportationAllowance().toString());
        currentY -= leading;

        // Food
        drawEstimatedTravelCostsRow(cs, boxTextStartX, boxRightAlignEndX, currentY, "Food", app.activeAmendment().mealAllowance().toString());
        currentY -= leading;

        // Lodging
        drawEstimatedTravelCostsRow(cs, boxTextStartX, boxRightAlignEndX, currentY, "Lodging", app.activeAmendment().lodgingAllowance().toString());
        currentY -= leading;

        // Parking/Tolls
        drawEstimatedTravelCostsRow(cs, boxTextStartX, boxRightAlignEndX, currentY, "Parking/Tolls", app.activeAmendment().tollsAndParkingAllowance().toString());
        currentY -= leading;

        // Taxi/Bus/Subway
        drawEstimatedTravelCostsRow(cs, boxTextStartX, boxRightAlignEndX, currentY, "Taxi/Bus/Subway", app.activeAmendment().alternateTransportationAllowance().toString());
        currentY -= leading;

        // Registration Fee
        drawEstimatedTravelCostsRow(cs, boxTextStartX, boxRightAlignEndX, currentY, "Registration Fee", app.activeAmendment().registrationAllowance().toString());
        currentY -= leading;

        // Total
        drawEstimatedTravelCostsRow(cs, boxTextStartX, boxRightAlignEndX, currentY, "TOTAL", app.activeAmendment().totalAllowance().toString());
        currentY -= leading;

        return currentY;
    }

    private void drawEstimatedTravelCostsRow(PDPageContentStream cs, float boxTextStartX, float boxRightAlignEndX, float y, String label, String dollars) throws IOException {
        drawText(boxTextStartX, y, config.font, config.fontSize, label);
        // Calculate x position for right aligned text
        String value = "$" + dollars;
        float rightAlignStartX = calculateRightAlignStartX(boxRightAlignEndX, value);
        drawText(rightAlignStartX, y, config.font, config.fontSize, value);
    }

    private float calculateRightAlignStartX(float boxRightAlignEndX, String value) throws IOException {
        float valueWidth = config.fontSize * config.font.getStringWidth(value) / 1000;
        return boxRightAlignEndX - valueWidth;
    }

    @Override
    public PDPageContentStream getContentStream() {
        return this.cs;
    }
}
