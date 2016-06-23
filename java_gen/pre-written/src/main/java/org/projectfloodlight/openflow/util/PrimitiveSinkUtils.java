package org.projectfloodlight.openflow.util;

import java.util.List;
import java.util.SortedSet;

import javax.annotation.Nullable;

import org.projectfloodlight.openflow.types.PrimitiveSinkable;

import com.google.common.hash.PrimitiveSink;

/** Utility methods for dumping collections into primitive sinks.
 *
 * @author Andreas Wundsam {@literal <}andreas.wundsam@bigswitch.com{@literal >}
 */
public class PrimitiveSinkUtils {
    private PrimitiveSinkUtils() {}

    /** puts a nullable String into a primitive sink. The entry is prepended by a 'presence'
     *  boolean bit and the string length;
     *
     *
     * @param sink the sink to put the object
     * @param nullableChars the potentially null string to put in the sink
     */
    public static void putNullableStringTo(PrimitiveSink sink,
            @Nullable CharSequence nullableChars) {

        sink.putBoolean(nullableChars != null);
        if(nullableChars != null) {
            sink.putInt(nullableChars.length());
            sink.putUnencodedChars(nullableChars);
        }
    }

    /** puts a nullable element into a primitive sink. The entry is prepended by a 'present' bit.
     *
     * @param sink the sink to put the object
     * @param nullableObj the nullable object
     */
    public static void putNullableTo(PrimitiveSink sink,
            @Nullable PrimitiveSinkable nullableObj) {
        sink.putBoolean(nullableObj != null);
        if(nullableObj != null)
            nullableObj.putTo(sink);
    }

    /** puts the elements of a sorted set into the {@link PrimitiveSink}. Does not support null
     *  elements. The elements are assumed to be self-delimitating.
     *
     * @param sink the sink to put the sorted set
     * @param set the sorted set
     */
    public static void putSortedSetTo(PrimitiveSink sink,
            SortedSet<? extends PrimitiveSinkable> set) {
        sink.putInt(set.size());
        for(PrimitiveSinkable e: set) {
            e.putTo(sink);
        }
    }

    /** puts the elements of a list into the {@link PrimitiveSink}. Does not support null
     *  elements. The elements are assumed to be self-delimitating.
     *
     * @param sink the sink to put the list elements
     * @param list the list
     */
    public static void putListTo(PrimitiveSink sink,
            List<? extends PrimitiveSinkable> list) {
        sink.putInt(list.size());
        for(PrimitiveSinkable e: list) {
            e.putTo(sink);
        }
    }
}
