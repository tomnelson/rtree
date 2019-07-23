package com.tom.rtree;


/**
 * Interface for items that present a bounding box rectangle
 *
 * @author Tom Nelson
 */
public interface Bounded {

  /**
   * return the Rectangle of the bounding box
   *
   * @return
   */
  Rectangle getBounds();
}
