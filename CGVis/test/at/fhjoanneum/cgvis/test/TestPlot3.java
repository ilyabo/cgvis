package at.fhjoanneum.cgvis.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

public class TestPlot3 extends JFrame {

    private static final long serialVersionUID = 4925131159324772021L;
    private PCanvas plotCanvas;
    private PNode biggestNode,
    // middleNode,
            smallestNode;

    public TestPlot3() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        plotCanvas = new PCanvas();

        final int N = 24;
        // final PFixedWidthStroke stroke = new PFixedWidthStroke(.5f);
        final BasicStroke stroke = null;
        double pos = 0;
        PPath pp = null;

        for (int i = 0; i < N; i++) {
            final double r = (double) (Math.pow(2, -i));
            // pp = new PPath(new Rectangle2D.Double(pos,pos,r,r));
            pp = new PPath(new Ellipse2D.Double(pos, pos, r, r));
            // pp = PPath.createRectangle(pos, pos, r, r);
            // pp = PPath.createEllipse(pos, pos, r, r);
            plotCanvas.getLayer().addChild(pp);
            pp.setPaint(new Color((int) Math.round(Math.random()
                    * Integer.MAX_VALUE), false));
            pp.setStroke(stroke);
            if (i == 0) {
                biggestNode = pp;
            } else if (i == N / 2) {
                // middleNode = pp;
            } else if (i == N - 1) {
                smallestNode = pp;
            }
            System.out.println(i + ": " + "pos = " + pos + ", r = " + r
                    + ", bounds=" + pp.getBounds());
            pos += r / 4;
            // pos += r;
        }

        getContentPane().add(plotCanvas);

        plotCanvas.addInputEventListener(new PBasicInputEventHandler() {
            boolean zoomed = false;

            public void mousePressed(PInputEvent event) {
                if (!zoomed) {
                    // System.out.println(smallestNode.getGlobalBounds());
                    plotCanvas.getCamera().animateViewToCenterBounds(
                            smallestNode.getGlobalBounds(), true, 5000);
                    zoomed = true;
                }
            }
        });

        setLocation(100, 100);
        setPreferredSize(new Dimension(500, 500));
    }

    public static void main(String[] args) throws IOException {
        final TestPlot3 plot = new TestPlot3();
        plot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        plot.pack();
        plot.setVisible(true);
        plot.plotCanvas.getCamera().setViewBounds(
                plot.biggestNode.getGlobalBounds());
    }
}
