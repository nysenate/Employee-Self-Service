package gov.nysenate.ess.travel.report.pdf;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.io.IOException;

public interface AppPdfWriter {

    /**
     * Write content to the pdf.
     * @return The last Y position written to. Used to know where to start the next part of the pdf.
     */
    float write() throws IOException;

    /**
     * Get the current PDPageContentStream. Used by default draw methods.
     * @return
     */
    PDPageContentStream getContentStream();

    /**
     * Writes the given text starting at position x,y with the given font and font size.
     * @throws IOException
     */
    default void drawText(float x, float y, PDFont font, float fontSize, String text) throws IOException {
        PDPageContentStream cs = getContentStream();
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    /**
     * Writes text centered between leftX and rightX at y height.
     * @throws IOException
     */
    default void drawCenteredText(float leftX, float rightX, float y, PDFont font, float fontSize, String text) throws IOException {
        float textWidth = fontSize * font.getStringWidth(text) / 1000;
        float centeringMargin = (rightX - leftX - textWidth) / 2; // The margin needed to center the text between leftX and rightX.
        drawText(leftX + centeringMargin, y, font, fontSize, text);
    }

    /**
     * Draws a box
     *
     * @param x         The top left x coordinate of the box.
     * @param y         The top left y coordinate of the box.
     * @param width     The width of the box.
     * @param height    The height of the box.
     * @param lineWidth The line width to draw with.
     * @throws IOException
     */
    default void drawBox(float x, float y, float width, float height, float lineWidth) throws IOException {
        PDPageContentStream cs = getContentStream();
        cs.setStrokingColor(Color.GRAY);
        cs.moveTo(x, y);
        cs.setLineWidth(lineWidth);
        cs.lineTo(x + width, y);
        cs.lineTo(x + width, y - height);
        cs.lineTo(x, y - height);
        cs.lineTo(x, y + lineWidth / 2);
        cs.stroke();
    }

    /**
     * Draw a checkbox, optionally checked or not.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param lineWidth
     * @param checked
     * @throws IOException
     */
    default void drawCheckbox(float x, float y, float width, float height, float lineWidth, boolean checked) throws IOException {
        PDPageContentStream cs = getContentStream();
        drawBox(x, y, width, height, lineWidth);
        if (checked) {
            // Draw the check
            cs.setStrokingColor(Color.BLACK);
            cs.moveTo(x + height * 0.15f, y - width * .5f);
            cs.lineTo(x + height * 0.4f, y - width * .8f);
            cs.lineTo(x + height * 0.85f, y - width * .15f);
            cs.stroke();
        }
    }
}
