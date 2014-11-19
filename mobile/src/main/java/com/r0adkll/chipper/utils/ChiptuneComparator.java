package com.r0adkll.chipper.utils;


import com.r0adkll.chipper.api.model.Chiptune;

import java.util.Comparator;

/**
 * Created by r0adkll on 4/30/14.
 * Project: ChipperProject
 * Package: com.r0adkll.chipper.utils
 * <p/>
 * Copyright @2014 Drew Heavner. All rights reserved.
 */
public class ChiptuneComparator implements Comparator<Chiptune> {

    /**
     * Compares the two specified objects to determine their relative ordering. The ordering
     * implied by the return value of this method for all possible pairs of
     * {@code (lhs, rhs)} should form an <i>equivalence relation</i>.
     * This means that
     * <ul>
     * <li>{@code compare(a, a)} returns zero for all {@code a}</li>
     * <li>the sign of {@code compare(a, b)} must be the opposite of the sign of {@code
     * compare(b, a)} for all pairs of (a,b)</li>
     * <li>From {@code compare(a, b) > 0} and {@code compare(b, c) > 0} it must
     * follow {@code compare(a, c) > 0} for all possible combinations of {@code
     * (a, b, c)}</li>
     * </ul>
     *
     * @param lhs an {@code Object}.
     * @param rhs a second {@code Object} to compare with {@code lhs}.
     * @return an integer < 0 if {@code lhs} is less than {@code rhs}, 0 if they are
     * equal, and > 0 if {@code lhs} is greater than {@code rhs}.
     * @throws ClassCastException if objects are not of the correct type.
     */
    @Override
    public int compare(Chiptune lhs, Chiptune rhs) {
        int c1 = lhs.artist.compareTo(rhs.artist);
        if (c1 == 0) {
            int c2 = lhs.title.compareTo(rhs.title);
            return c2;
        }
        return c1;
    }
}
