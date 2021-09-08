package com.tom.rtree;

import static com.tom.rtree.Node.M;
import static com.tom.rtree.Node.area;
import static com.tom.rtree.Node.m;

import java.util.*;

/**
 * splits the passed entries using the quadratic method (for R-Tree)
 *
 * @author Tom Nelson
 * @param <T>
 */
public class QuadraticLeafSplitter<T> implements LeafSplitter<T> {

  @Override
  public Pair<LeafNode<T>> split(
      Collection<Map.Entry<T, Rectangle>> entries, Map.Entry<T, Rectangle> newEntry) {
    return quadraticSplit(entries, newEntry);
  }

  private Optional<Map.Entry<T, Rectangle>> pickNext(
      List<Map.Entry<T, Rectangle>> entries, Pair<LeafNode<T>> pickedSeeds) {
    double maxDifference = 0;
    Optional<Map.Entry<T, Rectangle>> winner = Optional.empty();
    entries.removeAll(pickedSeeds.left.map.entrySet());
    entries.removeAll(pickedSeeds.right.map.entrySet());
    // for each entry
    for (Map.Entry<T, Rectangle> entry : entries) {
      // ... that is not already in the leaf node....
      if (!pickedSeeds.left.map.containsKey(entry.getKey())
          && !pickedSeeds.right.map.containsKey(entry.getKey())) {
        // calculate area increase that would happen
        LeafNode<T> leftNode = pickedSeeds.left;
        LeafNode<T> rightNode = pickedSeeds.right;
        double leftArea = area(leftNode.getBounds());
        double rightArea = area(rightNode.getBounds());
        Rectangle leftUnion = leftNode.getBounds().union(entry.getValue());
        Rectangle rightUnion = rightNode.getBounds().union(entry.getValue());
        double leftAreaIncrease = area(leftUnion) - leftArea;
        double rightAreaIncrease = area(rightUnion) - rightArea;
        double difference = leftAreaIncrease - rightAreaIncrease;
        // make sure it is positive
        difference = difference < 0 ? -difference : difference;
        if (!winner.isPresent()) {
          winner = Optional.of(entry);
          maxDifference = difference;
        } else if (difference > maxDifference) {
          maxDifference = difference;
          winner = Optional.of(entry);
        }
      }
    }

    winner.ifPresent(entries::remove);
    return winner;
  }

  /**
   * from the list of entries, return the pair that represent the largest increase in area
   *
   * @param entryList
   * @return
   */
  private Pair<LeafNode<T>> pickSeeds(List<Map.Entry<T, Rectangle>> entryList) {
    double largestArea = 0;
    Optional<Pair<Map.Entry<T, Rectangle>>> winningPair = Optional.empty();
    for (int i = 0; i < entryList.size(); i++) {
      for (int j = i + 1; j < entryList.size(); j++) {
        Pair<Map.Entry<T, Rectangle>> entryPair = new Pair<>(entryList.get(i), entryList.get(j));
        Rectangle union = entryPair.left.getValue().union(entryPair.right.getValue());
        double area =
            Node.area(union)
                - Node.area(entryPair.left.getValue())
                - Node.area(entryPair.right.getValue());
        if (!winningPair.isPresent()) {
          winningPair = Optional.of(entryPair);
          largestArea = area;
        } else if (area > largestArea) {
          winningPair = Optional.of(entryPair);
        }
      }
    }
    if (!winningPair.isPresent()) throw new RuntimeException("No winning pair returned");
    Map.Entry<T, Rectangle> leftEntry = winningPair.get().left;
    LeafNode leftNode = LeafNode.create(leftEntry);
    Map.Entry<T, Rectangle> rightEntry = winningPair.get().right;
    LeafNode rightNode = LeafNode.create(rightEntry);

    return Pair.of(leftNode, rightNode);
  }

  private void distributeEntry(
      List<Map.Entry<T, Rectangle>> entries, Pair<LeafNode<T>> pickedSeeds) {

    Optional<Map.Entry<T, Rectangle>> nextOptional = pickNext(entries, pickedSeeds);
    if (nextOptional.isPresent()) {
      Map.Entry<T, Rectangle> next = nextOptional.get();
      // which of the picked seeds should it be added to?
      Rectangle leftBounds = pickedSeeds.left.getBounds();
      Rectangle rightBounds = pickedSeeds.right.getBounds();
      // which rectangle is enlarged the least?
      double leftArea = Node.area(leftBounds);
      double rightArea = Node.area(rightBounds);
      double leftEnlargement = Node.area(leftBounds.union(next.getValue())) - leftArea;
      double rightEnlargement = Node.area(rightBounds.union(next.getValue())) - rightArea;
      if (leftEnlargement == rightEnlargement) {
        // a tie. consider the smaller area
        if (leftArea == rightArea) {
          // another tie. consider the one with the fewest kids
          int leftKids = pickedSeeds.left.size();
          int rightKids = pickedSeeds.right.size();
          if (leftKids < rightKids) {
            pickedSeeds.left.map.put(next.getKey(), next.getValue());
          } else {
            pickedSeeds.right.map.put(next.getKey(), next.getValue());
          }
        } else if (leftArea < rightArea) {
          pickedSeeds.left.map.put(next.getKey(), next.getValue());
        } else {
          pickedSeeds.right.map.put(next.getKey(), next.getValue());
        }
      } else if (leftEnlargement < rightEnlargement) {
        pickedSeeds.left.map.put(next.getKey(), next.getValue());
      } else {
        pickedSeeds.right.map.put(next.getKey(), next.getValue());
      }
    }
  }

  /**
   * combine the existing map elements with the new one and make a pair of leaf nodes to distribute
   * the entries into
   *
   * @param entries Collection of entries to split
   * @param newEntry
   * @return
   */
  private Pair<LeafNode<T>> quadraticSplit(
      Collection<Map.Entry<T, Rectangle>> entries, Map.Entry<T, Rectangle> newEntry) {
    // make a collection of kids from leafNode that also include the new element
    // items will be removed from the entryList as they are distributed
    List<Map.Entry<T, Rectangle>> entryList = new ArrayList<>(entries);
    entryList.add(newEntry);
    // get the best pair to split on from the leafNode elements
    Pair<LeafNode<T>> pickedSeeds = pickSeeds(entryList);
    // these currently have no parent set....
    while (entryList.size() > 0
        && pickedSeeds.left.size() < M - m + 1
        && pickedSeeds.right.size() < M - m + 1) {
      distributeEntry(entryList, pickedSeeds);
    }
    if (entryList.size() > 0) {
      // take care of entries that were not distributed
      if (pickedSeeds.left.size() >= M - m + 1) {
        // left side too big, give them to the right side
        for (Map.Entry<T, Rectangle> entry : entryList) {
          pickedSeeds.right.map.put(entry.getKey(), entry.getValue());
        }
      } else {
        // right side too big, give them to the left side
        for (Map.Entry<T, Rectangle> entry : entryList) {
          pickedSeeds.left.map.put(entry.getKey(), entry.getValue());
        }
      }
    }
    return pickedSeeds;
  }
}
