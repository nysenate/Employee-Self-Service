package gov.nysenate.ess.travel.report.pdf;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.request.route.MethodOfTravel;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * App pdf Mode of Transportation Writer
 */
public class AppPdfMotWriter implements AppPdfWriter {

    private final PdfConfig config;
    private final PDPageContentStream cs;
    // X and Y coordinates representing the top left of where we will start writing.
    private final float x, y;
    private final TravelApplication app;

    public AppPdfMotWriter(PdfConfig config, PDPageContentStream cs, float x, float y, TravelApplication app) {
        this.config = Preconditions.checkNotNull(config);
        this.cs = Preconditions.checkNotNull(cs);
        this.x = x;
        this.y = y;
        this.app = Preconditions.checkNotNull(app);
    }

    @Override
    public float write() throws IOException {
        final float leading = config.leadingRatio * config.fontSize;
        float currentY = y;
        float boxStartX = x + 60f;
        float boxTextStartX = boxStartX + 5f;
        float checkboxStartX = boxTextStartX + 120f;
        float boxWidth = 150f; // Box dimensions
        float boxHeight = 130f;
        float lineWidth = 1f;

        // Draw the box starting at top left corner
        drawBox(boxStartX, currentY, boxWidth, boxHeight, lineWidth);
        currentY -= leading;

        // Draw the modes of transportation and checkbox's
        String title = "Mode of Transportation";
        drawCenteredText(boxStartX, boxStartX + boxWidth, currentY, config.fontBold, config.fontSize, title);
        currentY -= (1.5 * leading); // Add a little extra spacing under box title.

        for (MethodOfTravel mot : MethodOfTravel.values()) {
            if (mot.equals(MethodOfTravel.OTHER)) {
                if (app.getRoute().hasMethodOfTravel(mot)) {
                    // If method of travel is OTHER, display the method of travel description which describes how they are traveling.
                    Set<String> otherDescriptions = app.getRoute().getMethodOfTravelDescriptions(mot);
                    Optional<String> firstDescription = otherDescriptions.stream().findFirst();
                    drawText(boxTextStartX, currentY, config.font, config.fontSize, mot.getDisplayName() + ": " + firstDescription.orElse(""));
                } else {
                    drawText(boxTextStartX, currentY, config.font, config.fontSize, mot.getDisplayName());
                }
            } else {
                drawText(boxTextStartX, currentY, config.font, config.fontSize, mot.getDisplayName());
            }
            float checkBoxWidth = 8f;
            float checkBoxStartY = currentY + checkBoxWidth; // Calculate the Y needed to center the checkbox in this line of text. boxY is the bottom of the text.
            drawCheckbox(checkboxStartX, checkBoxStartY, checkBoxWidth, checkBoxWidth, 1f, app.getRoute().hasMethodOfTravel(mot));
            currentY -= leading;
        }

        return currentY;
    }

    @Override
    public PDPageContentStream getContentStream() {
        return this.cs;
    }
}
