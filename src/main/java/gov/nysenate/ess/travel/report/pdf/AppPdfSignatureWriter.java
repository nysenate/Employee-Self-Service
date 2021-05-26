package gov.nysenate.ess.travel.report.pdf;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

public class AppPdfSignatureWriter implements AppPdfWriter {

    private final PdfConfig config;
    private final PDPageContentStream cs;
    // X and Y coordinates representing the top left of where we will start writing.
    private final float x, y;
    private final TravelApplication app;

    private final float leading;
    private static final float FONT_SIZE = 11f;
    private static final float SOS_SIG_TOP_MARGIN = 40f;

    public AppPdfSignatureWriter(PdfConfig config, PDPageContentStream cs, float x, float y, TravelApplication app) {
        this.config = Preconditions.checkNotNull(config);
        this.cs = Preconditions.checkNotNull(cs);
        this.x = x;
        this.y = y;
        this.app = Preconditions.checkNotNull(app);
        this.leading = config.leadingRatio * FONT_SIZE * 0.8f;
    }

    @Override
    public float write() throws IOException {
        float currentY = y;
        float deptHdSigMargin = 294f;

        String signatureLine = "_____________________________________________";
        drawText(x, currentY, config.fontBold, FONT_SIZE, signatureLine);
        drawText(x + deptHdSigMargin, currentY, config.fontBold, FONT_SIZE, signatureLine);
        currentY -= leading;

        drawText(x, currentY, config.font, FONT_SIZE,
                "Signature of Traveler                                               Date");
        drawText(x + deptHdSigMargin, currentY, config.font, FONT_SIZE,
                "Member/Department Head                                      Date");
        currentY -= SOS_SIG_TOP_MARGIN;

        drawText(x + (deptHdSigMargin /2), currentY, config.fontBold, FONT_SIZE, signatureLine);
        currentY -= leading;

        drawText(x + (deptHdSigMargin /2), currentY, config.font, FONT_SIZE,
                "Secretary of the Senate                                            Date");

        return currentY;
    }

    @Override
    public PDPageContentStream getContentStream() {
        return this.cs;
    }
}
