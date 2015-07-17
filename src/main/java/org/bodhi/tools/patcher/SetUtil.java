
package org.bodhi.tools.patcher;

import java.util.HashSet;
import java.util.Set;

public final class SetUtil {

    private SetUtil() {
    }

    public static <T> Set<T> intersection(Set<T> setA, Set<T> setB) {
	Set<T> tmp = new HashSet<T>(setA);
	tmp.retainAll(setB);
	return tmp;
    }

    // Returns A - B

    public static <T> Set<T> difference(Set<T> setA, Set<T> setB) {
	Set<T> tmp = new HashSet<T>(setA);
	tmp.removeAll(setB);
	return tmp;
    }
}
