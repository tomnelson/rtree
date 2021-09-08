package com.tom.rtree;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Map of elements to Rectangle where the union of the child elements is kept up to date with the
 * values in the Map
 *
 * @author Tom Nelson
 */
public class NodeMap<N> extends HashMap<N, Rectangle> implements BoundedMap<N>, Bounded {

  private static final Logger log = LoggerFactory.getLogger(NodeMap.class);
  private Rectangle bounds;

  public NodeMap() {}

  public NodeMap(int initialCapacity) {
    super(initialCapacity);
  }

  public NodeMap(Map<N, Rectangle> map) {
    super(map);
    recalculateBounds();
  }

  public void put(Entry<N, Rectangle> entry) {
    put(entry.getKey(), entry.getValue());
  }

  @Override
  public Rectangle put(N n, Rectangle b) {
    addBoundsFor(b);
    return super.put(n, b);
  }

  @Override
  public Rectangle remove(Object o) {
    Rectangle removed = super.remove(o);
    recalculateBounds();
    return removed;
  }

  @Override
  public void clear() {
    super.clear();
    bounds = null;
  }

  @Override
  public Rectangle getBounds() {
    if (bounds == null) {
      return Rectangle.IDENTITY;
    }
    return bounds;
  }

  private void addBoundsFor(Map<? extends N, Rectangle> kids) {
    for (Entry<? extends N, Rectangle> kid : kids.entrySet()) {
      addBoundsFor(kid.getValue());
    }
  }

  private void addBoundsFor(Rectangle r) {
    if (bounds == null) {
      bounds = r;
    } else {
      bounds = bounds.union(r);
    }
  }
  /** iterate over all children and update the bounds Called after removing from the collection */
  public void recalculateBounds() {
    bounds = null;
    for (Rectangle r : this.values()) {
      addBoundsFor(r);
    }
  }
}
