package gov.nysenate.ess.core.client.view.acknowledgement;

import gov.nysenate.ess.core.model.acknowledgement.AckDoc;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.IOException;

public class DetailedAckDocView extends AckDocView{

    private static final String ackDocDir = "ack_docs";
    private int pageCount;
    private float width;
    private float height;

    public DetailedAckDocView(AckDoc ackDoc, String dataDir) throws IOException {
        super(ackDoc,dataDir);
        // requires full path
        PDDocument doc = PDDocument.load( new File(dataDir + "/" + ackDocDir + "/" + ackDoc.getFilename()));
        pageCount = doc.getNumberOfPages();
        width = doc.getPage(0).getMediaBox().getWidth();
        height = 0;
        //max width, cumulative height
        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            if (doc.getPage(i).getMediaBox().getWidth() > width) {
                width = doc.getPage(i).getMediaBox().getWidth();
            }
            height += doc.getPage(i).getMediaBox().getHeight();
        }
        if( doc != null )
        {
            doc.close();
        }
    }

    @Override
    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
