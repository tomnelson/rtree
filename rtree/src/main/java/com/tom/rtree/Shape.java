package com.tom.rtree;

public interface Shape extends Bounded {

    boolean contains(Point p);

    boolean contains(double x, double y);

    boolean intersects(double x, double y, double w, double h);

    boolean intersects(Rectangle r);
}
