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

    public ApplicationReview(int appReviewId, TravelApplication application, TravelRole travelerRole, List<Action> actions) {
        this.appReviewId = appReviewId;
        this.application = application;
        this.travelerRole = travelerRole;
        this.actions = new TreeSet<>(actionComparator);
        this.actions.addAll(actions);

        if (travelerRole == TravelRole.NONE) {
            reviewerStrategy = new RegularReviewerStrategy();
        } else if (travelerRole == TravelRole.MAJORITY_LEADER) {
            reviewerStrategy = new MajReviewerStrategy();
        } else if (application.getTraveler().isSenator()) {
            reviewerStrategy = new SenatorReviewerStrategy();
        } else if (travelerRole == TravelRole.SUPERVISOR) {
            reviewerStrategy = new SupervisorReviewerStrategy();
        } else if (travelerRole == TravelRole.DEPUTY_EXECUTIVE_ASSISTANT) {
            reviewerStrategy = new DeaReviewerStrategy();
        } else if (travelerRole == TravelRole.SECRETARY_OF_THE_SENATE) {
            reviewerStrategy = new SosReviewerStrategy();
        }
    }

    public ApplicationReview(TravelApplication application, TravelRole travelerRole) {
        this(0, application, travelerRole, new ArrayList<>());
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
        } else {
            return reviewerStrategy.after(previousReviewerRole());
        }
    }

    /**
     * Discussion is requested for this application review if the most recent action
     * has requested discussion.
     */
    public boolean isDiscussionRequested() {
        Action a = mostRecentAction();
        return a == null ? false : a.isDiscussionRequested;
    }

    public TravelApplication application() {
        return application;
    }

    public SortedSet<Action> actions() {
        return actions;
    }

    public TravelRole travelerRole() {
        return travelerRole;
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
}
