package com.tom.rtree;

import static com.tom.rtree.Node.M;
import static com.tom.rtree.Node.m;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * splits a Collection of Map.Entries according to R*-Tree semantics
 *
 * @author Tom Nelson
 * @param <T>
 */
public class RStarLeafSplitter<T> implements LeafSplitter<T> {

  private static final Logger log = LoggerFactory.getLogger(RStarLeafSplitter.class);
  private final Comparator<Map.Entry<T, Rectangle>> horizontalEdgeComparator =
      new HorizontalEdgeMapEntryComparator();
  private final Comparator<Map.Entry<T, Rectangle>> verticalEdgeComparator =
      new VerticalEdgeMapEntryComparator();

  public Pair<LeafNode<T>> split(
      Collection<Map.Entry<T, Rectangle>> entries, Map.Entry<T, Rectangle> newEntry) {
    return chooseSplitNodes(entries, newEntry);
  }

  private Pair<LeafNode<T>> chooseSplitNodes(
      Collection<Map.Entry<T, Rectangle>> entries, Map.Entry<T, Rectangle> newEntry) {
    Pair<List<Map.Entry<T, Rectangle>>> pair = chooseSplit(entries, newEntry);
    LeafNode<T> leafNodeLeft = LeafNode.create(pair.left);
    LeafNode<T> leafNodeRight = LeafNode.create(pair.right);
    return Pair.of(leafNodeLeft, leafNodeRight);
  }

  /**
   * R*-Tree method
   *
   * @param entries
   * @param newEntry
   * @return
   */
  private Pair<List<Map.Entry<T, Rectangle>>> chooseSplit(
      Collection<Map.Entry<T, Rectangle>> entries, Map.Entry<T, Rectangle> newEntry) {
    // make 2 lists to sort
    List<Map.Entry<T, Rectangle>> xAxisList = new ArrayList<>(entries);
    xAxisList.add(newEntry);
    List<Map.Entry<T, Rectangle>> yAxisList = new ArrayList<>(entries);
    yAxisList.add(newEntry);

    // sort them by min value then max value
    xAxisList.sort(horizontalEdgeComparator);
    yAxisList.sort(verticalEdgeComparator);

    // create containers for the 2 lists to split
    List<Pair<List<Map.Entry<T, Rectangle>>>> horizontalGroup = new ArrayList<>();
    List<Pair<List<Map.Entry<T, Rectangle>>>> verticalGroup = new ArrayList<>();

    // iterate over the lists to create collections with different midpoints
    for (int k = 0; k < M - 2 * m + 2; k++) {
      horizontalGroup.add(
          Pair.of(xAxisList.subList(0, m - 1 + k), xAxisList.subList(m - 1 + k, xAxisList.size())));
      verticalGroup.add(
          Pair.of(yAxisList.subList(0, m - 1 + k), yAxisList.subList(m - 1 + k, yAxisList.size())));
    }
    if (log.isTraceEnabled()) {
      log.trace("horizontalGroup size is {}", horizontalGroup.size());
      for (Pair<List<Map.Entry<T, Rectangle>>> pair : horizontalGroup) {
        log.trace("size of pair lists are {} and {}", pair.left.size(), pair.right.size());
      }
      log.trace("verticalGroup size is {}", verticalGroup.size());
      for (Pair<List<Map.Entry<T, Rectangle>>> pair : verticalGroup) {
        log.trace("size of pair lists are {} and {}", pair.left.size(), pair.right.size());
      }
    }

    // sum up the margin values from each group
    int sumXMarginValue = 0;
    for (Pair<List<Map.Entry<T, Rectangle>>> pair : horizontalGroup) {
      sumXMarginValue += Node.entryMargin(pair.left, pair.right);
    }
    int sumYMarginValue = 0;
    for (Pair<List<Map.Entry<T, Rectangle>>> pair : verticalGroup) {
      sumYMarginValue += Node.entryMargin(pair.left, pair.right);
    }
    // use the group (horizontal or vertical) that has the smallest margin value su
    if (sumXMarginValue < sumYMarginValue) {
      // split on x axis
      return chooseSplitIndex(horizontalGroup);
    } else {
      // split on y axis
      return chooseSplitIndex(verticalGroup);
    }
  }

  /**
   * R*-Tree method
   *
   * @param group
   * @return
   */
  private Pair<List<Map.Entry<T, Rectangle>>> chooseSplitIndex(
      List<Pair<List<Map.Entry<T, Rectangle>>>> group) {
    double minOverlap = 0;
    double minArea = 0;
    Optional<Pair<List<Map.Entry<T, Rectangle>>>> winner = Optional.empty();
    // find the Pair of lists with the min overlap or min area
    for (Pair<List<Map.Entry<T, Rectangle>>> pair : group) {
      double nodeOverlap = Node.entryOverlap(pair.left, pair.right);
      double nodeArea = Node.entryArea(pair.left, pair.right);
      // no winner yet. first node wins by default
      if (!winner.isPresent()) {
        minOverlap = nodeOverlap;
        minArea = nodeArea;
        winner = Optional.of(pair);
      } else if (nodeOverlap == minOverlap) {
        // tie for overlap, try area
        if (nodeArea < minArea) {
          minOverlap = nodeOverlap;
          minArea = nodeArea;
          winner = Optional.of(pair);
        }
      } else if (nodeOverlap < minOverlap) {
        // winner has the smallest overlap
        minOverlap = nodeOverlap;
        minArea = nodeArea;
        winner = Optional.of(pair);
      }
    }
    return winner.orElse(null);
  }
}
