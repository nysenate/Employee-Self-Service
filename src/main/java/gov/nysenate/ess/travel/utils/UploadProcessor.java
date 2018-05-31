package gov.nysenate.ess.travel.utils;

import gov.nysenate.ess.travel.application.TravelAttachment;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UploadProcessor {

    private File uploadDir;

    public UploadProcessor(@Value("${data.dir}") String dataDir,
                           @Value("${data.travel.attachments.dir}") String travelDir) throws IOException {
        this.uploadDir = new File(dataDir + travelDir);
        FileUtils.forceMkdir(this.uploadDir);
    }

    /**
     * Saves an uploaded file.
     * File is saved with a random name in the 'data.travel.attachments.dir' directory.
     * @param upload
     * @return A {@link TravelAttachment} containing metadata about the file.
     * @throws IOException
     */
    public TravelAttachment uploadTravelAttachment(MultipartFile upload) throws IOException {
        String attachmentId = UUID.randomUUID().toString();
        String originalName = upload.getOriginalFilename();
        String contentType = upload.getContentType();

        File attachment = new File(getUploadPath() + attachmentId);
        upload.transferTo(attachment);

        return new TravelAttachment(attachmentId, originalName, contentType);
    }

    public File getAttachmentFile(String attachmentId) {
        return new File(getUploadPath() + attachmentId);
    }

    private String getUploadPath() {
        if (getUploadDir().getPath().endsWith("/")) {
            return getUploadDir().getPath();
        }
        else {
            return getUploadDir().getPath() + "/";
        }
    }

    private File getUploadDir() {
        return uploadDir;
    }
}
