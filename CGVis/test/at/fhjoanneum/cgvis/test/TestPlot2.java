package at.fhjoanneum.cgvis.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

public class TestPlot2 extends JFrame {

    private static final long serialVersionUID = -2779613966334740237L;
    private PCanvas plotCanvas1;
    private PCanvas plotCanvas2;

    public TestPlot2() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));

        plotCanvas1 = new PCanvas();
        plotCanvas2 = new PCanvas();

        final PFixedWidthStroke stroke = new PFixedWidthStroke(.5f);
        // final BasicStroke stroke = new BasicStroke(.01f);
        // final PFixedWidthStroke stroke = null;
        for (int i = 0; i < 10; i++) {
            PPath pp = PPath.createRectangle(i / 10f, i / 10f, .1f, .1f);
            // PPath pp = PPath.createEllipse(i/10f, i/10f, .1f, .1f);
            plotCanvas1.getLayer().addChild(pp);
            pp.setPaint(Color.blue);
            pp.setStroke(stroke);

            pp = PPath.createRectangle(i * 10, i * 10, 10, 10);
            // pp = PPath.createEllipse(i * 10, i * 10, 10, 10);
            plotCanvas2.getLayer().addChild(pp);
            pp.setPaint(Color.blue);
            pp.setStroke(stroke);
        }

        JButton but = new JButton("Try");
        but.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Iterator<PNode> it1 = plotCanvas1.getLayer()
                        .getChildrenIterator();
                Iterator<PNode> it2 = plotCanvas2.getLayer()
                        .getChildrenIterator();
                while (it1.hasNext()) {
                    double x = Math.random();
                    double y = Math.random();
                    double r = Math.random() / 10;

                    PNode p1 = it1.next();
                    PNode p2 = it2.next();

                    p1.addActivity(p1.animateToBounds(x, y, r, r, 1000));
                    p2.addActivity(p2.animateToBounds(x * 100, y * 100,
                            r * 100, r * 100, 1000));

                    // p1.setBounds(x, y, r, r);
                    // p2.setBounds(x * 100, y * 100, r * 100, r * 100);
                }
            }
        });
        but.setAlignmentX(CENTER_ALIGNMENT);

        getContentPane().add(plotCanvas1);
        getContentPane().add(but);
        getContentPane().add(plotCanvas2);

        setLocation(100, 100);
        setPreferredSize(new Dimension(500, 500));
    }

    public static void main(String[] args) throws IOException {
        final TestPlot2 plot = new TestPlot2();
        plot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        plot.pack();
        plot.setVisible(true);
        plot.plotCanvas1.getCamera().setViewBounds(
                new Rectangle2D.Double(0, 0, 1, 1));
        plot.plotCanvas2.getCamera().setViewBounds(
                new Rectangle2D.Double(0, 0, 100, 100));
    }
}
