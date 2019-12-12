package gov.nysenate.ess.travel.report.pdf;

import com.google.common.base.Preconditions;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.time.format.DateTimeFormatter;

public class PdfConfig {

    public final PDFont font;
    public final PDFont fontBold;
    public final float fontSize;
    public final float h1FontSize;
    public final float h2FontSize;
    public final float leadingRatio;
    public final DateTimeFormatter dateFormat;

    public PdfConfig(PDFont font, PDFont fontBold, float fontSize, float h1_FontSize, float h2_FontSize,
                     float leadingRatio, DateTimeFormatter dateFormat) {
        this.font = Preconditions.checkNotNull(font);
        this.fontBold = Preconditions.checkNotNull(fontBold);
        this.fontSize = fontSize;
        h1FontSize = h1_FontSize;
        h2FontSize = h2_FontSize;
        this.leadingRatio = leadingRatio;
        this.dateFormat = Preconditions.checkNotNull(dateFormat);
    }
}
