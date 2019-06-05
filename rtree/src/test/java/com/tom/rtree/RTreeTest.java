package com.tom.rtree;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import org.junit.Assert;
import org.junit.Test;

public class RTreeTest {

  SplitterContext<String> splitterContext =
      SplitterContext.of(new RStarLeafSplitter<>(), new RStarSplitter<>());
  RTree<String> rTree = RTree.create();
  int count;
  int width = 1000;
  int height = 1000;

  private void addRandomShape() {
    double w = 10;
    double h = 10;
    double x = Math.random() * width - w;
    double y = Math.random() * height - h;
    Rectangle2D r = new Rectangle2D.Double(x, y, w, h);
    rTree = rTree.add(splitterContext, "N" + count++, r);
  }

  @Test
  public void testAddRemoveAll() {
    System.err.println("RTREE: " + rTree);
    for (int i = 0; i < 20000; i++) {
      addRandomShape();
    }
    Assert.assertTrue(rTree.getRoot().isPresent());
    Node<String> root = rTree.getRoot().get();
    System.err.println("Root kid size is " + root.getChildren().size());
    assertHasChildren(root);
    System.err.println("RTREE: " + rTree);

    for (int i = 0; i < 1000; i++) {
      rTree = rTree.remove("N" + i);
    }
    Assert.assertFalse(rTree.getRoot().isPresent());
    count = 0;
    for (int i = 0; i < 10; i++) {
      addRandomShape();
    }
    System.err.println("RTREE: " + rTree);
    assertHasChildren(rTree.getRoot().get());
  }

  /**
   * all nodes have children (none are empty)
   *
   * @param parent
   */
  private void assertHasChildren(TreeNode parent) {
    if (parent instanceof LeafNode) {
      Assert.assertTrue(((LeafNode) parent).size() > 0);
    } else {
      Assert.assertTrue(parent.getChildren().size() > 0);
      for (TreeNode kid : parent.getChildren()) {
        assertHasChildren(kid);
      }
    }
  }
}
