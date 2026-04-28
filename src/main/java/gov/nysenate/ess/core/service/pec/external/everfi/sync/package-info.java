/**
 * Everfi user sync pipeline. Keeps the set of active Everfi users aligned with the employees we have
 * locally, so PEC training can be assigned to the right people without manual upkeep.
 *
 * <h2>Pipeline stages</h2>
 * <ol>
 *   <li><b>Load</b> ({@link gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncLoader}) —
 *       bootstrap any missing Everfi Department category labels, then fetch desired users from local employee data
 *       and remote users from Everfi, enriched with our mapping table.</li>
 *   <li><b>Plan</b> ({@link gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncPlanner}) —
 *       classify each employee/remote into a {@link gov.nysenate.ess.core.service.pec.external.everfi.sync.PlannedAction}.
 *       The planner is the home of the business rules; the rest of the pipeline is mechanical.</li>
 *   <li><b>Execute</b> ({@link gov.nysenate.ess.core.service.pec.external.everfi.sync.EverfiUserSyncExecutor}) —
 *       carry out planned actions against the Everfi API and the local mapping table. Honours dry-run.</li>
 *   <li><b>Report</b> ({@link gov.nysenate.ess.core.service.pec.external.everfi.sync.SyncReportRenderer}) —
 *       render a human-readable summary; emailed to PEC admins after each scheduled run.</li>
 * </ol>
 *
 * <h2>Ubiquitous Language</h2>
 *
 * <h3>Core concepts</h3>
 * <ul>
 *   <li><b>Desired user</b> — a local employee who should be active in Everfi. Derived from SFMS data.
 *       Represents the intended end-state, not any existing remote record.</li>
 *   <li><b>Remote user</b> — a snapshot of an Everfi user as returned by the Everfi API, enriched
 *       with our mapping if one exists. Represents current state in the remote system.</li>
 *   <li><b>Mapping</b> — a persisted link between a local employee ID and an Everfi user UUID.
 *       Created when a new Everfi user is successfully provisioned. The presence of a mapping is
 *       what makes a remote user authoritative — without one, the association is only inferred.</li>
 * </ul>
 *
 * <h3>Remote user classification (identity confidence)</h3>
 * <p>Every remote Everfi user falls into exactly one category based on how reliably
 * we can associate it with a local employee:
 * <ul>
 *   <li><b>Authoritative</b> — has a formal mapping entry in our database. One per employee ID.
 *       This is the trusted source of truth for which remote user represents a given employee.</li>
 *   <li><b>Candidate</b> — no mapping, but carries a remote employee ID that matches a local employee.
 *       Probably the right user, but unconfirmed. Requires human review before acting.</li>
 *   <li><b>Unidentifiable</b> — neither a mapping nor a remote employee ID. Cannot be associated
 *       with any local employee.</li>
 * </ul>
 *
 * <h3>Desired-remote alignment (sync perspective)</h3>
 * <p>The planner also reasons about whether each side of the sync has a counterpart:
 * <ul>
 *   <li><b>Resolved desired user</b> — a {@link gov.nysenate.ess.core.service.pec.external.everfi.sync.DesiredUser}
 *       that has an authoritative remote. Normal sync actions (SKIP, UPDATE, REACTIVATE) apply.</li>
 *   <li><b>Unresolved desired user</b> — a {@link gov.nysenate.ess.core.service.pec.external.everfi.sync.DesiredUser}
 *       with no authoritative remote. Either flagged (candidates exist) or created (no remote at all).</li>
 *   <li><b>Orphaned remote</b> — a remote user (authoritative, candidate, or unidentifiable) with no
 *       corresponding desired user. The action depends on identity confidence and active state:
 *       <ul>
 *         <li>Authoritative + active → DEACTIVATE (we provisioned this user and own its lifecycle)</li>
 *         <li>Authoritative + inactive → SKIP</li>
 *         <li>Candidate or unidentifiable + active → FLAG (may have been created outside this sync; requires human review)</li>
 *         <li>Candidate or unidentifiable + inactive → SKIP</li>
 *       </ul>
 *   </li>
 * </ul>
 */
package gov.nysenate.ess.core.service.pec.external.everfi.sync;
