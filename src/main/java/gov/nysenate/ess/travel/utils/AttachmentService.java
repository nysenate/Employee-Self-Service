package gov.nysenate.ess.travel.utils;

import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.attachment.SqlAttachmentDao;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class AttachmentService {

    private File uploadDir;
    private SqlAttachmentDao attachmentDao;

    @Autowired
    public AttachmentService(@Value("${data.dir}") String dataDir,
                             @Value("${data.travel.attachments.dir}") String travelDir,
                             SqlAttachmentDao attachmentDao) throws IOException {
        this.uploadDir = new File(dataDir + travelDir);
        FileUtils.forceMkdir(this.uploadDir);
        this.attachmentDao = attachmentDao;
    }

    /**
     * Saves an uploaded file to disk and the database.
     * Saved with filename = to the attachment's UUID in the 'data.travel.attachments.dir' directory.
     * @param upload
     * @return A {@link Attachment} containing metadata about the file.
     */
    public Attachment uploadAttachment(MultipartFile upload) throws IOException {
        UUID attachmentId = UUID.randomUUID();
        String originalName = upload.getOriginalFilename();
        String contentType = upload.getContentType();

        // Save attachment to disk
        File attachmentFile = new File(getUploadPath() + attachmentId);
        upload.transferTo(attachmentFile);

        Attachment attachment = new Attachment(attachmentId, originalName, contentType);
        // Save attachment to database.
        attachmentDao.saveAttachment(attachment);
        return attachment;
    }

    /**
     * Returns a File representing this attachment on disk.
     * @param attachmentId The UUID of the attachment.
     */
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
