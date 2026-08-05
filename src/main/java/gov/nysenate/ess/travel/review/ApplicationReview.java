package gov.nysenate.ess.travel.review;

import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.policy.ReviewPolicy;
import gov.nysenate.ess.travel.review.policy.ReviewPolicyType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * The entire review process for a single {@link TravelApplication}.
 */
public class ApplicationReview {
    private final static Comparator<Action> actionComparator = Comparator.comparing(Action::dateTime);

    private int appReviewId;
    private final TravelApplication application;
    private final SortedSet<Action> actions;
    private final ReviewPolicy policy;
    private TravelRole pendingReviewerRole;
    private boolean isShared;

    public ApplicationReview(int appReviewId, TravelApplication application, ReviewPolicy policy,
                             TravelRole pendingReviewerRole, List<Action> actions, boolean isShared) {
        this.appReviewId = appReviewId;
        this.application = application;
        this.actions = new TreeSet<>(actionComparator);
        this.actions.addAll(actions);
        this.policy = policy;
        if (!policy.reviewerOrder().contains(pendingReviewerRole)) {
            throw new IllegalArgumentException("Pending reviewer role must belong to the review policy.");
        }
        this.pendingReviewerRole = pendingReviewerRole;
        this.isShared = isShared;
    }

    public ApplicationReview(TravelApplication application, ReviewPolicy policy) {
        this(0, application, policy, policy.firstReviewer(), List.of(), false);
    }

    public void addAction(Action action) {
        if (action.role() != pendingReviewerRole) {
            throw new IllegalArgumentException("Action role must match the pending reviewer role.");
        }
        actions.add(action);
        pendingReviewerRole = action.isDisapproval()
                ? TravelRole.NONE
                : policy.after(pendingReviewerRole);
    }

    public void restart() {
        pendingReviewerRole = policy.firstReviewer();
    }

    public TravelRole pendingReviewerRole() {
        return pendingReviewerRole;
    }

    public Action getLatestActionByRole(TravelRole role) {
        List<Action> actionsByRole = actions.stream()
                .filter(a -> a.role() == role)
                .collect(Collectors.toList());
        if (actionsByRole.isEmpty()) {
            return null;
        }
        return actionsByRole.get(actionsByRole.size() - 1);
    }

    public TravelApplication application() {
        return application;
    }

    public SortedSet<Action> actions() {
        return actions;
    }

    public Action lastAction() {
        return actions.last();
    }

    public ReviewPolicyType policyType() {
        return policy.id().type();
    }

    public int policyVersion() {
        return policy.id().version();
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean isShared) {
        this.isShared = isShared;
    }

    public int getAppReviewId() {
        return appReviewId;
    }

    void setAppReviewId(int appReviewId) {
        this.appReviewId = appReviewId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationReview that = (ApplicationReview) o;
        return appReviewId == that.appReviewId &&
                isShared == that.isShared &&
                Objects.equals(application, that.application) &&
                Objects.equals(policy.id(), that.policy.id()) &&
                pendingReviewerRole == that.pendingReviewerRole &&
                Objects.equals(actions, that.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appReviewId, application, policy.id(), pendingReviewerRole, actions, isShared);
    }
}
