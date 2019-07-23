package com.tom.rtree;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for R-Tree nodes Includes static functions for area, union, margin, overlap
 *
 * @author Tom Nelson
 */
public interface Node<T> extends TreeNode, Bounded {

  Logger log = LoggerFactory.getLogger(Node.class);

  int M = 10;
  int m = (int) (M * .4); // m is 40% of M

  String asString(String margin);

  T getPickedObject(Point p);

  int size();

  void setParent(Node<T> node);

  Optional<Node<T>> getParent();

  Node<T> add(SplitterContext<T> splitterContext, T element, Rectangle bounds);

  boolean isLeafChildren();

  int count();

  Point centerOfGravity();

  Set<LeafNode<T>> getContainingLeafs(Set<LeafNode<T>> containingLeafs, double x, double y);

  Set<LeafNode<T>> getContainingLeafs(Set<LeafNode<T>> containingLeafs, Point p);

  LeafNode<T> getContainingLeaf(T element);

  Node<T> remove(T element);

  Node<T> recalculateBounds();

  Collection<Shape> collectGrids(Collection<Shape> list);

  Set<T> getVisibleElements(Set<T> visibleElements, Shape shape);

  static String asString(List<Rectangle> rectangles) {
    StringBuilder sb = new StringBuilder();
    for (Rectangle r : rectangles) {
      sb.append(asString(r));
      sb.append("\n");
    }
    return sb.toString();
  }

  static String asString(Rectangle r) {
    return "["
        + (int) r.x
        + ","
        + (int) r.y
        + ","
        + (int) r.width
        + ","
        + (int) r.height
        + "]";
  }

  static <T> String asString(Map.Entry<T, Rectangle> entry) {
    return entry.getKey() + "->" + asString(entry.getValue());
  }

  static <T> String asString(Node<T> node, String margin) {
    StringBuilder s = new StringBuilder();
    s.append(margin);
    s.append("bounds=");
    s.append(asString(node.getBounds()));
    s.append('\n');

    s.append(node.asString(margin + marginIncrement));
    return s.toString();
  }

  String marginIncrement = "   ";

  static <T> Rectangle entryBoundingBox(Collection<Map.Entry<T, Rectangle>> entries) {
    Rectangle boundingBox = null;
    for (Map.Entry<T, Rectangle> entry : entries) {
      Rectangle rectangle = entry.getValue();
      if (boundingBox == null) {
        boundingBox = rectangle;
      } else {
        boundingBox = boundingBox.union(rectangle);
      }
    }
    return boundingBox;
  }

  static <T> Rectangle nodeBoundingBox(Collection<Node<T>> nodes) {
    Rectangle boundingBox = null;
    for (Node<T> node : nodes) {
      Rectangle rectangle = node.getBounds();
      if (boundingBox == null) {
        boundingBox = rectangle;
      } else {
        boundingBox = boundingBox.union(rectangle);
      }
    }
    return boundingBox;
  }

  static Rectangle boundingBox(Collection<Rectangle> rectangles) {
    Rectangle boundingBox = null;
    for (Rectangle rectangle : rectangles) {
      if (boundingBox == null) {
        boundingBox = rectangle;
      } else {
        boundingBox = boundingBox.union(rectangle);
      }
    }
    return boundingBox;
  }

  static double area(Collection<Rectangle> rectangles) {
    return area(boundingBox(rectangles));
  }

  static <T> double nodeArea(Collection<Node<T>> nodes) {
    return area(nodeBoundingBox(nodes));
  }

  static <T> double entryArea(Collection<Map.Entry<T, Rectangle>> entries) {
    return area(entryBoundingBox(entries));
  }

  static <T> double entryArea(
      Collection<Map.Entry<T, Rectangle>> left, Collection<Map.Entry<T, Rectangle>> right) {
    return entryArea(left) + entryArea(right);
  }

  static <T> double nodeArea(Collection<Node<T>> left, Collection<Node<T>> right) {
    return nodeArea(left) + nodeArea(right);
  }

  static double area(Collection<Rectangle> left, Collection<Rectangle> right) {
    return area(left) + area(right);
  }

  static double area(Rectangle r) {
    double area = r.width * r.height;
    return area < 0 ? -area : area;
  }

  static double area(Rectangle left, Rectangle right) {
    return area(left) + area(right);
  }

  static double margin(Collection<Rectangle> rectangles) {
    return margin(boundingBox(rectangles));
  }

  static double margin(Rectangle r) {
    double width = r.maxX - r.x;
    double height = r.maxY - r.y;
    return 2 * (width + height);
  }

  static double margin(Rectangle left, Rectangle right) {
    return margin(left) + margin(right);
  }

  static double margin(Collection<Rectangle> left, Collection<Rectangle> right) {
    return margin(left) + margin(right);
  }

  static <T> double nodeMargin(Collection<Node<T>> left, Collection<Node<T>> right) {
    return margin(nodeBoundingBox(left)) + margin(nodeBoundingBox(right));
  }

  static <T> double entryMargin(
      Collection<Map.Entry<T, Rectangle>> left, Collection<Map.Entry<T, Rectangle>> right) {
    return margin(entryBoundingBox(left)) + margin(entryBoundingBox(right));
  }

  static <T> double entryOverlap(
      Collection<Map.Entry<T, Rectangle>> left, Collection<Map.Entry<T, Rectangle>> right) {
    return overlap(entryBoundingBox(left), entryBoundingBox(right));
  }

  static <T> double nodeOverlap(Collection<Node<T>> left, Collection<Node<T>> right) {
    return overlap(nodeBoundingBox(left), nodeBoundingBox(right));
  }

  static double overlap(Collection<Rectangle> left, Collection<Rectangle> right) {
    return overlap(boundingBox(left), boundingBox(right));
  }

  static double overlap(Rectangle left, Rectangle right) {
    return area(left.intersection(right));
  }

  static Rectangle union(Collection<? extends Bounded> boundedItems) {
    Rectangle union = null;
    for (Bounded r : boundedItems) {
      if (union == null) {
        union = r.getBounds();
      } else {
        union = r.getBounds().union(union);
      }
    }
    return union;
  }

  static double width(Collection<? extends Bounded> boundedItems) {
    double min = 600;
    double max = 0;
    for (Bounded b : boundedItems) {
      min = Math.min(b.getBounds().x, min);
      max = Math.max(b.getBounds().maxX, max);
    }
    return max - min;
  }
}
