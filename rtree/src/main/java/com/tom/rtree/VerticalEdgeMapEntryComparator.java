package com.tom.rtree;

import java.util.Comparator;
import java.util.Map;

/**
 * A comparator to compare along the y-axis, Map.Entries where the values are Rectangle First
 * compare the min y values, then the max y values
 *
 * @author Tom Nelson
 * @param <T>
 */
public class VerticalEdgeMapEntryComparator<T> implements Comparator<Map.Entry<T, Rectangle>> {
  /**
   * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer
   * as the first argument is less than, equal to, or greater than the second.
   *
   * @param left the first object to be compared.
   * @param right the second object to be compared.
   * @return a negative integer, zero, or a positive integer as the first argument is less than,
   *     equal to, or greater than the second.
   * @throws NullPointerException if an argument is null and this comparator does not permit null
   *     arguments
   * @throws ClassCastException if the arguments' types prevent them from being compared by this
   *     comparator.
   */
  public int compare(Rectangle left, Rectangle right) {
    if (left.y == right.y) {
      if (left.maxY == right.maxY) return 0;
      if (left.maxY < right.maxY) return -1;
      else return 1;
    } else {
      if (left.y < right.y) return -1;
      return 1;
    }
  }

  @Override
  public int compare(Map.Entry<T, Rectangle> leftNode, Map.Entry<T, Rectangle> rightNode) {
    return compare(leftNode.getValue(), rightNode.getValue());
  }

  public int compare(Node<?> leftNode, Node<?> rightNode) {
    return compare(leftNode.getBounds(), rightNode.getBounds());
  }
}
