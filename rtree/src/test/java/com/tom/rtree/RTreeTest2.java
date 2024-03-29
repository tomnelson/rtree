package com.tom.rtree;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Tom Nelson */
public class RTreeTest2 {

  private static final Logger log = LoggerFactory.getLogger(RTreeTest2.class);

  SplitterContext<String> splitterContext =
      SplitterContext.of(new RStarLeafSplitter(), new RStarSplitter());
  private RTree<String> rTree;
  private Rectangle r1;
  private Rectangle r2;
  private Rectangle r3;
  private Rectangle r4;
  private Rectangle r5;
  private Rectangle r6;
  private Rectangle r7;
  private Rectangle r8;
  Map<String, Rectangle> linkedMap = new LinkedHashMap<>();

  @Before
  public void before() {
    rTree = RTree.create();
    r1 = Rectangle.of(100, 100, 100, 100);
    r2 = Rectangle.of(200, 200, 100, 100);
    r3 = Rectangle.of(300, 300, 100, 100);
    r4 = Rectangle.of(400, 400, 100, 100);
    r5 = Rectangle.of(500, 500, 100, 100);
    r6 = Rectangle.of(100, 300, 100, 100);
    r7 = Rectangle.of(300, 100, 100, 100);
    r8 = Rectangle.of(400, 100, 100, 100);

    Random generator = new Random(1001);
    // generate reusable random nodes
    for (int i = 0; i < 30; i++) {

      double x = generator.nextDouble() * 500;
      double y = generator.nextDouble() * 500;
      double width = generator.nextDouble() * (600 - x);
      double height = generator.nextDouble() * (600 - y);
      Rectangle r = Rectangle.of(x, y, width, height);
      linkedMap.put("N" + i, r);
    }
  }

  @Test
  public void testOne() {

    RTree<String> rTree = RTree.create();
    rTree = RTree.add(rTree, splitterContext, "A", Rectangle.of(3, 3, 200, 100));
    rTree = RTree.add(rTree, splitterContext, "B", Rectangle.of(400, 300, 100, 100));
    rTree = RTree.add(rTree, splitterContext, "C", Rectangle.of(200, 300, 100, 100));

    rTree = RTree.add(rTree, splitterContext, "D", Rectangle.of(400, 120, 100, 100));
    rTree = RTree.add(rTree, splitterContext, "E", Rectangle.of(20, 500, 10, 100));
    rTree = RTree.add(rTree, splitterContext, "F", Rectangle.of(5, 40, 100, 100));
    log.info("tree {} initial size is {}", rTree, rTree.count());
    for (int i = 0; i < 100; i++) {
      double x = Math.random() * 500;
      double y = Math.random() * 500;
      double width = Math.random() * 50 + 50;
      double height = Math.random() * 50 + 50;
      Rectangle r = Rectangle.of(x, y, width, height);
      rTree = RTree.add(rTree, splitterContext, "N" + i, r);
      log.trace("tree:" + rTree);
    }
    log.trace("root:{}");
    testAreas(rTree);

    log.info("after adding 100 'N' nodes, tree size is {}", rTree.count());

    for (int i = 0; i < 100; i++) {
      String element = "N" + i;
      rTree = rTree.remove(rTree, element);
      log.trace("tree size:{}", rTree.count());
    }
    log.info(
        "after removing all 'N' nodes, tree {} size is back to initial size of {}",
        rTree,
        rTree.count());
  }

  @Test
  public void testAddOne() {
    rTree = RTree.add(rTree, splitterContext, "A", r1);
    //    Assert.assertTrue(rTree.level == 0);
    Assert.assertTrue(rTree.getRoot().isPresent());
    Node<String> root = rTree.getRoot().get();
    Assert.assertTrue(root instanceof LeafNode);
    Assert.assertTrue(root.size() == 1);

    testAreas(rTree);
  }

  @Test
  public void testAddTwo() {
    rTree = RTree.add(rTree, splitterContext, "A", r1);
    rTree = RTree.add(rTree, splitterContext, "B", r2);

    testAreas(rTree);
  }

  @Test
  public void testAddThree() {
    rTree = RTree.add(rTree, splitterContext, "A", r1);
    rTree = RTree.add(rTree, splitterContext, "B", r2);
    rTree = RTree.add(rTree, splitterContext, "C", r3);

    testAreas(rTree);
  }

  @Test
  public void testAddFour() {
    rTree = RTree.add(rTree, splitterContext, "A", r1);
    rTree = RTree.add(rTree, splitterContext, "B", r2);
    rTree = RTree.add(rTree, splitterContext, "C", r3);
    rTree = RTree.add(rTree, splitterContext, "D", r4);

    testAreas(rTree);
  }

  @Test
  public void testAddFive() {
    rTree = RTree.add(rTree, splitterContext, "A", r1);
    rTree = RTree.add(rTree, splitterContext, "B", r2);
    rTree = RTree.add(rTree, splitterContext, "C", r3);
    rTree = RTree.add(rTree, splitterContext, "D", r4);
    rTree = RTree.add(rTree, splitterContext, "E", r5);

    testAreas(rTree);
  }

  @Test
  public void testAddSix() {
    rTree = RTree.add(rTree, splitterContext, "A", r1);
    rTree = RTree.add(rTree, splitterContext, "B", r2);
    rTree = RTree.add(rTree, splitterContext, "C", r3);
    rTree = RTree.add(rTree, splitterContext, "D", r4);
    rTree = RTree.add(rTree, splitterContext, "E", r5);
    rTree = RTree.add(rTree, splitterContext, "F", r6);

    testAreas(rTree);
  }

  @Test
  public void testAddSeven() {
    rTree = RTree.add(rTree, splitterContext, "A", r1);
    rTree = RTree.add(rTree, splitterContext, "B", r2);
    rTree = RTree.add(rTree, splitterContext, "C", r3);
    rTree = RTree.add(rTree, splitterContext, "D", r4);
    rTree = RTree.add(rTree, splitterContext, "E", r5);
    rTree = RTree.add(rTree, splitterContext, "F", r6);
    rTree = RTree.add(rTree, splitterContext, "G", r7);

    testAreas(rTree);
  }

  @Test
  public void testAddEight() {
    rTree = RTree.add(rTree, splitterContext, "A", r1);
    log.trace("rtree:{}", rTree);
    rTree = RTree.add(rTree, splitterContext, "B", r2);
    log.trace("rtree:{}", rTree);
    rTree = RTree.add(rTree, splitterContext, "C", r3);
    log.trace("rtree:{}", rTree);
    rTree = RTree.add(rTree, splitterContext, "D", r4);
    log.trace("rtree:{}", rTree);
    rTree = RTree.add(rTree, splitterContext, "E", r5);
    log.trace("rtree:{}", rTree);
    rTree = RTree.add(rTree, splitterContext, "F", r6);
    log.trace("rtree:{}", rTree);
    rTree = RTree.add(rTree, splitterContext, "G", r7);
    log.trace("rtree:{}", rTree);
    rTree = RTree.add(rTree, splitterContext, "H", r8);
    log.trace("rtree:{}", rTree);

    testAreas(rTree);
  }

  @Test
  public void testFindingElementsByLocation() {
    rTree = RTree.add(rTree, splitterContext, "A", r1);
    rTree = RTree.add(rTree, splitterContext, "B", r2);
    rTree = RTree.add(rTree, splitterContext, "C", r3);
    rTree = RTree.add(rTree, splitterContext, "D", r4);
    rTree = RTree.add(rTree, splitterContext, "F", r5);
    rTree = RTree.add(rTree, splitterContext, "G", r6);
    rTree = RTree.add(rTree, splitterContext, "H", r7);

    Point p = Point.of(r4.x + r4.width / 2, r4.y + r4.height / 2);
    Object found = rTree.getPickedObject(p);
    Assert.assertEquals(found, "D");

    p = Point.of(r7.x + r7.width / 2, r7.y + r7.height / 2);
    found = rTree.getPickedObject(p);
    Assert.assertEquals(found, "H");

    p = Point.of(r1.x + r1.width / 2, r1.y + r1.height / 2);
    found = rTree.getPickedObject(p);
    Assert.assertEquals(found, "A");
  }

  // make sure the rectangle area in rTree is the same as the union of the areas
  // in its children (elements or children)

  private void testAreas(RTree<String> rTree) {
    if (rTree.getRoot().isPresent()) {
      TreeNode root = rTree.getRoot().get();
      testAreas(root);
    }
  }

  private void testAreas(TreeNode rootNode) {

    Rectangle rootBounds = rootNode.getBounds();
    if (rootNode instanceof InnerNode) {
      InnerNode innerNode = (InnerNode) rootNode;
      Rectangle unionBounds = Node.union(innerNode.getChildren());
      Assert.assertTrue(Math.abs(rootBounds.x - unionBounds.x) < 1.0E-3);
      Assert.assertTrue(Math.abs(rootBounds.y - unionBounds.y) < 1.0E-3);
      Assert.assertTrue(Math.abs(rootBounds.maxX - unionBounds.maxX) < 1.0E-3);
      Assert.assertTrue(Math.abs(rootBounds.maxY - unionBounds.maxY) < 1.0E-3);
    }

    for (TreeNode rt : rootNode.getChildren()) {
      testAreas(rt);
    }
  }

  private void stuff() {
    Collection<String> strings = new HashSet<>();
    strings.add("1");
    List<String> list = strings.stream().collect(Collectors.toList());

    List<Map.Entry<String, String>> entryList =
        new HashMap<String, String>().entrySet().stream().collect(Collectors.toList());

    List<String> valueList =
        new HashMap<String, String>()
            .values()
            .stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    valueList.replaceAll(String::toLowerCase);
  }
}
