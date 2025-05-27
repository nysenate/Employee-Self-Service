package gov.nysenate.ess.travel.request.draft;

import java.util.Set;

public class DraftViewPatches {
    private Set<DraftViewPatchOption> options;
    private DraftView draft;

    public DraftViewPatches() {
    }

    public Set<DraftViewPatchOption> getOptions() {
        return options;
    }

    public void setOptions(Set<DraftViewPatchOption> options) {
        this.options = options;
    }

    public DraftView getDraft() {
        return draft;
    }

    public void setDraft(DraftView draft) {
        this.draft = draft;
    }
}
