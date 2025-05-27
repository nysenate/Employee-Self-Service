package gov.nysenate.ess.travel.report.pdf;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;

public class AppPdfEmployeeInfoWriter implements AppPdfWriter {

    private final PdfConfig config;
    private final PDPageContentStream cs;
    // X and Y coordinates representing the top left of where we will start writing.
    private final float x, y;
    private final TravelApplication app;

    public AppPdfEmployeeInfoWriter(PdfConfig config, PDPageContentStream cs, float x, float y, TravelApplication app) {
        this.config = Preconditions.checkNotNull(config);
        this.cs = Preconditions.checkNotNull(cs);
        this.x = x;
        this.y = y;
        this.app = Preconditions.checkNotNull(app);
    }

    /**
     * Writes Employee data to the PDPageContentStream.
     * @return The last Y position written to.
     * @throws IOException
     */
    @Override
    public float write() throws IOException {
        float currentY = y;
        final float leading = config.leadingRatio * config.fontSize;
        final float column1LabelX = this.x;
        final float column1DataX = column1LabelX + 90f;
        final float column2LabelX = column1LabelX + 375f;
        final float column2DataX = column2LabelX + 90f;

        // Row 1
        drawText(column1LabelX, currentY, config.fontBold, config.fontSize, "Date:");
        drawText(column1DataX, currentY, config.font, config.fontSize, app.getSubmittedDateTime().format(config.dateFormat));
        drawText(column2LabelX, currentY, config.fontBold, config.fontSize, "NYS EMPLID#:");
        drawText(column2DataX, currentY, config.font, config.fontSize, getNidSafe(app.getTraveler()));

        // Row 2
        currentY -= leading;
        drawText(column1LabelX, currentY, config.fontBold, config.fontSize, "Name/Title:");
        drawText(column1DataX, currentY, config.font, config.fontSize, getNameTitleSafe(app.getTraveler()));
        drawText(column2LabelX, currentY, config.fontBold, config.fontSize, "Phone:");
        drawText(column2DataX, currentY, config.font, config.fontSize, app.getTraveler().getWorkPhone());

        // Row 3
        currentY -= leading;
        drawText(column1LabelX, currentY, config.fontBold, config.fontSize, "Office:");
        drawText(column1DataX, currentY, config.font, config.fontSize, getOfficeSafe(app.getTraveler()));
        drawText(column2LabelX, currentY, config.fontBold, config.fontSize, "Agency Code:");
        drawText(column2DataX, currentY, config.font, config.fontSize, getAgencySafe(app.getTraveler()));

        // row 4
        currentY -= leading;
        drawText(column1LabelX, currentY, config.fontBold, config.fontSize, "Office Address:");
        drawText(column1DataX, currentY, config.font, config.fontSize, getOfficeAddressSafe(app.getTraveler()));

        return currentY;
    }

    @Override
    public PDPageContentStream getContentStream() {
        return this.cs;
    }

    private String getNidSafe(Employee emp) {
        return StringUtils.isBlank(emp.getNid()) ? "N/A" : emp.getNid();
    }

    /**
     * Get the employees name and title, return N/A for any parts that encounter nulls.
     * @param emp
     * @return
     */
    private String getNameTitleSafe(Employee emp) {
        String fullName = StringUtils.isBlank(emp.getFullName()) ? "N/A" : emp.getFullName();
        String title = StringUtils.isBlank(emp.getJobTitle()) ? "N/A" : emp.getJobTitle();
        return fullName + " - " + title;
    }

    /**
     * Get the employees office, returns N/A if any null values are encountered.
     * @param emp
     * @return
     */
    private String getOfficeSafe(Employee emp) {
        try {
            return emp.getRespCenter().getHead().getName();
        } catch (NullPointerException ex) {
            return "N/A";
        }
    }

    /**
     * Get the employees agency, returns N/A if any null values are encountered.
     * @param emp
     * @return
     */
    private String getAgencySafe(Employee emp) {
        try {
            return emp.getRespCenter().getAgency().getCode();
        } catch (NullPointerException ex) {
            return "N/A";
        }
    }

    /**
     * Get the employees office address, returns N/A if any null values are encountered.
     * @param emp
     * @return
     */
    private String getOfficeAddressSafe(Employee emp) {
        try {
            return emp.getWorkLocation().getAddress().getFormattedAddressWithCounty();
        } catch (NullPointerException ex) {
            return "N/A";
        }
    }
}
