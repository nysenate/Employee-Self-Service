package gov.nysenate.ess.travel.report.pdf;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.review.ApplicationReview;
import org.apache.pdfbox.multipdf.Overlay;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.util.ResourceUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class TravelAppPdfGenerator {

    private static final float PAGE_MARGIN = 35f;
    private static final float HR_WIDTH = 3f;

    private final ApplicationReview appReview;
    private float currentY;         // The current Y position for the current page of the pdf.
    private float contentWidth;     // The width of the page minus the margin on both sides. Used to center text.
    private float lineStartX;       // The x position where all lines start.
    private float bottomY;          // The y position representing the bottom of the page.

    public TravelAppPdfGenerator(ApplicationReview appReview) {
        this.appReview = Preconditions.checkNotNull(appReview);
    }

    /**
     * Writes this application to a pdf on the given OutputStream.
     *
     * @param os The OutputStream to write this application's pdf to.
     */
    public void write(OutputStream os, boolean drawWatermark) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            // Initial page setup
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);
            PDRectangle mediaBox = page.getMediaBox();
            contentWidth = mediaBox.getWidth() - 2 * PAGE_MARGIN;
            lineStartX = mediaBox.getLowerLeftX() + PAGE_MARGIN;
            currentY = mediaBox.getUpperRightY() - PAGE_MARGIN;
            bottomY = mediaBox.getLowerLeftY() + PAGE_MARGIN;

            PdfConfig config = new PdfConfig(PDType1Font.TIMES_ROMAN, PDType1Font.TIMES_BOLD, 12f, 16f, 14f,
                    1.35f, DateTimeFormatter.ofPattern("MM/dd/yy"));

            // Draw header
            AppPdfHeaderWriter headerWriter = new AppPdfHeaderWriter(config, cs, lineStartX, lineStartX + contentWidth, currentY);
            currentY = headerWriter.write();
            currentY -= 30f;

            // Draw Employee Info
            AppPdfEmployeeInfoWriter employeeInfoWriter = new AppPdfEmployeeInfoWriter(config, cs, lineStartX, currentY, appReview.application());
            currentY = employeeInfoWriter.write();
            currentY -= 15f;
            drawHorizontalLine(cs);
            currentY -= 15f + 12f - HR_WIDTH; // Keep whitespace around horizontal line the same on both sides.

            // Draw App Info
            AppPdfTravelInfoWriter travelInfoWriter = new AppPdfTravelInfoWriter(config, cs, lineStartX, currentY, appReview.application());
            currentY = travelInfoWriter.write();
            currentY -= 15f;

            // Draw MOT and Expenses box
            if (calculateRemainingSpace() < 159f) {
                // 159f is the spacing needed for the expenses box and the horizontal line below it.
                // Cant fit on current page, start a new page.
                cs = newPage(doc, cs);
                AppPdfMotWriter motWriter = new AppPdfMotWriter(config, cs, lineStartX, currentY, appReview.application());
                motWriter.write(); // Do not update currentY, we want this and the expenses box to start at the same y

                AppPdfExpensesWriter expensesWriter = new AppPdfExpensesWriter(config, cs, lineStartX, currentY, appReview.application());
                currentY = expensesWriter.write();
            } else {
                // Draw on current page.
                AppPdfMotWriter motWriter = new AppPdfMotWriter(config, cs, lineStartX, currentY, appReview.application());
                motWriter.write(); // Do not update currentY, we want this and the expenses box to start at the same y

                AppPdfExpensesWriter expensesWriter = new AppPdfExpensesWriter(config, cs, lineStartX, currentY, appReview.application());
                currentY = expensesWriter.write();
            }

            currentY -= 5f;
            drawHorizontalLine(cs);
            currentY -= 30f;

            // Draw Signatures
            if (calculateRemainingSpace() < 64f) {
                // 64f is the space needed to draw signatures. Hard coded since I could not figure out how to calculate dynamically accurately.
                // Cant fit on current page, start a new page.
                cs = newPage(doc, cs);
                currentY -= 30f; // Additional top of page margin before signatures.
                AppPdfSignatureWriter signatureWriter = new AppPdfSignatureWriter(config, cs, lineStartX, currentY, appReview);
                currentY = signatureWriter.write();
            } else {
                AppPdfSignatureWriter signatureWriter = new AppPdfSignatureWriter(config, cs, lineStartX, currentY, appReview);
                currentY = signatureWriter.write();
            }

            if (drawWatermark) {
                // --- Draw the watermark ---
                // Add a watermark/overlay for each page.
                HashMap<Integer, PDDocument> overlayGuide = new HashMap<>();
                for (int i = 0; i < doc.getNumberOfPages(); i++) {
                    File file = ResourceUtils.getFile("classpath:travel/RTA_Watermark.pdf");
                    PDDocument overlayDoc = PDDocument.load(file);
                    overlayGuide.put(i + 1, overlayDoc);
                }

                // Draw the overlays
                Overlay overlay = new Overlay();
                overlay.setInputPDF(doc);
                overlay.setOverlayPosition(Overlay.Position.BACKGROUND);
                overlay.overlayDocuments(overlayGuide);
            }

            cs.close();
            doc.save(os);
        }
    }

    private PDPageContentStream newPage(PDDocument doc, PDPageContentStream cs) throws IOException {
        cs.close();
        PDPage page = new PDPage();
        doc.addPage(page);
        cs = new PDPageContentStream(doc, page);
        currentY = page.getMediaBox().getUpperRightY() - PAGE_MARGIN;
        return cs;
    }

    private float calculateRemainingSpace() {
        return currentY - bottomY;
    }

    private void drawHorizontalLine(PDPageContentStream cs) throws IOException {
        cs.moveTo(lineStartX, currentY);
        cs.setLineWidth(HR_WIDTH);
        cs.setStrokingColor(Color.GRAY);
        cs.lineTo(lineStartX + contentWidth, currentY);
        cs.stroke();
    }
}
