package gov.nysenate.ess.travel.api.application;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.util.LimitOffset;
import gov.nysenate.ess.core.util.PaginatedList;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.travel.api.application.statistics.TravelApplicationStatisticsUtil;
import gov.nysenate.ess.travel.api.application.statistics.TravelStatusCountDTO;
import gov.nysenate.ess.travel.api.application.statistics.TravelStatusCountView;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.request.attachment.Attachment;
import gov.nysenate.ess.travel.request.attachment.SqlAttachmentDao;
import gov.nysenate.ess.travel.request.app.*;
import gov.nysenate.ess.travel.report.pdf.TravelAppPdfGenerator;
import gov.nysenate.ess.travel.review.ApplicationReview;
import gov.nysenate.ess.travel.review.ApplicationReviewService;
import gov.nysenate.ess.travel.utils.AttachmentService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/travel")
public class TravelApplicationCtrl extends BaseRestApiCtrl {

    private static final Logger logger = LoggerFactory.getLogger(TravelApplicationCtrl.class);

    @Autowired private TravelApplicationService appService;
    @Autowired private ApplicationReviewService appReviewService;
    @Autowired private AttachmentService attachmentService;
    @Autowired private SqlAttachmentDao attachmentDao;

    @RequestMapping(value = "/applications/{appId}", method = RequestMethod.GET)
    public BaseResponse getTravelAppById(@PathVariable int appId) {
        TravelApplication app = appService.getTravelApplication(appId);
        checkTravelAppPermission(app, RequestMethod.GET);
        return new ViewObjectResponse<>(new TravelApplicationView(app));
    }

    @RequestMapping(value = "/applications/{appId}.pdf", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getAppPdf(@PathVariable int appId) throws IOException {
        ApplicationReview appReview = appReviewService.getApplicationReviewByAppId(appId);
        checkTravelAppPermission(appReview.application(), RequestMethod.GET);

        TravelAppPdfGenerator pdfGenerator = new TravelAppPdfGenerator(appReview);
        try (ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream()) {
            // Draw the watermark unless user has TRAVEL_ADMIN or SOS roles.
            boolean drawWatermark = !(getSubject().hasRole(TravelRole.TRAVEL_ADMIN.name()) || getSubject().hasRole(TravelRole.SECRETARY_OF_THE_SENATE.name()));
            pdfGenerator.write(pdfBytes, drawWatermark);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            return new ResponseEntity<>(pdfBytes.toByteArray(), headers, HttpStatus.OK);
        } catch (IOException ex) {
            logger.error("Error generating pdf for appId: " + appId, ex);
            throw ex;
        }
    }

    @RequestMapping(value = "/applications/statistics")
    public BaseResponse getTravelAppStatistics(
            @RequestParam("fromDate") String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        LocalDate fromLocalDate = parseISODate(fromDate, "from-date");
        LocalDateTime fromLocalDateTime = fromLocalDate.atStartOfDay();
        if (toDate == null || toDate.isEmpty()) {
            toDate = LocalDate.now().toString();
        }

        LocalDate toLocalDate = parseISODate(toDate, "to-date");
        LocalDateTime toLocalDateTime = toLocalDate.atStartOfDay();

        List<TravelApplication> apps = appService.selectAllTravelApplications(fromLocalDateTime, toLocalDateTime);

        List<TravelApplicationView> appViews = apps.stream()
                .map(TravelApplicationView::new)
                .collect(Collectors.toList());

        List<TravelStatusCountDTO> appStatuses = TravelApplicationStatisticsUtil.getTravelStatusCount(appViews);

        List<TravelStatusCountView> appStatsViews = appStatuses.stream()
                .map(TravelStatusCountView::new)
                .collect(Collectors.toList());
        return ListViewResponse.of(appStatsViews);
    }

    /**
     * Returns submitted travel applications where the current user is the traveler or submitter.
     * Results can be filtered by inclusive travel start-date bounds and by one or more statuses.
     * <p>
     * Usage:
     * {@code GET /api/v1/travel/applications}
     * <p>
     * Example:
     * <pre>
     * GET /api/v1/travel/applications?from=2026-01-01&to=2026-06-30&status=APPROVED&status=CANCELED&sort=startDate:desc&limit=16&offset=1
     * </pre>
     * <p>
     * Request parameters:
     * <ul>
     *   <li>{@code from} - optional ISO date; earliest travel start date, inclusive.</li>
     *   <li>{@code to} - optional ISO date; latest travel start date, inclusive.</li>
     *   <li>{@code status} - optional, repeatable {@link AppStatus}; omitted means all statuses.</li>
     *   <li>{@code sort} - optional {@code field:direction}. Fields: {@code startDate},
     *       {@code submittedDate}, {@code status}, {@code id}. Directions: {@code asc}, {@code desc}.
     *       Defaults to {@code startDate:desc}.</li>
     *   <li>{@code limit} - optional page size; defaults to 16.</li>
     *   <li>{@code offset} - optional one-indexed result offset; defaults to 1.</li>
     * </ul>
     *
     * @return matching application summaries and pagination metadata
     */
    @GetMapping(value = "/applications")
    public BaseResponse getActiveTravelApps(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(required = false) String[] status,
            @RequestParam(defaultValue = "startDate:desc") String sort,
            WebRequest request) {
        LocalDate fromDate = from == null ? null : parseISODate(from, "from");
        LocalDate toDate = to == null ? null : parseISODate(to, "to");
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new InvalidRequestParamEx(to, "to", "date", "Must be on or after from");
        }

        EnumSet<AppStatus> statuses = EnumSet.noneOf(AppStatus.class);
        if (status != null) {
            for (String statusValue : status) {
                statuses.add(getEnumParameter("status", statusValue, AppStatus.class));
            }
        }

        String[] sortParts = sort.split(":", -1);
        if (sortParts.length != 2) {
            throw new InvalidRequestParamEx(
                    sort, "sort", "sort", "Must use the format field:direction"
            );
        }
        TravelApplicationSortField sortField = getEnumParameterByValue(
                TravelApplicationSortField.class,
                TravelApplicationSortField::fromParameter,
                TravelApplicationSortField::parameterName,
                "sort",
                sortParts[0]
        );
        SortOrder sortOrder = getEnumParameter("sort", sortParts[1], SortOrder.class);
        if (sortOrder == SortOrder.NONE) {
            throw new InvalidRequestParamEx(sort, "sort", "sort", "Direction must be ASC or DESC");
        }

        LimitOffset limitOffset = getLimitOffset(request, 16);
        TravelApplicationQuery query = new TravelApplicationQuery(
                fromDate, toDate, statuses, sortField, sortOrder, limitOffset
        );
        PaginatedList<TravelApplication> apps =
                appService.selectTravelApplications(getSubjectEmployeeId(), query);
        return ListViewResponse.fromPaginatedList(apps, TravelApplicationSummaryView::new);
    }

    @RequestMapping(value = "/applications/attachment/{uuid}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getAttachment(@PathVariable String uuid) throws IOException {
        Attachment attachment = attachmentDao.selectAttachment(uuid);
        File attachmentFile = attachmentService.getAttachmentFile(uuid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(attachment.getContentType()));
        byte[] bytes = FileUtils.readFileToByteArray(attachmentFile);
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
