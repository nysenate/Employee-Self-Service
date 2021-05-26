package gov.nysenate.ess.travel.request.amendment;

import gov.nysenate.ess.travel.request.app.TravelApplication;

/**
 * Uniquely identifies {@link Amendment Amendments} on a {@link TravelApplication}.
 *
 * A TravelApplication's first amendment will be Version.A, second Version.B, etc.
 */
public enum Version {
    A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z,
    AA, BB, CC, DD, EE, FF, GG, HH, II, JJ, KK, LL, MM, NN, OO, PP, QQ, RR, SS, TT, UU, VV, WW, XX, YY, ZZ
    ;

    private static final Version[] versions = values();

    /**
     * @return The version after the current.
     * @throws ArrayIndexOutOfBoundsException if the current version is the last.
     */
    public Version next() {
        return versions[(this.ordinal() + 1)];
    }
}
