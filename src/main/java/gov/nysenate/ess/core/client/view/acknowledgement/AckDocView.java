package gov.nysenate.ess.core.client.view.acknowledgement;

import gov.nysenate.ess.core.client.view.base.ViewObject;
import gov.nysenate.ess.core.model.acknowledgement.AckDoc;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

@XmlRootElement
public class AckDocView implements ViewObject {

    private static final String ackDocDir = "ack_docs";
    private static final String ackDocUri = "/assets/ack_docs/";

    // Hidden fields
    private String dataDir;
    private String filename;

    private String title;
    private String path;
    private Boolean active;
    private Integer id;
    private LocalDateTime effectiveDateTime;

    protected AckDocView() {
    }

    public AckDocView(AckDoc ackDoc, String dataDir) {
        this.dataDir = dataDir;
        this.filename = ackDoc.getFilename();
        this.title = ackDoc.getTitle();
        this.path = ackDocUri + ackDoc.getFilename();
        this.active = ackDoc.getActive();
        this.id = ackDoc.getId();
        this.effectiveDateTime = ackDoc.getEffectiveDateTime();
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    @XmlElement
    public String getPath() {
        return path;
    }

    @XmlElement
    public Boolean getActive() {
        return active;
    }

    @XmlElement
    public Integer getId() {
        return id;
    }

    @XmlElement
    public LocalDateTime getEffectiveDateTime() {
        return effectiveDateTime;
    }

    @Override
    @XmlElement
    public String getViewType() {
        return "ack_doc";
    }

    public int getPageCount() throws IOException {
        PDDocument doc = PDDocument.load( new File(dataDir + "/" + ackDocDir + "/" + filename)); // requires full path
        int numOfPages = doc.getNumberOfPages();
        if( doc != null )
        {
            doc.close();
        }
        return numOfPages;
    }

    public float[] getDimensions() throws IOException {
        //max width, cumulative height
        PDDocument doc = PDDocument.load( new File(dataDir + "/" + ackDocDir + "/" + filename)); // requires full path
        float width = doc.getPage(0).getMediaBox().getWidth();
        float height = 0;
        for (int i = 0; i < doc.getNumberOfPages(); i++) {
            if (doc.getPage(i).getMediaBox().getWidth() > width) {
                width = doc.getPage(i).getMediaBox().getWidth();
            }
            height += doc.getPage(i).getMediaBox().getHeight();
        }
        float[] dimensions = new float[2];
        dimensions[0] = width;
        dimensions[1] = height;
        if( doc != null )
        {
            doc.close();
        }
        return dimensions;
    }
}
