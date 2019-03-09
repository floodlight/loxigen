package org.projectfloodlight.openflow.types;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.SortedSet;

import org.projectfloodlight.openflow.util.PrimitiveSinkUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hasher;
import com.google.common.hash.PrimitiveSink;

/**
 * Interface Contract for objects that can dump their internal state into a {@link PrimitiveSink}
 * for strong hashing.
 *
 * An example primitive sink is a {@link Hasher}) from Guava's Hashing module, which can be
 * created e.g., using <code>Hashing.sha256.newHasher()</code> for a SHA256 hash.
 * <p>
 * <h2>object equality contract<h2>: Implementors should dump their <b>full observable object
 * state</b> into the sink.
 * <p>
 * I..e, if two objects o1, o2 ever share the same data-structure (e.g., HashTable), then
 *    <code>o1.putTo(h1)</code>
 * and
 *    <code>o2.putTo(h2)</code>
 * must put the same data into the hasher <strong>if and only if</strong>
 * <code>
 *   o1.equals(o2).
 * </code>
 * <p>,
 *   Note this is a stronger contract than the one of {@link Object#hashCode()}, which only
 *   requires that equal objects yield equal hash codes (but not that differing objects equal
 *   differing hash codes).
 * </p>
 * <h2>Caveats/common traps:</h2>
 *
 *
 * <h3>(1) Ordering:</h3>
 * Be careful about the order of items in a sub-datastructure, e.g., a List or a Set. If the
 * order does not matter for the logical equality (e.g., Set semantics), make sure you dump
 * the child objects in stable,  deterministic order. If the data comes from a data which does
 * not provide deterministic, stable iteration order (e.g., {@link HashSet} {@link LinkedHashSet},
 * Guava's {@link ImmutableSet}), make sure to sort the entries before dumping them, or use
 * a {@link SortedSet}.
 *
 * <pre>
 * {@code
 *
 *    class ObjectWithSet implements PrimitiveSinkable {
 *       private Set<PrimitiveSinkable> children = new HashSet<>();
 *
 *       // BAD: don't do this:
 *       public void putTo(PrimitiveSink s) {
 *          // children may come in arbitrary order! So you may end up with a different hash
 *          / for the same object
 *          for(PrimitiveSinkable child: children) {
 *              child.putTo(sink);
 *          }
 *       }

 *       // better:
 *       public void putTo(PrimitiveSink s) {
 *          // set a sorted version (or use a treeset to begin with?)
 *          private TreeSet<String> sorted = Sets.newTreeSet(children);
 *
 *          for(PrimitiveSinkable child: children) {
 *              // note: this still requires children to be self delimitating; see caveat
 *              / (2), below.
 *              child.putTo(sink);
 *          }
 *       }
 *
 *    }
 * }
 * </pre>

 * <h3>(2) Delimiting Variable Length Objects or Conditionals:</h3>

 * If your object contains parts of variable length or conditionals, (or if itself is going to
 * be used in a parent object), make sure that you don't end up with identical hashes across code
 * paths. This can be achieved by using <b>delimiters</b> between variable parts or by putting
 * explicit <b>marker values</b>.
 * <pre>
 * {@code
 *       // BAD: don't do this:
 *       public void putTo(PrimitiveSink s) {
 *          String a, b;
 *
 *          // variable length:
 *
 *          // BAD: Don't do this! same hash code will result for a="X", b="YZ"
 *          // and a="XY" and b="Z"
 *          s.putString(a);
 *          s.putString(b);
 *
 *          // OK if a and b are not to not contain '|'
 *          s.putString(a, Charsets.UTF_8);
 *          s.putChar('|');
 *          s.putString(b, Charsets.UTF_8););
 *          s.putChar('|');
 *
 *          // GOOD!: Encodes the length as a marker value - safe!
 *          PrimitiveSinkUtils.putNullableStringTo(a);
 *          PrimitiveSinkUtils.putNullableStringTo(b);
 *
 *
 *          // conditional
 *          // BAD: May end up with the same hash code depending on which conditional is taken
 *
 *          if(conditionA) {
 *              s.putInteger(valueA);
 *          }
 *
 *          if(conditionB) {
 *              s.putInteger(valueB);
 *          }
 *
 *          // GOOD: Use delimiter to separate values
 *          if(conditionA) {
 *              s.putValue(valueA);
 *          }
 *          s.putChar('|');
 *
 *          if(conditionB) {
 *              s.putInteger(valueB);
 *          }
 *          s.putChar('|');
 *
 *          // GOOD: Conditional decisions are recorded in marker values.
 *          if(conditionA) {
 *              s.putBoolean(true);
 *              s.putInteger(valueA);
 *          } else {
 *              s.putBoolean(false);
 *          }
 *          if(conditionB) {
 *              s.putBoolean(true);
 *              s.putInteger(valueB);
 *          } else {
 *              s.putBoolean(false);
 *          }
 *
 *
 *          // Careful: Make sure your children are fixed length, or delimited:
 *          for(PrimitiveSinkable child: children) {
 *              child.putTo(s);
 *              // may be safer to (if the child serialization does not contain '|').
 *              s.putChar('|')
 *          }
 *       }
 * }
 * </pre>
 * Take a look at {@link PrimitiveSinkUtils} for functions that help with these caveats.
 *
 * @author Andreas Wundsam {@literal <andreas.wundsam@bigswitch.com>}
 */
public interface PrimitiveSinkable {
    /** Dump the state of this object into a {@link PrimitiveSink} (e.g., a Hasher) for the purpose
     * of computing a strong hash.
     *
     * <strong>Equality contract:</strong>
     * Equal objects  must dump the equal data into the sink, non-equal objects must dump different
     * data into the sink. Please see the interface documentation of {@link PrimitiveSink} for more
     * details and caveats/traps.</strong>
     *
     * @param sink the sink to dump the object state into.
     */
    public void putTo(PrimitiveSink sink);
}
