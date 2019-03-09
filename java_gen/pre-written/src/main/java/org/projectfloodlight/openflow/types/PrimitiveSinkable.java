package org.projectfloodlight.openflow.types;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import org.projectfloodlight.openflow.util.PrimitiveSinkUtils;

import com.google.common.collect.ImmutableSet;
import com.google.common.hash.Hasher;
import com.google.common.hash.PrimitiveSink;

/**
 * Interface contract for objects that can dump their internal state into a {@link PrimitiveSink}
 * for strong hashing.
 *
 * <p>
 * An example primitive sink is a {@link Hasher}) from Guava's Hashing module, which can be
 * created e.g., using <code>Hashing.sha256.newHasher()</code> for a SHA256 hash.
 *
 * <h2>Object Equality Contract<h2>
 *
 * Implementors should dump their <b>full observable object state</b> into the sink.
 * <p>
 * In other words, given two strongly hashable objects o1, o2, if they ever share the same
 * data-structure (e.g., HashTable), then <code>o1.putTo(h1)</code> and <code>o2.putTo(h2)</code>
 * must put the same data into the hasher <strong>if and only if</strong>
 * <code>o1.equals(o2)</code>.
 * <p>
 *   Note this is a stronger contract than the one of {@link Object#hashCode()}, which only
 *   requires that equal objects yield equal hash codes (but not that unequal objects yield
 *   unequal hash codes).
 * </p>
 *
 * <h2>Caveats/common traps:</h2>
 *
 * <h3>(1) Ordering:</h3>
 * Be careful about the serialization order for children when serializing sets.
 * If the order does not matter for the logical equality, make sure you dump
 * the child objects in a deterministic order. If the data comes from an set which does
 * not provide deterministic iteration order, make sure to sort the entries before dumping them, or
 * use a {@link SortedSet}, e.g., a {@link TreeSet}. Note that the most commonly used sets
 * all don't provide an guaranteed order: {@link HashSet} iterates in arbitrary, non-stable
 * order (e.g., item with hash-code collisions are returned in insertion order).
 * {@link LinkedHashSet} or Guava's {@link ImmutableSet} always yield objects in insertion order.
 * Similarly watch out for cases where logical sets are represented as arrays or lists.
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

 * If your object contains parts of variable length or conditionals, make sure you serialize
 * the parts carefully to avoid situations where unequal objects yield identical hash codes.
 * This can be achieved by using <b>delimiters</b> between variable parts or by putting in
 * explicit <b>marker values</b>.
 * <p>
 * The same applies for recursive application of PrimitiveSinkable; make sure the child
 * objects are fixed length or unambiguously delimited, or put explicit delimiters between
 * child objects to avoid cross-talk.
 *
 * <pre>
 * {@code
 *       public void putTo(PrimitiveSink s) {
 *
 *          ///////// variable length:
 *          String a, b;
 *
 *          // BAD: Don't do this! same hash code will result for a="X", b="YZ"
 *          // and a="XY" and b="Z"
 *          s.putString(a);
 *          s.putString(b);
 *
 *          // OK if a and b are known to to not contain '|'
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
 *          ///////// conditional:
 *          Integer valueA, valueB;
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
 * @see PrimitiveSinkUtils
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
