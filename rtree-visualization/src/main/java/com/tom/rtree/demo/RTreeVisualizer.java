package com.tom.rtree.demo;

import com.tom.rtree.Bounded;
import com.tom.rtree.InnerNode;
import com.tom.rtree.LeafNode;
import com.tom.rtree.Node;
import com.tom.rtree.RStarLeafSplitter;
import com.tom.rtree.RStarSplitter;
import com.tom.rtree.RTree;
import com.tom.rtree.SplitterContext;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A visualization of the R-Tree structure. users can add random elements, elements at mouse-click
 * location, or 2000 randomly generated elements. The structure of the R-Tree is also drawn
 *
 * @author Tom Nelson
 */
public class RTreeVisualizer extends JPanel {

  private static final Logger log = LoggerFactory.getLogger(RTreeVisualizer.class);

  private final int INITIAL_WIDTH = 600;
  private final int INITIAL_HEIGHT = 600;

  SplitterContext<Object> splitterContext =
      SplitterContext.of(new RStarLeafSplitter<>(), new RStarSplitter<>());
  RTree<Object> rTree = RTree.create();
  int count;

  public RTreeVisualizer() {
    setBackground(Color.white);
    setLayout(new BorderLayout());

    JButton addStuff = new JButton("Add something");
    addStuff.addActionListener(e -> addRandomShape());
    JPanel drawingPane =
        new JPanel() {
          public Dimension getPreferredSize() {
            return new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT);
          }

          @Override
          public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g;
            Collection<Shape> grid = rTree.getGrid();
            log.info("grid size is {}", grid.size());
            for (Shape r : grid) {
              g2d.draw(r);
            }
          }
        };
    JButton addLots = new JButton("Add Many");
    addLots.addActionListener(e -> addMany());

    drawingPane.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);

            if (SwingUtilities.isRightMouseButton(e)) {
              Object o = rTree.getPickedObject(e.getPoint());

              rTree = rTree.remove(o);
              log.info("after removing {} rtree:{}", o, rTree);
              repaint();

            } else {
              addShapeAt(e.getPoint());
            }
            repaint();
          }
        });
    JButton samePoint = new JButton("Add Same");
    samePoint.addActionListener(e -> addShapeAt(new Point2D.Double(200, 200)));
    JButton clear = new JButton("clear");
    clear.addActionListener(
        e -> {
          rTree = RTree.create();
          repaint();
        });
    addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            log.info("clicked at {}", e.getPoint());
            String node = "N" + count++;
            repaint();
          }
        });

    JPanel controls = new JPanel();
    controls.add(addStuff);
    controls.add(addLots);
    controls.add(clear);
    controls.add(samePoint);
    add(drawingPane);
    add(controls, BorderLayout.SOUTH);
  }

  private void addRandomShape() {
    double width = 10;
    double height = 10;
    double x = Math.random() * getWidth() - width;
    double y = Math.random() * getHeight() - height;
    Rectangle2D r = new Rectangle2D.Double(x, y, width, height);
    rTree = rTree.add(splitterContext, "N" + count++, r);
    repaint();
  }

  private void addMany() {
    for (int i = 0; i < 2000; i++) {
      double width = 4;
      double height = 4;
      double x = Math.random() * getWidth() - width;
      double y = Math.random() * getHeight() - height;
      Rectangle2D r = new Rectangle2D.Double(x, y, width, height);
      rTree = rTree.add(splitterContext, "N" + count++, r);
      checkBounds(rTree);
      repaint();
    }
    repaint();
  }

  private void addShapeAt(Point2D p) {
    double width = 10;
    double height = 10;
    Rectangle2D r =
        new Rectangle2D.Double(p.getX() - width / 2, p.getY() - height / 2, width, height);
    rTree = rTree.add(splitterContext, "N" + count++, r);
    log.info("after adding {} rtree:{}", "N" + (count - 1), rTree);
    checkBounds(rTree);
    repaint();
  }

  private void checkBounds(RTree<?> tree) {
    checkBounds(tree.getRoot().get());
  }

  private Rectangle2D getBounds(Collection<? extends Bounded> nodes) {
    Rectangle2D bounds = null;
    for (Bounded b : nodes) {
      if (bounds == null) bounds = b.getBounds();
      else {
        bounds = bounds.createUnion(b.getBounds());
      }
    }
    return bounds;
  }

  private void checkBounds(InnerNode<?> node) {
    if (!node.getBounds().equals(getBounds(node.getChildren()))) {
      log.error("bounds not equal \n{} != \n{}", node.getBounds(), getBounds(node.getChildren()));
    }
  }

  private void checkBounds(Node<?> node) {
    if (node instanceof InnerNode) {
      checkBounds((InnerNode) node);
    } else if (node instanceof LeafNode) {
      log.info("leafNode: {}", node);
    }
  }

  public static void main(String[] args) {
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    f.getContentPane().add(new RTreeVisualizer());
    f.pack();
    f.setVisible(true);
  }
}
