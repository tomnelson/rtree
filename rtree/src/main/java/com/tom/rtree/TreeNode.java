package com.tom.rtree;

import java.awt.geom.Rectangle2D;
import java.util.Collection;

/** @author Tom Nelson */
public interface TreeNode {

  Rectangle2D getBounds();

  Collection<? extends TreeNode> getChildren();
}
