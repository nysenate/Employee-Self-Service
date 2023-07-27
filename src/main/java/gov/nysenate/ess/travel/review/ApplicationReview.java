package gov.nysenate.ess.travel.review;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.strategy.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The entire review process for a single {@link TravelApplication}.
 */
public class ApplicationReview {

    private final static Comparator<Action> actionComparator = Comparator.comparing(Action::dateTime);

    private int appReviewId;
    private TravelApplication application;
    private TravelRole travelerRole;
    private SortedSet<Action> actions;
    private ReviewerStrategy reviewerStrategy;
    private boolean isShared;

    public ApplicationReview(int appReviewId, TravelApplication application,
                             TravelRole travelerRole, List<Action> actions, boolean isShared) {
        this.appReviewId = appReviewId;
        this.application = application;
        this.travelerRole = travelerRole;
        this.actions = new TreeSet<>(actionComparator);
        this.actions.addAll(actions);
        this.isShared = isShared;
        this.reviewerStrategy = ReviewerStrategy.getStrategy(travelerRole, application.getTraveler().isSenator());
    }

    public ApplicationReview(TravelApplication application, TravelRole travelerRole) {
        this(0, application, travelerRole, new ArrayList<>(), false);
    }

    public void addAction(Action action) {
        Preconditions.checkArgument(action.role() == nextReviewerRole());
        actions.add(action);
    }

    /**
     * Returns the role which needs to review the application next.
     * If the application has been disapproved there is no need to continue the review workflow.
     */
    public TravelRole nextReviewerRole() {
        if (application.status().isDisapproved()) {
            return TravelRole.NONE;
        } else if (mostRecentAction() != null && mostRecentAction().isDisapproval() && application.status().isPending()) {
            // App has been resubmitted.
            return reviewerStrategy.after(null);
        } else {
            return reviewerStrategy.after(previousReviewerRole());
        }
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

    public TravelRole travelerRole() {
        return travelerRole;
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

    private TravelRole previousReviewerRole() {
        Action a = mostRecentAction();
        return a == null ? null : a.role();
    }

    // Returns the most recent action or null if there are no actions.
    private Action mostRecentAction() {
        try {
            return actions.last();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationReview that = (ApplicationReview) o;
        return appReviewId == that.appReviewId &&
                isShared == that.isShared &&
                Objects.equals(application, that.application) &&
                travelerRole == that.travelerRole &&
                Objects.equals(actions, that.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appReviewId, application, travelerRole, actions, isShared);
    }
}
