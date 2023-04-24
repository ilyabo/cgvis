package at.fhj.utils.swing;

import java.awt.Component;
import java.awt.EventQueue;

import javax.swing.JOptionPane;

import at.fhj.utils.misc.StringUtils;

public class JMsgPane extends JOptionPane {

  private static final int maxMessageLines = 32;
  private static final int maxCharactersPerLineCount = 64;

  public JMsgPane(Object message, int messageType) {
    super(message, messageType);
  }

  public JMsgPane(Object message, int messageType, int optionType) {
    super(message, messageType, optionType);
  }

  @Override
  public void setVisible(boolean aFlag) {
    super.setVisible(aFlag);
  }

  @Override
  public int getMaxCharactersPerLineCount() {
    return maxCharactersPerLineCount;
  }

  private static String message(Object obj) {
    String message;
    if (obj instanceof Exception) {
      Exception ex = (Exception)obj;
      message = ex.getMessage();
      if (message == null) {
        message = ex.getClass().getSimpleName();
      }
    } else {
      message = obj.toString();
    }
    return StringUtils.cutLinesOff(message, maxMessageLines, maxCharactersPerLineCount);
  }

  private static void showMessage(Component parent, Object message, String title, int type) {
    new JMsgPane(message, type).
      createDialog(parent, title).setVisible(true);
  }

  /**
   * This method shows an error dialog. It guarantees that the dialog will
   * be shown from the EDT.
   */
  public static void showErrorDialog(final Component parent, final Object message) {
    if (EventQueue.isDispatchThread()) {
      showMessage(parent, message(message), "Error", JMsgPane.ERROR_MESSAGE);
    } else {
      EventQueue.invokeLater(new Runnable() {
        public void run() {
          showMessage(parent, message(message), "Error", JMsgPane.ERROR_MESSAGE);
        }
      });
    }
  }

  /**
   * This method shows a problem dialog. It guarantees that the dialog will
   * be shown from the EDT.
   */
  public static void showProblemDialog(final Component parent, final Object message) {
    if (EventQueue.isDispatchThread()) {
      showMessage(parent, message(message), "Problem", JMsgPane.ERROR_MESSAGE);
    } else {
      EventQueue.invokeLater(new Runnable() {
        public void run() {
          showMessage(parent, message(message), "Problem", JMsgPane.ERROR_MESSAGE);
        }
      });
    }
  }

  /**
   * This method shows an info dialog. It guarantees that the dialog will
   * be shown from the EDT.
   */
  public static void showInfoDialog(final Component parent, final Object message) {
    if (EventQueue.isDispatchThread()) {
      showMessage(parent, message(message), "Info", JMsgPane.INFORMATION_MESSAGE);
    } else {
      EventQueue.invokeLater(new Runnable() {
        public void run() {
          showMessage(parent, message(message), "Info", JMsgPane.INFORMATION_MESSAGE);
        }
      });
    }
  }

}
