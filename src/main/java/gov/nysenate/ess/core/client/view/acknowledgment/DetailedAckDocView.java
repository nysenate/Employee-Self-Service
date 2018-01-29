package gov.nysenate.ess.core.client.view.acknowledgment;

import gov.nysenate.ess.core.model.acknowledgment.AckDoc;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.bind.annotation.XmlElement;
import java.io.File;
import java.io.IOException;

public class DetailedAckDocView extends AckDocView{

    private int pageCount;
    private float maxWidth;
    private float totalHeight;

    public DetailedAckDocView(AckDoc ackDoc, String ackDocResPath, String ackDocDir) throws IOException {
        super(ackDoc, ackDocResPath);
        // requires full path
        PDDocument doc = PDDocument.load(new File(ackDocDir + ackDoc.getFilename()));
        pageCount = doc.getNumberOfPages();
        maxWidth = 0;
        totalHeight = 0;
        //max width, cumulative height
        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            if (doc.getPage(i).getMediaBox().getWidth() > maxWidth) {
                maxWidth = doc.getPage(i).getMediaBox().getWidth();
            }
            totalHeight += doc.getPage(i).getMediaBox().getHeight();
        }
        doc.close();
    }

    @Override
    public String getViewType() {
        return super.getViewType() + "-detailed";
    }

    @XmlElement
    public int getPageCount() {
        return pageCount;
    }

    @XmlElement
    public float getMaxWidth() {
        return maxWidth;
    }

    @XmlElement
    public float getTotalHeight() {
        return totalHeight;
    }
}
