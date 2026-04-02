package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.service.pec.external.everfi.category.EverfiCategoryLabel;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Renders a {@link SyncRun} as a plain-text admin email. One section per action type, plus a flagged
 * section for issues that need human follow-up. {@code detailed} mode adds a per-record diff showing
 * exactly which fields/labels would change.
 */
public class EverfiUserSyncReport {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String DIVIDER = "=".repeat(60);
    private static final String SUB_DIVIDER = "-".repeat(40);

    private final SyncRun run;
    private final boolean detailed;

    public EverfiUserSyncReport(SyncRun run) {
        this(run, false);
    }

    public EverfiUserSyncReport(SyncRun run, boolean detailed) {
        this.run = run;
        this.detailed = detailed;
    }

    public String generate() {
        return generateText();
    }

    public String generateText() {
        var sb = new StringBuilder();
        appendHeader(sb);
        appendSummary(sb);
        appendReactivateSection(sb);
        appendCreateSection(sb);
        appendDeactivateSection(sb);
        appendSkippedIssueSection(sb);
        appendUpdateSection(sb);
        appendFlaggedSection(sb);
        return sb.toString();
    }

    public String generateHtml() {
        return """
                <html>
                <body style="margin:0; padding:16px; font-family: Arial, sans-serif;">
                <pre style="font-family: Consolas, Menlo, Monaco, 'Courier New', monospace; font-size: 13px; line-height: 1.4; white-space: pre-wrap; margin: 0;">%s</pre>
                </body>
                </html>
                """.formatted(escapeHtml(generateText()));
    }

    private void appendHeader(StringBuilder sb) {
        sb.append(DIVIDER).append("\n");
        sb.append("EVERFI USER SYNC REPORT\n");
        sb.append(DATE_FORMAT.format(run.ranAt()))
                .append("  |  Mode: ")
                .append(run.dryRun() ? "DRY RUN" : "LIVE")
                .append("\n");
        sb.append(DIVIDER).append("\n\n");
    }

    private void appendSummary(StringBuilder sb) {
        sb.append("SUMMARY\n");
        sb.append(SUB_DIVIDER).append("\n");
        appendSummaryRow(sb, "Skip", SyncAction.SKIP);
        appendSummaryRow(sb, "Update", SyncAction.UPDATE);
        appendSummaryRow(sb, "Deactivate", SyncAction.DEACTIVATE);
        appendSummaryRow(sb, "Create", SyncAction.CREATE);
        appendSummaryRow(sb, "Reactivate", SyncAction.REACTIVATE);
        appendSummaryRow(sb, "Flag", SyncAction.FLAG);
        sb.append(SUB_DIVIDER).append("\n");
        sb.append(String.format("  %-12s %4d%n", "Total", run.results().size()));
        sb.append("\n");
    }

    private void appendSummaryRow(StringBuilder sb, String label, SyncAction syncAction) {
        var typeResults = filterByAction(syncAction);
        if (typeResults.isEmpty()) return;
        long errors = countOutcome(typeResults, SyncOutcome.ERROR);
        sb.append(String.format("  %-12s %4d", label, typeResults.size()));
        if (errors > 0) {
            sb.append(String.format("  (%d error%s)", errors, errors == 1 ? "" : "s"));
        }
        sb.append("\n");
    }

    private void appendUpdateSection(StringBuilder sb) {
        var updates = filterByAction(SyncAction.UPDATE);
        if (updates.isEmpty()) return;

        var successes = updates.stream().filter(r -> r.outcome() == SyncOutcome.SUCCESS).toList();
        var errors = updates.stream().filter(r -> r.outcome() == SyncOutcome.ERROR).toList();

        sb.append(DIVIDER).append("\n");
        sb.append(String.format("UPDATED USERS  (%d succeeded, %d error%s)%n",
                successes.size(), errors.size(), errors.size() == 1 ? "" : "s"));
        sb.append(DIVIDER).append("\n");

        if (!successes.isEmpty()) {
            sb.append(String.format("  %-8s  %-28s  %s%n", "Emp ID", "Name", "Email"));
            sb.append(String.format("  %-8s  %-28s  %s%n", "------", "----", "-----"));
            for (var result : successes) {
                var d = result.action().requireDesired();
                sb.append(String.format("  %-8d  %-28s  %s%n",
                        d.employeeId(),
                        formatName(d.firstName(), d.lastName()),
                        nullToFallback(d.email(), "(no email)")));
                if (detailed) appendFieldDiffs(sb, result.action());
            }
        }

        if (!errors.isEmpty()) {
            sb.append("\n  ERRORS:\n");
            for (var result : errors) {
                var d = result.action().requireDesired();
                sb.append(String.format("  [%d] %s -- %s%n",
                        d.employeeId(),
                        formatName(d.firstName(), d.lastName()),
                        result.message()));
                if (detailed) appendFieldDiffs(sb, result.action());
            }
        }
        sb.append("\n");
    }

    private void appendCreateSection(StringBuilder sb) {
        var creates = filterByAction(SyncAction.CREATE);
        if (creates.isEmpty()) return;

        var successes = creates.stream().filter(r -> r.outcome() == SyncOutcome.SUCCESS).toList();
        var errors = creates.stream().filter(r -> r.outcome() == SyncOutcome.ERROR).toList();

        sb.append(DIVIDER).append("\n");
        sb.append(String.format("CREATED USERS  (%d succeeded, %d error%s)%n",
                successes.size(), errors.size(), errors.size() == 1 ? "" : "s"));
        sb.append(DIVIDER).append("\n");

        if (!successes.isEmpty()) {
            sb.append(String.format("  %-8s  %-28s  %s%n", "Emp ID", "Name", "Email"));
            sb.append(String.format("  %-8s  %-28s  %s%n", "------", "----", "-----"));
            for (var result : successes) {
                var d = result.action().requireDesired();
                sb.append(String.format("  %-8d  %-28s  %s%n",
                        d.employeeId(),
                        formatName(d.firstName(), d.lastName()),
                        nullToFallback(d.email(), "(no email)")));
                if (detailed) appendFieldDiffs(sb, result.action());
            }
        }

        if (!errors.isEmpty()) {
            sb.append("\n  ERRORS:\n");
            for (var result : errors) {
                var d = result.action().requireDesired();
                sb.append(String.format("  [%d] %s -- %s%n",
                        d.employeeId(),
                        formatName(d.firstName(), d.lastName()),
                        result.message()));
                if (detailed) appendFieldDiffs(sb, result.action());
            }
        }
        sb.append("\n");
    }

    private void appendDeactivateSection(StringBuilder sb) {
        var deactivates = filterByAction(SyncAction.DEACTIVATE);
        if (deactivates.isEmpty()) return;

        var successes = deactivates.stream().filter(r -> r.outcome() == SyncOutcome.SUCCESS).toList();
        var errors = deactivates.stream().filter(r -> r.outcome() == SyncOutcome.ERROR).toList();

        sb.append(DIVIDER).append("\n");
        sb.append(String.format("DEACTIVATED USERS  (%d succeeded, %d error%s)%n",
                successes.size(), errors.size(), errors.size() == 1 ? "" : "s"));
        sb.append(DIVIDER).append("\n");

        if (!successes.isEmpty()) {
            sb.append(String.format("  %-8s  %-28s  %s%n", "Emp ID", "Name", "Email"));
            sb.append(String.format("  %-8s  %-28s  %s%n", "------", "----", "-----"));
            for (var result : successes) {
                var remote = result.action().requireRemote();
                sb.append(String.format("  %-8s  %-28s  %s%n",
                        resolveEmpId(result.action()),
                        formatName(remote.remoteFirstName(), remote.remoteLastName()),
                        nullToFallback(remote.remoteEmail(), "(no email)")));
            }
        }

        if (!errors.isEmpty()) {
            sb.append("\n  ERRORS:\n");
            for (var result : errors) {
                sb.append(String.format("  [%s] %s -- %s%n",
                        resolveEmpId(result.action()),
                        resolveName(result.action()),
                        result.message()));
            }
        }
        sb.append("\n");
    }

    private void appendReactivateSection(StringBuilder sb) {
        var reactivates = filterByAction(SyncAction.REACTIVATE);
        if (reactivates.isEmpty()) return;

        var successes = reactivates.stream().filter(r -> r.outcome() == SyncOutcome.SUCCESS).toList();
        var errors = reactivates.stream().filter(r -> r.outcome() == SyncOutcome.ERROR).toList();

        sb.append(DIVIDER).append("\n");
        sb.append(String.format("REACTIVATED USERS  (%d succeeded, %d error%s)%n",
                successes.size(), errors.size(), errors.size() == 1 ? "" : "s"));
        sb.append(DIVIDER).append("\n");

        if (!successes.isEmpty()) {
            sb.append(String.format("  %-8s  %-28s  %s%n", "Emp ID", "Name", "Email"));
            sb.append(String.format("  %-8s  %-28s  %s%n", "------", "----", "-----"));
            for (var result : successes) {
                var d = result.action().requireDesired();
                sb.append(String.format("  %-8d  %-28s  %s%n",
                        d.employeeId(),
                        formatName(d.firstName(), d.lastName()),
                        nullToFallback(d.email(), "(no email)")));
                if (detailed) appendFieldDiffs(sb, result.action());
            }
        }

        if (!errors.isEmpty()) {
            sb.append("\n  ERRORS:\n");
            for (var result : errors) {
                var d = result.action().requireDesired();
                sb.append(String.format("  [%d] %s -- %s%n",
                        d.employeeId(),
                        formatName(d.firstName(), d.lastName()),
                        result.message()));
                if (detailed) appendFieldDiffs(sb, result.action());
            }
        }
        sb.append("\n");
    }

    private void appendSkippedIssueSection(StringBuilder sb) {
        var skippedWithIssues = filterByAction(SyncAction.SKIP).stream()
                .filter(result -> !result.action().issues().isEmpty())
                .toList();
        if (skippedWithIssues.isEmpty()) return;

        sb.append(DIVIDER).append("\n");
        sb.append(String.format("SKIPPED USERS WITH ISSUES  (%d)%n", skippedWithIssues.size()));
        sb.append(DIVIDER).append("\n");
        sb.append(String.format("  %-8s  %-28s  %-18s  %s%n", "Emp ID", "Name", "Status", "Reason"));
        sb.append(String.format("  %-8s  %-28s  %-18s  %s%n", "------", "----", "------", "------"));

        for (var result : skippedWithIssues) {
            var action = result.action();
            sb.append(String.format("  %-8s  %-28s  %-18s  %s%n",
                    resolveEmpId(action),
                    resolveName(action),
                    describeSkippedStatus(action),
                    describeIssues(action)));
        }
        sb.append("\n");
    }

    /**
     * Appends indented lines showing which fields will change (UPDATE/REACTIVATE)
     * or which labels will be assigned (CREATE).
     */
    private void appendFieldDiffs(StringBuilder sb, PlannedAction action) {
        DesiredUser desired = action.desired();
        RemoteUser remote = action.remote();

        if (remote == null) {
            // CREATE: name/email are already in the table row; only show labels if present
            if (!desired.categoryLabels().isEmpty()) {
                sb.append(String.format("    %-12s  %s%n", "labels:", formatLabelNames(desired.categoryLabels())));
            }
            return;
        }

        // UPDATE / REACTIVATE: emit a line for each field that differs
        if (!remote.remoteActive()) {
            sb.append(String.format("    %-12s  false -> true%n", "active:"));
        }
        if (!Objects.equals(remote.remoteEmployeeId(), desired.employeeId())) {
            sb.append(String.format("    %-12s  %s -> %s%n", "emp_id:",
                    remote.remoteEmployeeId(), desired.employeeId()));
        }
        if (!Objects.equals(remote.remoteFirstName(), desired.firstName())) {
            sb.append(String.format("    %-12s  %s -> %s%n", "first_name:",
                    quote(remote.remoteFirstName()), quote(desired.firstName())));
        }
        if (!Objects.equals(remote.remoteLastName(), desired.lastName())) {
            sb.append(String.format("    %-12s  %s -> %s%n", "last_name:",
                    quote(remote.remoteLastName()), quote(desired.lastName())));
        }
        if (!remote.hasPersonalEmailOverride() && !Objects.equals(remote.remoteEmail(), desired.email())) {
            sb.append(String.format("    %-12s  %s -> %s%n", "email:",
                    quote(remote.remoteEmail()), quote(desired.email())));
        }
        if (!desired.categoryLabels().isEmpty()) {
            Set<Integer> remoteLabelIds = remote.categoryLabels().stream()
                    .map(EverfiCategoryLabel::getLabelId)
                    .collect(Collectors.toSet());
            List<EverfiCategoryLabel> missing = desired.categoryLabels().stream()
                    .filter(l -> !remoteLabelIds.contains(l.getLabelId()))
                    .toList();
            if (!missing.isEmpty()) {
                sb.append(String.format("    %-12s  %s%n", "+labels:", formatLabelNames(missing)));
            }
        }
    }

    private static String quote(String value) {
        return value == null ? "(null)" : "\"" + value + "\"";
    }

    private static String formatLabelNames(List<EverfiCategoryLabel> labels) {
        return labels.stream()
                .map(l -> {
                    String labelName = l.getLabelName() != null ? l.getLabelName() : String.valueOf(l.getLabelId());
                    return l.getCategoryName() != null ? l.getCategoryName() + ": " + labelName : labelName;
                })
                .collect(Collectors.joining(", "));
    }

    private void appendFlaggedSection(StringBuilder sb) {
        var flagged = filterByAction(SyncAction.FLAG);
        if (flagged.isEmpty()) return;

        sb.append(DIVIDER).append("\n");
        sb.append(String.format("FLAGGED USERS  (%d)  -- REQUIRE MANUAL REVIEW%n", flagged.size()));
        sb.append(DIVIDER).append("\n");
        sb.append(String.format("  %-8s  %-28s  %s%n", "Emp ID", "Name", "Issues"));
        sb.append(String.format("  %-8s  %-28s  %s%n", "------", "----", "------"));

        for (var result : flagged) {
            var action = result.action();
            sb.append(String.format("  %-8s  %-28s  %s%n",
                    resolveEmpId(action),
                    resolveName(action),
                    describeIssues(action)));
        }
        sb.append("\n");
    }

    private String describeSkippedStatus(PlannedAction action) {
        if (action.desired() == null) {
            return "Skipped";
        }
        if (action.remote() == null) {
            return "CREATE blocked";
        }
        if (!action.remote().remoteActive()) {
            return "REACTIVATE blocked";
        }
        return "UPDATE blocked";
    }

    private String describeIssues(PlannedAction action) {
        return action.issues().stream()
                .map(this::describeIssue)
                .collect(Collectors.joining(", "));
    }

    private String describeIssue(SyncIssue issue) {
        return switch (issue) {
            case MISSING_EMAIL -> "Missing local email";
            case DUPLICATE_REMOTE_EMP_ID -> "Multiple Everfi users share this employee ID";
            case MAPPING_WITHOUT_REMOTE_USER -> "Existing ESS mapping points to a missing Everfi user";
            case UNMAPPED_REMOTE_USER -> "Matching Everfi user exists without an ESS mapping";
            case UNRECOGNIZED_ACTIVE_REMOTE -> "Active Everfi user has no ESS match or mapping";
            case MAPPING_EMPLOYEE_ID_MISMATCH -> "Mapped Everfi user has a different employee ID than ESS expects";
        };
    }

    private String resolveEmpId(PlannedAction action) {
        if (action.desired() != null) {
            return String.valueOf(action.desired().employeeId());
        }
        if (action.remote() != null && action.remote().remoteEmployeeId() != null) {
            return String.valueOf(action.remote().remoteEmployeeId());
        }
        return "(unknown)";
    }

    private String resolveName(PlannedAction action) {
        if (action.desired() != null) {
            return formatName(action.desired().firstName(), action.desired().lastName());
        }
        if (action.remote() != null) {
            return formatName(action.remote().remoteFirstName(), action.remote().remoteLastName());
        }
        return "(unknown)";
    }

    private List<SyncResult> filterByAction(SyncAction syncAction) {
        return run.results().stream()
                .filter(r -> r.action().action() == syncAction)
                .toList();
    }

    private long countOutcome(List<SyncResult> results, SyncOutcome outcome) {
        return results.stream().filter(r -> r.outcome() == outcome).count();
    }

    private static String formatName(String firstName, String lastName) {
        if (firstName == null && lastName == null) return "(unknown)";
        if (firstName == null) return lastName;
        if (lastName == null) return firstName;
        return lastName + ", " + firstName;
    }

    private static String nullToFallback(String value, String fallback) {
        return value != null ? value : fallback;
    }

    private static String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
