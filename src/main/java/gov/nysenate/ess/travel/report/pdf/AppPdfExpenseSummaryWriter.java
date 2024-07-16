package gov.nysenate.ess.travel.report.pdf;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import gov.nysenate.ess.travel.request.allowances.lodging.LodgingPerDiem;
import gov.nysenate.ess.travel.request.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.request.allowances.mileage.MileagePerDiem;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.utils.Dollars;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class AppPdfExpenseSummaryWriter implements AppPdfWriter {

    private final PdfConfig config;
    private final PDDocument doc;
    private final float contentWidth;
    private float y;
    private float yStartNewPage;
    private final TravelApplication app;
    private static final float tableFontSize = 10f;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final float FREE_SPACE_FOR_PAGE_BREAK = 200f; // If there is less than this much free space, start table on a new page.

    public AppPdfExpenseSummaryWriter(PDDocument doc, float contentWidth, PdfConfig config, TravelApplication app) {
        this.doc = doc;
        this.contentWidth = contentWidth;
        this.config = config;
        this.app = app;
    }

    @Override
    public float write() throws IOException {
        PDPage page = new PDPage();
        doc.addPage(page);
        yStartNewPage = page.getMediaBox().getUpperRightY() - 35f;
        y = yStartNewPage;

        page = writeMealSummary(page);
        y -= 30f;
        page = writeLodgingSummary(page);
        y -= 30f;
        page = writeMileageExpenses(page);

        return y;
    }

    private PDPage writeMealSummary(PDPage page) throws IOException {
        // If no meal per diem's, don't draw anything.
        if (app.getMealPerDiems().total().equals(Dollars.ZERO)) {
            return page;
        }

        BaseTable dataTable = new BaseTable(y, yStartNewPage, 35f, contentWidth, 35f, doc, page, true, true);
        dataTable.drawTitle("Meal Expenses", config.fontBold, 14, contentWidth, 18f, "center", FREE_SPACE_FOR_PAGE_BREAK, true);
        Row<PDPage> headerRow = dataTable.createRow(16f);
        createHeaderCell(headerRow, 15, "Date");
        createHeaderCell(headerRow, 40, "Address");
        createHeaderCell(headerRow, 15, "Breakfast");
        createHeaderCell(headerRow, 15, "Dinner");
        createHeaderCell(headerRow, 15, "Total");
        dataTable.addHeaderRow(headerRow);
        for (MealPerDiem mpd : app.getMealPerDiems().requestedMealPerDiems()) {
            if (mpd.isReimbursementRequested()) {
                Row<PDPage> row = dataTable.createRow(16f);
                createBodyCell(row, 15, mpd.date().format(DATE_FORMAT), HorizontalAlignment.LEFT);
                createBodyCell(row, 40, mpd.address().getFormattedAddressWithCounty(), HorizontalAlignment.LEFT);
                createBodyCell(row, 15, "$" + mpd.breakfast(), HorizontalAlignment.RIGHT);
                createBodyCell(row, 15, "$" + mpd.dinner(), HorizontalAlignment.RIGHT);
                createBodyCell(row, 15, "$" + mpd.total(), HorizontalAlignment.RIGHT);
            }
        }
        // Total Row
        Row<PDPage> row = dataTable.createRow(16f);
        createBodyCell(row, 15, "Total", HorizontalAlignment.LEFT, config.fontBold, tableFontSize);
        createBodyCell(row, 40, "", HorizontalAlignment.LEFT);
        createBodyCell(row, 15, "", HorizontalAlignment.LEFT);
        createBodyCell(row, 15, "", HorizontalAlignment.LEFT);
        createBodyCell(row, 15, "$" + app.mealAllowance(), HorizontalAlignment.RIGHT,
                config.fontBold, tableFontSize);

        y = dataTable.draw();
        return dataTable.getCurrentPage();
    }

    private PDPage writeLodgingSummary(PDPage page) throws IOException {
        if (app.getLodgingPerDiems().requestedLodgingPerDiems().isEmpty()) {
            return page;
        }

        BaseTable dataTable = new BaseTable(y, yStartNewPage, 35f, contentWidth, 35f, doc, page, true, true);
        dataTable.drawTitle("Lodging Expenses", config.fontBold, 14, contentWidth, 18f, "center", FREE_SPACE_FOR_PAGE_BREAK, true);
        Row<PDPage> headerRow = dataTable.createRow(16f);
        createHeaderCell(headerRow, 15, "Date");
        createHeaderCell(headerRow, 70, "Address");
        createHeaderCell(headerRow, 15, "Rate");
        dataTable.addHeaderRow(headerRow);

        for(LodgingPerDiem lpd : app.getLodgingPerDiems().requestedLodgingPerDiems()) {
            Row<PDPage> row = dataTable.createRow(16f);
            createBodyCell(row, 15, lpd.date().format(DATE_FORMAT), HorizontalAlignment.LEFT);
            createBodyCell(row, 70, lpd.address().getFormattedAddressWithCounty(), HorizontalAlignment.LEFT);
            createBodyCell(row, 15, "$" + lpd.requestedPerDiem().toString(), HorizontalAlignment.RIGHT);
        }

        // Total Row
        Row<PDPage> row = dataTable.createRow(16f);
        createBodyCell(row, 15, "Total", HorizontalAlignment.LEFT, config.fontBold, tableFontSize);
        createBodyCell(row, 70, "", HorizontalAlignment.LEFT);
        createBodyCell(row, 15, "$" + app.getLodgingPerDiems().totalPerDiem().toString(),
                HorizontalAlignment.RIGHT, config.fontBold, tableFontSize);

        y = dataTable.draw();
        return dataTable.getCurrentPage();
    }

    private PDPage writeMileageExpenses(PDPage page) throws IOException {
        if (app.getMileagePerDiems().requestedPerDiems().isEmpty()) {
            return page;
        }

        BaseTable dataTable = new BaseTable(y, yStartNewPage, 35f, contentWidth, 35f, doc, page, true, true);
        dataTable.drawTitle("Mileage Expenses", config.fontBold, 14, contentWidth, 18f, "center", FREE_SPACE_FOR_PAGE_BREAK, true);
        Row<PDPage> headerRow = dataTable.createRow(16f);
        createHeaderCell(headerRow, 35, "From");
        createHeaderCell(headerRow, 35, "To");
        createHeaderCell(headerRow, 10, "Rate");
        createHeaderCell(headerRow, 10, "Miles");
        createHeaderCell(headerRow, 10, "Total");
        dataTable.addHeaderRow(headerRow);

        for (MileagePerDiem mpd : app.getMileagePerDiems().requestedPerDiems()) {
            Row<PDPage> row = dataTable.createRow(16f);
            createBodyCell(row, 35, mpd.getFrom().getFormattedAddressWithCounty(), HorizontalAlignment.LEFT);
            createBodyCell(row, 35, mpd.getTo().getFormattedAddressWithCounty(), HorizontalAlignment.LEFT);
            createBodyCell(row, 10, mpd.getMileageRate().toString(), HorizontalAlignment.LEFT);
            createBodyCell(row, 10, String.valueOf(mpd.getMiles()), HorizontalAlignment.RIGHT);
            createBodyCell(row, 10, "$" + mpd.requestedPerDiemValue().toString(), HorizontalAlignment.RIGHT);
        }

        // Total Row
        Row<PDPage> row = dataTable.createRow(16f);
        createBodyCell(row, 35, "Total", HorizontalAlignment.LEFT, config.fontBold, tableFontSize);
        createBodyCell(row, 35, "", HorizontalAlignment.LEFT);
        createBodyCell(row, 10, "", HorizontalAlignment.LEFT);
        createBodyCell(row, 10, String.valueOf(app.getMileagePerDiems().totalMileage()),
                HorizontalAlignment.RIGHT, config.fontBold, tableFontSize);
        createBodyCell(row, 10, "$" + app.getMileagePerDiems().totalPerDiemValue().toString(),
                HorizontalAlignment.RIGHT, config.fontBold, tableFontSize);

        y = dataTable.draw();
        return dataTable.getCurrentPage();
    }

    private void createHeaderCell(Row<PDPage> headerRow, float width, String value) {
        Cell<PDPage> cell = headerRow.createCell(width, value);
        cell.setAlign(HorizontalAlignment.CENTER);
        cell.setFillColor(Color.LIGHT_GRAY);
        cell.setFont(config.fontBold);
        cell.setFontSize(config.fontSize);
    }

    private void createBodyCell(Row<PDPage> row, float width, String value, HorizontalAlignment alignment) {
        this.createBodyCell(row, width, value, alignment, config.font, tableFontSize);
    }

    private void createBodyCell(Row<PDPage> row, float width, String value, HorizontalAlignment alignment, PDFont font, float fontSize) {
        Cell<PDPage> cell = row.createCell(width, value);
        cell.setFont(font);
        cell.setFontSize(fontSize);
        cell.setAlign(alignment);
    }

    @Override
    public PDPageContentStream getContentStream() {
        return null;
    }
}
