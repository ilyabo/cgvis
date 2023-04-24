package at.fhj.utils.misc;

/**
 * 
 * @author Ilya Boyandin
 *
 * $Revision: 1.1 $
 */
public interface ProgressListener {
  
  void progressUpdated();
  
  void processFinished();
  
  void processCancelled();
}
