package at.fhjoanneum.cgvis.test;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

import at.fhjoanneum.cgvis.data.IPointSet;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

public class TestPlot extends JFrame {

    private static final long serialVersionUID = -2243600487014494723L;
    private IPointSet plotData;
    private PCanvas plotCanvas;

    public TestPlot(IPointSet plotData) {
        super("TestPlot");

        this.plotData = plotData;

        setLayout(new GridBagLayout());

        final GridBagConstraints c = new GridBagConstraints();

        plotCanvas = new PCanvas();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        final Container contentPane = getContentPane();
        contentPane.add(plotCanvas, c);

        final PText tooltipNode = new PText();
        tooltipNode.setPickable(false);

        final PCamera camera = plotCanvas.getCamera();
        camera.addChild(tooltipNode);

        camera.addInputEventListener(new PBasicInputEventHandler() {
            public void mouseMoved(PInputEvent event) {
                updateToolTip(event);
            }

            public void mouseDragged(PInputEvent event) {
                updateToolTip(event);
            }

            public void updateToolTip(PInputEvent event) {
                PNode n = event.getInputManager().getMouseOver()
                        .getPickedNode();
                String tooltipString = (String) n.getAttribute("tooltip");
                Point2D p = event.getCanvasPosition();

                n.addAttribute("tooltip", n.getWidth() + "," + n.getHeight());

                event.getPath().canvasToLocal(p, camera);

                tooltipNode.setText(tooltipString);
                tooltipNode.setOffset(p.getX() + 8, p.getY() - 8);
            }
        });

        final PLayer layer = plotCanvas.getLayer();
        final PFixedWidthStroke stroke = new PFixedWidthStroke(.5f);
        for (int i = 0, size = plotData.getSize(); i < size; i++) {
            final double x = plotData.getValue(i, 0);
            final double y = plotData.getValue(i, 1);
            final double r = Math.abs(plotData.getValue(i, 2));
            // final PPath pp = PPath.createEllipse((float)x, (float)y,
            // (float)r, (float)r);
            final PPath pp = PPath.createEllipse(0f, 0f, 1f, 1f);
            layer.addChild(pp);
            pp.setPaint(Color.blue);
            pp.setStroke(stroke);
            pp.setBounds(x, y, r, r);
        }

        JButton but = new JButton("Try");
        but.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (PNode p : (List<PNode>) plotCanvas.getLayer()
                        .getChildrenIterator()) {
                    double x = Math.random();
                    double y = Math.random();
                    double r = Math.random() / 10;

                    x *= 100;
                    y *= 100;
                    r *= 100;

                    // if (r == 0) r = .0001;
                    p.addActivity(p.animateToBounds(x, y, r, r, 1000));
                    // p.setBounds(x, y, r, r);
                }
            }
        });
        getContentPane().add(but);
        but = new JButton("Fit");
        but.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                plotCanvas.getCamera().animateViewToCenterBounds(
                        new Rectangle2D.Double(0, 0, 1, 1), true, 1000);
            }
        });
        getContentPane().add(but);

        setLocation(100, 100);
        setPreferredSize(new Dimension(500, 500));
    }

    public IPointSet getPlotData() {
        return plotData;
    }

    public static void main(String[] args) throws IOException {
        // final int N = 100;
        // final PointSet pd = new PointSet("testps", N, 3);
        // for (int i = 0; i < N; i++) {
        // final double x = Math.random() * 2000 - 1000;
        // final double y = Math.random() * 2000 - 1000;
        // // final double x = Math.random();
        // // final double y = Math.random();
        // pd.setValue(i, 0, x);
        // pd.setValue(i, 1, y);
        //			
        // final double r = Math.abs(Math.random() * 100 - Math.random() * 100)
        // + 1;
        // // final double r = Math.random() / 10;
        //			
        // pd.setValue(i, 2, r);
        // //plotData.setElementLabel(i, "Circle #" + i);
        // }
        //
        // final TestPlot plot = new TestPlot(pd);
        //
        // // PDebug.debugRegionManagement = true;
        // // PDebug.debugThreads = true;
        //		
        // plot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // plot.pack();
        // plot.setVisible(true);
        // // plot.plotCanvas.getCamera().setViewBounds(new
        // Rectangle2D.Double(0,0,1,1));
    }
}
