package gov.nysenate.ess.travel.report.pdf;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.review.Action;
import gov.nysenate.ess.travel.review.ApplicationReview;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

public class AppPdfSignatureWriter implements AppPdfWriter {

    private final PdfConfig config;
    private final PDPageContentStream cs;
    // X and Y coordinates representing the top left of where we will start writing.
    private final float x, y;
    private final ApplicationReview appReview;

    private final float leading;
    private static final float FONT_SIZE = 11f;
    private static final float SIGNATURE_FONT_SIZE = 13f;

    private static final float DATE_LEFT_MARGIN = 202f;
    private static final float DATE_LABEL_LEFT_MARGIN = 225f;
    private static final float DEPARTMENT_HD_SIG_LEFT_MARGIN = 294f;
    private static final float SOS_SIG_TOP_MARGIN = 40f;

    public AppPdfSignatureWriter(PdfConfig config, PDPageContentStream cs, float x, float y, ApplicationReview appReview) {
        this.config = Preconditions.checkNotNull(config);
        this.cs = Preconditions.checkNotNull(cs);
        this.x = x;
        this.y = y;
        this.appReview = Preconditions.checkNotNull(appReview);
        this.leading = config.leadingRatio * FONT_SIZE * 0.8f;
    }

    @Override
    public float write() throws IOException {
        float currentY = y;

        // Traveler Signature
        String travelerName = appReview.application().getTraveler().getFullName();
        drawText(x, currentY, config.font, SIGNATURE_FONT_SIZE, travelerName);
        drawText(x + DATE_LEFT_MARGIN, currentY, config.font, SIGNATURE_FONT_SIZE,
                appReview.application().getSubmittedDateTime().format(config.dateFormat));

        // DeptHd Signature
        Action deptHeadAction = appReview.getLatestActionByRole(TravelRole.DEPARTMENT_HEAD);
        if (deptHeadAction != null && deptHeadAction.isApproval()) {
            drawText(x + DEPARTMENT_HD_SIG_LEFT_MARGIN, currentY, config.font, SIGNATURE_FONT_SIZE, deptHeadAction.user().getFullName());
            drawText(x + DEPARTMENT_HD_SIG_LEFT_MARGIN + DATE_LEFT_MARGIN, currentY, config.font, SIGNATURE_FONT_SIZE,
                    deptHeadAction.dateTime().format(config.dateFormat));
        }

        currentY -= 2f;

        // Signature lines.
        String signatureLine = "_____________________________________________";
        drawText(x, currentY, config.fontBold, FONT_SIZE, signatureLine);
        drawText(x + DEPARTMENT_HD_SIG_LEFT_MARGIN, currentY, config.fontBold, FONT_SIZE, signatureLine);
        currentY -= leading;

        // Signature labels.
        drawText(x, currentY, config.font, FONT_SIZE, "Signature of Traveler");
        drawText(x + DATE_LABEL_LEFT_MARGIN, currentY, config.font, FONT_SIZE, "Date");
        drawText(x + DEPARTMENT_HD_SIG_LEFT_MARGIN, currentY, config.font, FONT_SIZE,
                "Member/Department Head");
        drawText(x + DEPARTMENT_HD_SIG_LEFT_MARGIN + DATE_LABEL_LEFT_MARGIN, currentY, config.font, FONT_SIZE, "Date");

        currentY -= SOS_SIG_TOP_MARGIN;

        // SOS Signature
        Action approvalAction = appReview.getLatestActionByRole(TravelRole.SECRETARY_OF_THE_SENATE);
        if (approvalAction != null && approvalAction.isApproval()) {
            float height = (config.font.getFontDescriptor().getCapHeight()) / 1000 * SIGNATURE_FONT_SIZE;
            currentY += height + 2f;
            drawText(x + (DEPARTMENT_HD_SIG_LEFT_MARGIN / 2), currentY, config.font, SIGNATURE_FONT_SIZE, "Alejandra N. Paulino");
            drawText(x + (DEPARTMENT_HD_SIG_LEFT_MARGIN / 2) + DATE_LEFT_MARGIN, currentY, config.font, SIGNATURE_FONT_SIZE,
                    approvalAction.dateTime().format(config.dateFormat));
            currentY -= 2f;
        }
        drawText(x + (DEPARTMENT_HD_SIG_LEFT_MARGIN /2), currentY, config.fontBold, FONT_SIZE, signatureLine);
        currentY -= leading;

        drawText(x + (DEPARTMENT_HD_SIG_LEFT_MARGIN /2), currentY, config.font, FONT_SIZE,
                "Secretary of the Senate                                            Date");

        return currentY;
    }

    @Override
    public PDPageContentStream getContentStream() {
        return this.cs;
    }
}
