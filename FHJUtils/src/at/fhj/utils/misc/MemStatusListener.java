package at.fhj.utils.misc;

/**
 * @author Ilya Boyandin
 */
public interface MemStatusListener {

  void memStatusUpdated(long used, long total, long max);

}
