package gov.nysenate.ess.travel.report.pdf;

import com.google.common.base.Preconditions;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

public class AppPdfHeaderWriter implements AppPdfWriter {

    private final PdfConfig config;
    private final PDPageContentStream cs;
    // The left and right x coordinates this writer should write between. Used for centering text.
    private final float leftX, rightX;
    // The starting y coordinate for this writer. Start first line here.
    private final float y;

    public AppPdfHeaderWriter(PdfConfig config, PDPageContentStream cs, float leftX, float rightX, float y) {
        this.config = Preconditions.checkNotNull(config);
        this.cs = Preconditions.checkNotNull(cs);
        this.leftX = leftX;
        this.rightX = rightX;
        this.y = y;
    }

    /**
     * Writes the headers to the PDPageContentStream.
     * @return Current y position after all writing.
     * @throws IOException
     */
    @Override
    public float write() throws IOException {
        float currentY = this.y;
        drawCenteredText(leftX, rightX, currentY, config.fontBold, config.h1FontSize, "NEW YORK STATE SENATE");
        currentY -= config.leadingRatio * config.h2FontSize;
        drawCenteredText(leftX, rightX, currentY, config.fontBold, config.h2FontSize, "Secretary of the Senate");
        currentY -= config.leadingRatio * config.h1FontSize;
        drawCenteredText(leftX, rightX, currentY, config.font, config.h1FontSize, "APPLICATION FOR TRAVEL APPROVAL");
        currentY -= config.leadingRatio * config.h2FontSize;
        drawCenteredText(leftX, rightX, currentY, config.fontBold, config.h2FontSize, "Prior Approval for ALL travel must be obtained from the Secretary of the Senate");
        return currentY;
    }

    @Override
    public PDPageContentStream getContentStream() {
        return this.cs;
    }
}
