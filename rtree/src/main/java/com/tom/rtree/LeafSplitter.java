package com.tom.rtree;

import java.util.Collection;
import java.util.Map;

/**
 * Interface for splitting LeafNodes containing Map.Entries as children
 *
 * @param <T> the type of the elements
 * @author Tom Nelson
 */
public interface LeafSplitter<T> {

  Pair<LeafNode<T>> split(
      Collection<Map.Entry<T, Rectangle>> entries, Map.Entry<T, Rectangle> newEntry);
}
