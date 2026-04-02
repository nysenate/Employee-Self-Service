package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import gov.nysenate.ess.core.annotation.UnitTest;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@Category(UnitTest.class)
public class RemoteUserIndexTest {

    @Test
    public void whenEmptyRemoteUsers_returnsEmptyIndex() {
        RemoteUserIndex index = RemoteUserIndex.from(Set.of());
        assertIndex(index, Map.of(), Map.of(), Set.of());
    }

    @Test
    public void whenNullRemoteUsers_throws() {
        assertThatThrownBy(() -> RemoteUserIndex.from(null))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class)
                .hasMessageContaining("remoteUsers");
    }

    @Test
    public void whenNullUnmatchedMappingEmpIds_throws() {
        assertThatThrownBy(() -> RemoteUserIndex.from(Set.of(), null))
                .isInstanceOfAny(NullPointerException.class, IllegalArgumentException.class)
                .hasMessageContaining("empIdsWithUnmatchedMappings");
    }

    @Test
    public void whenUnmatchedMappingEmpIdsProvided_exposedOnIndex() {
        Set<Integer> unmatchedEmpIds = Set.of(42, 99);
        RemoteUserIndex index = RemoteUserIndex.from(Set.of(), unmatchedEmpIds);
        assertThat(index.empIdsWithUnmatchedMappings()).isEqualTo(unmatchedEmpIds);
    }

    @Test
    public void whenMappingExists_indexedAsAuthoritative() {
        RemoteUser r = remoteUser().build();
        RemoteUserIndex index = RemoteUserIndex.from(Set.of(r));
        assertIndex(index, Map.of(1, r), Map.of(), Set.of());
    }

    @Test
    public void whenMappingMissingButRemoteEmployeeIdExists_indexedAsCandidate() {
        RemoteUser r = remoteUser()
                .mapping(null)
                .build();
        RemoteUserIndex index = RemoteUserIndex.from(Set.of(r));
        assertIndex(index, Map.of(), Map.of(1, Set.of(r)), Set.of());
    }

    @Test
    public void whenMappingAndRemoteEmployeeIdMissing_indexedAsUnidentifiable() {
        RemoteUser r = remoteUser()
                .mapping(null)
                .remoteEmployeeId(null)
                .build();
        RemoteUserIndex index = RemoteUserIndex.from(Set.of(r));
        assertIndex(index, Map.of(), Map.of(), Set.of(r));
    }

    @Test
    public void whenMultipleAuthoritativeRemotes_throws() {
        RemoteUser r1 = remoteUser()
                .build();
        RemoteUser r2 = remoteUser()
                .mapping(new EverfiEmployeeMapping(1, "everfi-uuid-2"))
                .remoteEmail("auth2@nysenate.gov")
                .build();

        assertThatThrownBy(() -> RemoteUserIndex.from(Set.of(r1, r2)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("multiple authoritative remotes found for employeeId 1");
    }

    @Test
    public void whenMultipleCandidateMatches_allPutInCandidateMap() {
        RemoteUser r1 = remoteUser().mapping(null).build();
        RemoteUser r2 = remoteUser()
                .mapping(null)
                .remoteUuid("everfi-uuid-2")
                .remoteEmail("second@nysenate.gov")
                .build();
        RemoteUserIndex index = RemoteUserIndex.from(Set.of(r1, r2));
        assertIndex(index, Map.of(), Map.of(1, Set.of(r1, r2)), Set.of());
    }

    @Test
    public void whenCandidateHasSameEmployeeIdAsAuthoritative_bothIndexedIndependently() {
        RemoteUser authoritative = remoteUser().build();
        RemoteUser candidate = remoteUser()
                .mapping(null)
                .remoteUuid("everfi-uuid-2")
                .remoteEmail("candidate@nysenate.gov")
                .build(); // same remoteEmployeeId=1 as authoritative

        RemoteUserIndex index = RemoteUserIndex.from(Set.of(authoritative, candidate));

        assertIndex(index, Map.of(1, authoritative), Map.of(1, Set.of(candidate)), Set.of());
    }

    @Test
    public void whenUsersCoverAllClassificationTypes_indexesAllAtOnce() {
        RemoteUser authoritative = remoteUser().build();
        RemoteUser candidate = remoteUser()
                .mapping(null)
                .remoteUuid("everfi-uuid-2")
                .remoteEmployeeId(2)
                .remoteEmail("candidate@nysenate.gov")
                .build();
        RemoteUser unidentifiable = remoteUser()
                .mapping(null)
                .remoteUuid("everfi-uuid-3")
                .remoteEmployeeId(null)
                .remoteEmail("unidentifiable@nysenate.gov")
                .build();

        RemoteUserIndex index = RemoteUserIndex.from(Set.of(authoritative, candidate, unidentifiable));

        assertIndex(
                index,
                Map.of(1, authoritative),
                Map.of(2, Set.of(candidate)),
                Set.of(unidentifiable)
        );
    }

    private void assertIndex(RemoteUserIndex index,
                             Map<Integer, RemoteUser> authoritativeRemotes,
                             Map<Integer, Set<RemoteUser>> candidateRemotes,
                             Set<RemoteUser> unidentifiableRemotes) {
        assertThat(index.authoritativeRemotes()).isEqualTo(authoritativeRemotes);
        assertThat(index.candidateRemotes()).isEqualTo(candidateRemotes);
        assertThat(index.unidentifiableRemotes()).isEqualTo(unidentifiableRemotes);
    }

    private RemoteUser.RemoteUserBuilder remoteUser() {
        return RemoteUser.builder()
                .mapping(new EverfiEmployeeMapping(1, "everfi-uuid-1"))
                .remoteUuid("everfi-uuid-1")
                .remoteEmployeeId(1)
                .remoteActive(true)
                .remoteFirstName("John")
                .remoteLastName("Doe")
                .remoteEmail("example@nysenate.gov");
    }
}
