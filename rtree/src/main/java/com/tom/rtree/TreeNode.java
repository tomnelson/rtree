package com.tom.rtree;

import java.util.Collection;

/** @author Tom Nelson */
public interface TreeNode {

  Rectangle getBounds();

  Collection<? extends TreeNode> getChildren();
}
