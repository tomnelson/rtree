package com.tom.rtree;

import com.google.common.base.Preconditions;

/**
 * Simple, immutable Rectangle class used for spatial data structures.
 *
 * @author Tom Nelson
 */
public class Rectangle implements Shape {

  public final double x;
  public final double y;
  public final double width;
  public final double height;
  public final double maxX;
  public final double maxY;

  public Rectangle() {
    this(0, 0, 0, 0);
  }
  /**
   * @param x left most x location
   * @param y top most y location
   * @param width horizontal size of rectangle when aligned
   * @param height vertical size of rectangle when aligned
   */
  public Rectangle(double x, double y, double width, double height) {
//    Preconditions.checkArgument(width >= 0 && height >= 0, "width and height must be non-negative");
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.maxX = x + width;
    this.maxY = y + height;
  }

  /** @return the x coordinate of the center of this Rectangle */
  public double getCenterX() {
    return x + width / 2;
  }

  /** @return the y coordinate of the center of this Rectangle */
  public double getCenterY() {
    return y + height / 2;
  }

  /**
   * fail-fast implementation to reduce computation
   *
   * @param other
   * @return
   */
  public boolean intersects(Rectangle other) {
    return maxX >= other.x && other.maxX >= x && maxY >= other.y && other.maxY >= y;
  }

  /**
   * fail-fast implementation to reduce computation
   *
   * @return
   */
  public boolean intersects(double x, double y, double w, double h) {
    if (isEmpty() || w <= 0 || h <= 0) {
      return false;
    }
    double x0 = x;
    double y0 = y;
    return (x + w > this.x &&
            y + h > this.y &&
            x < this.x + this.width &&
            y < this.y + this.height);
  }

  public boolean isEmpty() {
    return (width <= 0) || (height <= 0);
  }

  /**
   * @param p point to test
   * @return true if the coordinate is contained, false otherwise
   */
  public boolean contains(Point p) {
    return contains(p.x, p.y);
  }

  /**
   * Fail fast for the most common case where the point coordinates are not contained.
   * Implementation leaves space for debug breakpoints
   *
   * @param ox coodinate to test
   * @param oy coordinate to test
   * @return true if the coordinate is contained, false otherwise
   */
  public boolean contains(double ox, double oy) {
    if (ox < x) return false;
    if (ox > maxX) return false;
    if (oy < y) return false;
    if (oy > maxY) return false;
    return true;
  }

  public Rectangle add(double newX, double newY) {
    double x1 = Math.min(x, newX);
    double x2 = Math.max(maxX, newX);
    double y1 = Math.min(y, newY);
    double y2 = Math.max(maxY, newY);
    return new Rectangle(x1, y1, x2 - x1, y2 - y1);
  }

  public Rectangle union(Rectangle other) {
    double x1 = Math.min(x, other.x);
    double x2 = Math.max(maxX, other.maxX);
    double y1 = Math.min(y, other.y);
    double y2 = Math.max(maxY, other.maxY);
    return new Rectangle(x1, y1, x2 - x1, y2 - y1);
  }

  public Rectangle intersection(Rectangle other) {
    double x1 = Math.max(x, other.x);
    double y1 = Math.max(y, other.y);
    double x2 = Math.min(maxX, other.maxX);
    double y2 = Math.min(maxY, other.maxY);
    return new Rectangle(x1, y1, x2 - x1, y2 - y1);
  }



  @Override
  public String toString() {
    return "Rectangle{"
        + "x="
        + x
        + ", y="
        + y
        + ", width="
        + width
        + ", height="
        + height
        + ", maxX="
        + maxX
        + ", maxY="
        + maxY
        + '}';
  }

  @Override
  public Rectangle getBounds() {
    return this;
  }
}
