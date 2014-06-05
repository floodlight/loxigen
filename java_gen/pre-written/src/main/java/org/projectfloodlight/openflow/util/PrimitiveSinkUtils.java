package org.projectfloodlight.openflow.util;

import java.util.List;
import java.util.SortedSet;

import javax.annotation.Nullable;

import org.projectfloodlight.openflow.types.PrimitiveSinkable;

import com.google.common.hash.PrimitiveSink;

/** Utility methods for dumping collections into primitive sinks.
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public class PrimitiveSinkUtils {
    private PrimitiveSinkUtils() {}

    /** puts a nullable element into a primitive sink. The entry is terminated by a null byte
     *  to disambiguate null elements.
     *
     * @param sink the sink to put the object
     * @param nullableObj the nullable object
     */
    public static void putNullableTo(PrimitiveSink sink,
            @Nullable PrimitiveSinkable nullableObj) {
        if(nullableObj != null)
            nullableObj.putTo(sink);

        // terminate this object representation by a null byte. this ensures that we get
        // unique digests even if some values are null
        sink.putByte((byte) 0);
    }

    /** puts the elements of a sorted set into the {@link PrimitiveSink}. Does not support null
     *  elements. The elements are assumed to be self-delimitating.
     *
     * @param sink
     * @param set
     */
    public static void putSortedSetTo(PrimitiveSink sink,
            SortedSet<? extends PrimitiveSinkable> set) {
        for(PrimitiveSinkable e: set) {
            e.putTo(sink);
        }
    }

    /** puts the elements of a list into the {@link PrimitiveSink}. Does not support null
     *  elements. The elements are assumed to be self-delimitating.
     *
     * @param sink
     * @param set
     */
    public static void putListTo(PrimitiveSink sink,
            List<? extends PrimitiveSinkable> set) {
        for(PrimitiveSinkable e: set) {
            e.putTo(sink);
        }
    }
}
