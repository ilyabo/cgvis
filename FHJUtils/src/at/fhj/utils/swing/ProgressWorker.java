package at.fhj.utils.swing;

import at.fhj.utils.misc.Interruptible;
import at.fhj.utils.misc.ProgressTracker;

/**
 * Job-progress-aware SwingWorker
 * 
 * @author Ilya Boyandin
 *
 * $Revision: 1.3 $
 */
public abstract class ProgressWorker extends SwingWorker {
  protected ProgressTracker progress;
  protected Interruptible process;
  
  public ProgressWorker(ProgressTracker progress) {
    this(progress, null);
  }
  
  public ProgressWorker(ProgressTracker progress, Interruptible process) {
    this.process = process;
    this.progress = progress;
  }
  
  public void interrupt() {
    super.interrupt();
    progress.processCancelled();
    if (process != null) {
      process.interrupt();
    }
  }

  public Interruptible getProcess() {
    return process;
  }

  public ProgressTracker getProgressTracker() {
    return progress;
  }

  public void finished() {
    progress.processFinished();
  }
  
  public double getTaskProgressValue() {
    return progress.getTaskProgress();
  }

  public String getSubtaskName() {
    return progress.getSubtaskName();
  }

  public String getTaskName() {
    return progress.getTaskName();
  }
  
  public double getTotalProgressValue() {
    return progress.getTotalProgress();
  }
  
  public long getEstimatedTaskRemainingTime() {
    return progress.getEstimatedTaskRemainingTime();
  }
  
  public long getEstimatedRemainingTime() {
    return progress.getEstimatedRemainingTime();
  }

}
