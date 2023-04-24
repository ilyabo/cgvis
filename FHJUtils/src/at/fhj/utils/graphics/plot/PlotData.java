package at.fhj.utils.graphics.plot;

/**
 * @author Ilya Boyandin
 *
 * $Revision: 1.1 $
 */
public interface PlotData {
  
  boolean hasData();

  int getSize();

  String getXLabel();

  String getYLabel();

  double getXValue(int index);

  double getYValue(int index);

}
