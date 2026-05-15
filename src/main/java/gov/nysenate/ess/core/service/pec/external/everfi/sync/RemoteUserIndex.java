package gov.nysenate.ess.core.service.pec.external.everfi.sync;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Bucketed view of remote Everfi users by identity confidence. Constructed once from a flat set of
 * {@link RemoteUser}s and consumed by the planner to look up matches by employee ID. See
 * {@code package-info.java} for the meaning of authoritative / candidate / unidentifiable.
 *
 * <p>Building the index throws {@link IllegalStateException} if two remote users share a mapping
 * entry for the same employee ID — that's a data invariant the rest of the planner relies on.
 */
record RemoteUserIndex(
        /**
         * Remote users with a formal mapping entry — one per employee ID.
         */
        Map<Integer, RemoteUser> authoritativeRemotes,
        /**
         * Remote users without a mapping but with a remote employee ID — may be multiple per employee.
         */
        Map<Integer, Set<RemoteUser>> candidateRemotes,
        /**
         * Remote users with neither a mapping nor a remote employee ID.
         */
        Set<RemoteUser> unidentifiableRemotes,
        /**
         * Employee IDs whose local mapping row has no corresponding user in the remote snapshot.
         * These employees must not be routed to CREATE — the dangling mapping must be investigated first.
         */
        Set<Integer> empIdsWithUnmatchedMappings
) {

    public RemoteUserIndex {
        authoritativeRemotes = Map.copyOf(authoritativeRemotes);
        candidateRemotes = candidateRemotes.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> Set.copyOf(entry.getValue())
                ));
        unidentifiableRemotes = Set.copyOf(unidentifiableRemotes);
        empIdsWithUnmatchedMappings = Set.copyOf(empIdsWithUnmatchedMappings);
    }

    public static RemoteUserIndex from(@NotNull Set<RemoteUser> remoteUsers) {
        return from(remoteUsers, Set.of());
    }

    public static RemoteUserIndex from(@NotNull EverfiUserSyncPreflight.RemoteLoadResult remoteLoadResult) {
        Objects.requireNonNull(remoteLoadResult, "remoteLoadResult must not be null");
        return from(remoteLoadResult.remoteUsers(), remoteLoadResult.empIdsWithUnmatchedMappings());
    }

    public static RemoteUserIndex from(@NotNull Set<RemoteUser> remoteUsers,
                                       @NotNull Set<Integer> empIdsWithUnmatchedMappings) {
        Objects.requireNonNull(remoteUsers, "remoteUsers must not be null");
        Objects.requireNonNull(empIdsWithUnmatchedMappings, "empIdsWithUnmatchedMappings must not be null");

        Map<Integer, RemoteUser> authoritativeRemotes = new HashMap<>();
        Map<Integer, Set<RemoteUser>> candidateRemotes = new HashMap<>();
        Set<RemoteUser> unidentifiableRemotes = new HashSet<>();

        for (RemoteUser remoteUser : remoteUsers) {
            indexRemoteUser(remoteUser, authoritativeRemotes, candidateRemotes, unidentifiableRemotes);
        }

        return new RemoteUserIndex(authoritativeRemotes, candidateRemotes, unidentifiableRemotes, empIdsWithUnmatchedMappings);
    }

    private static void indexRemoteUser(RemoteUser remoteUser,
                                        Map<Integer, RemoteUser> authoritativeRemotes,
                                        Map<Integer, Set<RemoteUser>> candidateRemotes,
                                        Set<RemoteUser> unidentifiableRemotes) {
        var mapping = remoteUser.mapping();
        var remoteEmployeeId = remoteUser.remoteEmployeeId();

        if (mapping != null) {
            int employeeId = mapping.employeeId();
            RemoteUser existing = authoritativeRemotes.putIfAbsent(employeeId, remoteUser);
            if (existing != null) {
                throw new IllegalStateException("multiple authoritative remotes found for employeeId " + employeeId);
            }
        } else if (remoteEmployeeId != null) {
            candidateRemotes.computeIfAbsent(remoteEmployeeId, key -> new HashSet<>())
                    .add(remoteUser);
        } else {
            unidentifiableRemotes.add(remoteUser);
        }
    }

    public Optional<RemoteUser> getAuthoritativeMatch(int employeeId) {
        return Optional.ofNullable(authoritativeRemotes.get(employeeId));
    }

    public Optional<Set<RemoteUser>> getCandidates(int employeeId) {
        return Optional.ofNullable(candidateRemotes.get(employeeId));
    }

    public Set<RemoteUser> allRemoteUsers() {
        Set<RemoteUser> all = new HashSet<>(authoritativeRemotes.values());
        candidateRemotes.values().forEach(all::addAll);
        all.addAll(unidentifiableRemotes);
        return Collections.unmodifiableSet(all);
    }
}
