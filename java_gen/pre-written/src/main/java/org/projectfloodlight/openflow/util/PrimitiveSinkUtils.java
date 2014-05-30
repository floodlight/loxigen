package org.projectfloodlight.openflow.util;

import java.util.List;
import java.util.SortedSet;

import org.projectfloodlight.openflow.types.PrimitiveSinkable;

import com.google.common.hash.PrimitiveSink;

/** Utility methods for dumping collections into primitive sinks.
 *
 * @author Andreas Wundsam <andreas.wundsam@bigswitch.com>
 */
public class PrimitiveSinkUtils {
    private PrimitiveSinkUtils() {}

    public static void putSortedSetTo(PrimitiveSink sink,
            SortedSet<? extends PrimitiveSinkable> set) {
        for(PrimitiveSinkable e: set) {
            e.putTo(sink);
        }
    }

    public static void putListTo(PrimitiveSink sink,
            List<? extends PrimitiveSinkable> set) {
        for(PrimitiveSinkable e: set) {
            e.putTo(sink);
        }
    }
}
