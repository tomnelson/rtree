package com.tom.rtree;

import java.util.Comparator;
import java.util.Map;

/**
 * A comparator to compare along the x-axis, Nodes where the values are Rectangle First compare
 * the min x values, then the max x values
 *
 * @author Tom Nelson
 * @param <T>
 */
public class HorizontalEdgeNodeComparator<T> implements Comparator<Node<T>> {

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
    if (left.x == right.x) {
      if (left.maxX == right.maxX) return 0;
      if (left.maxX < right.maxX) return -1;
      else return 1;
    } else {
      if (left.x < right.x) return -1;
      return 1;
    }
  }

  public int compare(Map.Entry<?, Rectangle> leftNode, Map.Entry<?, Rectangle> rightNode) {
    return compare(leftNode.getValue(), rightNode.getValue());
  }

  @Override
  public int compare(Node<T> leftNode, Node<T> rightNode) {
    return compare(leftNode.getBounds(), rightNode.getBounds());
  }
}
