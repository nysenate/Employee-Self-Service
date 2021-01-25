package gov.nysenate.ess.travel.review;

import com.google.common.base.Preconditions;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.authorization.role.TravelRole;
import gov.nysenate.ess.travel.review.strategy.*;

import java.util.*;

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

        if (travelerRole == TravelRole.NONE) {
            reviewerStrategy = new DefaultReviewerStrategy();
        } else if (travelerRole == TravelRole.MAJORITY_LEADER) {
            reviewerStrategy = new MajReviewerStrategy();
        } else if (application.getTraveler().isSenator()) {
            reviewerStrategy = new SenatorReviewerStrategy();
        } else if (travelerRole == TravelRole.DEPARTMENT_HEAD) {
            reviewerStrategy = new DepartmentHeadReviewerStrategy();
        } else if (travelerRole == TravelRole.TRAVEL_ADMIN) {
            reviewerStrategy = new DeaReviewerStrategy();
        } else if (travelerRole == TravelRole.SECRETARY_OF_THE_SENATE) {
            reviewerStrategy = new SosReviewerStrategy();
        }
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

    int getAppReviewId() {
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
        }
        catch (NoSuchElementException ex) {
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
