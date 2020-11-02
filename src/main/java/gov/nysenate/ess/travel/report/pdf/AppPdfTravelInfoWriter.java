package gov.nysenate.ess.travel.report.pdf;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

public class AppPdfTravelInfoWriter implements AppPdfWriter {

    private final PdfConfig config;
    private final PDPageContentStream cs;
    // x and y coordinates representing the top left of where this will start writing.
    private final float x, y;
    private final TravelApplication app;

    public AppPdfTravelInfoWriter(PdfConfig config, PDPageContentStream cs, float x, float y, TravelApplication app) {
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
        final float column1LabelX = this.x;
        final float column1DataX = column1LabelX + 90f;

        // Departure
        drawText(column1LabelX, currentY, config.fontBold, config.fontSize, "Departure:");
        drawText(column1DataX, currentY, config.font, config.fontSize, app.activeAmendment().route().origin().getFormattedAddressWithCounty());

        // Destinations
        currentY -= leading;
        drawText(column1LabelX, currentY, config.fontBold, config.fontSize, "Destination:");
        // Each destination goes on its own line
        for (Destination destination : app.activeAmendment().route().destinations()) {
            drawText(column1DataX, currentY, config.font, config.fontSize, destination.getAddress().getFormattedAddressWithCounty());
            currentY -= leading;
        }

        // Dates of Travel
        boolean singleDay = app.activeAmendment().startDate().equals(app.activeAmendment().endDate());
        drawText(column1LabelX, currentY, config.fontBold, config.fontSize, "Dates of Travel:");
        // If a single day just write that day, if multiple write a range. i.e. "11/4/19 - 11/6/19"
        if (singleDay) {
            drawText(column1DataX, currentY, config.font, config.fontSize, app.activeAmendment().startDate().format(config.dateFormat));
        } else {
            drawText(column1DataX, currentY, config.font, config.fontSize, app.activeAmendment().startDate().format(config.dateFormat) + " - " + app.activeAmendment().endDate().format(config.dateFormat));
        }

        // Purpose
        currentY -= leading;
        String purposeText = app.activeAmendment().purposeOfTravel().eventType().displayName()
                + ": " + app.activeAmendment().purposeOfTravel().eventName();
        drawText(column1LabelX, currentY, config.fontBold, config.fontSize, "Purpose:");
        drawText(column1DataX, currentY, config.font, config.fontSize, purposeText);

        // Additional purpose
        if (!app.activeAmendment().purposeOfTravel().additionalPurpose().isEmpty()) {
            String text = app.activeAmendment().purposeOfTravel().additionalPurpose();
            for (String line : WordUtils.wrap(text, 80).split("\\n")) {
                currentY -= leading;
                drawText(column1DataX, currentY, config.font, config.fontSize, line);
            }
        }

        return currentY;
    }

    @Override
    public PDPageContentStream getContentStream() {
        return this.cs;
    }
}
