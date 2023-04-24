package at.fhj.utils.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import at.fhj.utils.misc.DateUtils;
import at.fhj.utils.misc.ProgressListener;


/**
 *
 * @author Ilya Boyandin
 *
 * $Revision: 1.4 $
 */
public class ProgressDialog extends JDialog implements ProgressListener {

  private static final long serialVersionUID = 3746232066819222234L;
    private JProgressBar progressBar;
  private final JProgressBar totalProgressBar;
  private JLabel statusLabel;
  private final JLabel totalStatusLabel;
  private final JLabel timeLeftLabel;
  private final ProgressWorker worker;
  private final JButton interruptButton;
  private final boolean doubleBarMode;
  // TODO: private Component visPanel;

  public ProgressDialog(Window owner, String title, ProgressWorker worker, boolean doubleBarMode) {
      this(owner, title, worker, doubleBarMode, false);
  }

  public ProgressDialog(Window owner, String title, ProgressWorker worker, boolean doubleBarMode, boolean indeterminate) {
    super(owner);
    this.worker = worker;
    this.doubleBarMode = doubleBarMode;

    setTitle(title);
    setModal(true);
    setResizable(false);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    JPanel cp = new JPanel();
    cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));
    cp.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
    setContentPane(cp);

    totalProgressBar = new JProgressBar();
    totalProgressBar.setIndeterminate(indeterminate);
    totalProgressBar.setMaximum(100);
    totalProgressBar.setSize(350, 175);
    Border pbBorder = totalProgressBar.getBorder();
    totalProgressBar.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(0, 0, 5, 0), pbBorder));

    totalStatusLabel = new JLabel(" ", JLabel.CENTER);
    totalStatusLabel.setForeground(Color.black);
    totalStatusLabel.setAlignmentX(CENTER_ALIGNMENT);

    cp.add(totalStatusLabel);
    cp.add(totalProgressBar);

    if (doubleBarMode) {
      progressBar = new JProgressBar();
      progressBar.setMaximum(100);
      progressBar.setSize(350, 175);
      Border ppbBorder = progressBar.getBorder();
      progressBar.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createEmptyBorder(0, 0, 5, 0), ppbBorder));

      statusLabel = new JLabel(" ", JLabel.CENTER);
      statusLabel.setAlignmentX(CENTER_ALIGNMENT);

      cp.add(statusLabel);
      cp.add(progressBar);
    }


    timeLeftLabel = new JLabel(" ", JLabel.CENTER);
    timeLeftLabel.setAlignmentX(CENTER_ALIGNMENT);
    cp.add(timeLeftLabel);

    if (indeterminate  &&  !doubleBarMode) {
        timeLeftLabel.setVisible(false);
    }

    interruptButton = new JButton("Cancel");
    interruptButton.addActionListener(interruptListener);
    interruptButton.setFocusable(false);

    JComponent buttonBox = new JPanel();
    buttonBox.add(interruptButton);

    cp.add(buttonBox);

    pack();
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension size = getSize();
    size.width = 360;
    setSize(size);

    if (owner == null) {
      setLocation((screen.width - size.width)/2, (screen.height - size.height)/2);
    } else {
      final Dimension ownerSize = owner.getSize();
      final Point ownerLoc = owner.getLocationOnScreen();
      setLocation(
          ownerLoc.x + (ownerSize.width - size.width)/2,
          ownerLoc.y + (ownerSize.height - size.height)/2);
    }
  }

  public void progressUpdated() {
    Runnable doSetProgressBarValue = new Runnable() {
      public void run() {
        final int val = (int)Math.round(worker.getTaskProgressValue());
        final int total = (int)Math.round(worker.getTotalProgressValue());
        /*
        TODO: ? make a new thread for updating the progress values, so that the updates can be deferred
        if (progressBar.getValue() == 100  &&   val == 0) {
          // Wait a bit before clearing the value, so the user sees
          // that the previous task was completed
          try { Thread.sleep(500); } catch (InterruptedException e) { }
        }
        */
        totalProgressBar.setValue(total);
        final String name = worker.getTaskName();
        totalStatusLabel.setText(name != null ? name : " ");

        final long timeLeft;

        if (doubleBarMode) {
          final String subName = worker.getSubtaskName();
          progressBar.setValue(val);
          statusLabel.setText(subName != null ? subName : " ");

          timeLeft = worker.getEstimatedTaskRemainingTime();
        } else {
          timeLeft = worker.getEstimatedRemainingTime();
        }

        if (timeLeft >= 0) {
          String timeLeftStr = DateUtils.timeIntervalToString(timeLeft);
          timeLeftLabel.setText(
              "Estimated time remaining" +
              (doubleBarMode ? " for the current step" : "") +
              ": " + timeLeftStr);
        } else {
          timeLeftLabel.setText(" ");
        }
      }
    };
    SwingUtilities.invokeLater(doSetProgressBarValue);
  }

  /**
   * This action listener, called by the "Cancel" button, interrupts
   * the worker thread which is running this.doWork().  Note that
   * the doWork() method handles InterruptedExceptions cleanly.
   */
  private final ActionListener interruptListener = new ActionListener() {
    public void actionPerformed(ActionEvent event) {
      worker.interrupt();
      interruptButton.setEnabled(false);

      // TODO: JOptionDialog - continue? cancel?
    }
  };

  public void processFinished() {
    dispose();
  }

  public void processCancelled() {
  }

}
