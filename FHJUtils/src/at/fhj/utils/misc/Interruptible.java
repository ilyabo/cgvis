package at.fhj.utils.misc;

/**
 * Represent a process which can be interrupted.
 * 
 * @author Ilya Boyandin
 *
 * $Revision: 1.2 $
 */
public interface Interruptible {

  void interrupt();

  boolean isInterrupted();

}
