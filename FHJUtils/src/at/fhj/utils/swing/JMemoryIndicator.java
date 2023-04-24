package at.fhj.utils.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import at.fhj.utils.misc.MemStatusListener;

/**
 * TODO: rewrite JMemoryIndicator using Swing Timer
 *
 * @author Ilya Boyandin
 */
public class JMemoryIndicator extends JComponent {

  private static boolean lowMemWarningShown = false;
  private final JLabel memStatus;
  private MemStatusUpdater memStatusUpdater;
  private final long updateRate;

  public JMemoryIndicator(long updateRate) {
    this.updateRate = updateRate;

    setLayout(new BorderLayout());

    memStatus = new JLabel();
    add(memStatus, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Runtime r = Runtime.getRuntime();
                long free;
                do {
                    free = r.freeMemory();
                    System.gc();
                } while (free > r.freeMemory());
            }
        });
        setToolTipText("Memory: [Used / Buffered by JVM / Max JVM buffer size]");
        setForeground(Color.gray);
        if (UIManager.getLookAndFeel().getID() == "Aqua") {
          setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 16));
        }
  }

  public void startUpdater() {
    if (memStatusUpdater == null) {
      memStatusUpdater = new MemStatusUpdater(new MemStatusListener() {
        public void memStatusUpdated(long used, long total, long max) {
          memStatus.setText("Used " + humanReadable(used) + " of " + humanReadable(total));
          if (!lowMemWarningShown  &&  (max - used <= max * 0.05)) {
            warnMemoryLow();
            lowMemWarningShown = true;
          }
        }

      }, updateRate);
      memStatusUpdater.start();
    }
  }

  private String humanReadable(long used) {
    return (used >= 1024 ? (used / 1024) + "M" : used + "K");
  }

  private static class MemStatusUpdater extends Thread {
    private final MemStatusListener memStatusListener;
    private final long repeatDelay;

    public MemStatusUpdater(MemStatusListener memStatusListener, long repeatDelay) {
      this.memStatusListener = memStatusListener;
      this.repeatDelay = repeatDelay;
    }

    @Override
        public void run() {
      while (true) {
        try {
          Runtime rt = Runtime.getRuntime();
          final long total = rt.totalMemory() / 1024;
          final long free = rt.freeMemory() / 1024;
          final long max = rt.maxMemory() / 1024;
          final long used = total - free;
          memStatusListener.memStatusUpdated(used, total, max);
          Thread.sleep(repeatDelay);
        } catch (InterruptedException ie) {
          // do nothing
        }
      }
    }
  }

  private void warnMemoryLow() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        JOptionPane.showMessageDialog(null,
            "Free heap memory size is very low. Please, increase\n" +
            "the maximum java heap size (using the -Xmx<size> JVM parameter)",
            "Warning",
            JOptionPane.WARNING_MESSAGE
        );
        // TODO: ? interrupt the worker or show option pane interrupt/continue
      }
    });
  }
}
