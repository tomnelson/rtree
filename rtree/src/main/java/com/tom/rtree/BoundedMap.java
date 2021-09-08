package com.tom.rtree;

/**
 * @author Tom Nelson
 * @param <T>
 */
public interface BoundedMap<T> extends java.util.Map<T, Rectangle> {

  Rectangle getBounds();

  void recalculateBounds();
}
