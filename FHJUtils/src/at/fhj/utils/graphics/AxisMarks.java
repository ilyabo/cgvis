package at.fhj.utils.graphics;

/**
 * 
 * @author Ilya Boyandin
 *
 * $Revision: 1.2 $
 */
public class AxisMarks {
  private static double EPS = 1e-07;
  
  private int xMinDist;
  private int xMaxDist;
  private int yMinDist;
  private int yMaxDist;
  
  private int plotWidth; 
  private int plotHeight; 

  double xStart;
  double xStep; 
  double yStart;
  double yStep; 

  public AxisMarks(int xMinDist, int xMaxDist, int yMinDist, int yMaxDist) {
    this.xMinDist = xMinDist;
    this.xMaxDist = xMaxDist;
    this.yMinDist = yMinDist;
    this.yMaxDist = yMaxDist;
  }
  
  public void setPlotSize(int width, int height) {
    plotWidth = width;
    plotHeight = height;
  }
  
  private transient double start; // temporary
  private transient double step;

  public void calc(double xMinV, double xMaxV, double yMinV, double yMaxV) {
    if (plotWidth <= 0  ||  plotHeight <= 0) {
      return;
    }
    calc(xMinV, xMaxV, xMinDist, xMaxDist, plotWidth);
    xStart = start; xStep = step;
    
    calc(yMinV, yMaxV, yMinDist, yMaxDist, plotHeight);
    yStart = start; yStep = step;
  }
  
  private void calc(double minV, double maxV, int minDist, int maxDist, int width) {
    if (Math.abs(minV - maxV) < EPS) {  // equal
      start = minV;
      step = 0;
      return;
    } else if (maxV < minV) {
      // Wrong parameters order, so just swap them
      final double t = minV;
      minV = maxV;
      maxV = t;
    }
    
    // Step
    final double len = (maxV - minV);
    int numAxes = (int)Math.round(width / ((maxDist + minDist) / 2));
    if (numAxes <= 0) {
      numAxes = (int)Math.round(width / minDist);
      if (numAxes <= 0) {
        numAxes = 1;
      }
    }
    
    
    final double d = len / numAxes;

    final double ord = ordAlpha(d);
    final double ord10 = ordAlpha(d * 10);
    final double ord01 = ordAlpha(d / 10);

    
    final double ord10Dist = width / (len / ord10);
    final double ordDist = width / (len / ord);
    final double ord01Dist = width / (len / ord01);

    // Look first if one of the ord's suits straight away
    double _step = 0;
    if (ord10Dist >= minDist  &&  ord10Dist <= maxDist) {
      _step = ord10;
    } else if (ordDist >= minDist   &&  ordDist <= maxDist) {
      _step = ord;
    } else if (ord01Dist >= minDist   &&  ord01Dist <= maxDist) {
      _step = ord01;
    }
    
    if (_step == 0) {
      // Try a multiplied ord
      if (ord10Dist <= minDist) {
        _step = ord10;
        int i = 2;
        while (width / (len / _step) < minDist) {
          _step = ord10 * i++;
        }
      } else if (ordDist <= minDist) {
        _step = ord;
        int i = 2;
        while (width / (len / _step) < minDist) {
          _step = ord * i++;
        }
      } else if (ord01Dist <= minDist) {
        _step = ord01;
        int i = 2;
        while (width / (len / _step) < minDist) {
          _step = ord01 * i++;
        }          
      }
    }
    
    if (_step == 0) {  // It seems that all of the ords are too wide,
              // so fallback to the smallest 
      _step = ord01;
    }
    
    // Start
    start = _step * Math.ceil(minV / _step);  // start ord must be equal to the
                          // ord of _step or zero
    if (Math.abs(start) <= EPS) {
      start = 0;  // to avoid -0.0
    }

    if (numAxes > 1) {
      step = _step;
    } else {
      step = 0;
    }
  }
  
  public double getXStart() {
    return xStart;
  }
  
  public double getXStep() {
    return xStep;
  }
  
  public double getYStart() {
    return yStart;
  }
  
  public double getYStep() {
    return yStep;
  }
  
  public static double ordAlpha(double x) {
    if (Double.isNaN(x)) {
      return Double.NaN;
    }
    if (Double.isInfinite(x)) {
      return x > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }
    int sgn;
    if (x == 0) return 0;
    if (x < 0) {
      x = -x;
      sgn = -1;
    } else {
      sgn = 1;
    }
    double ord = 1;
    if (x >= 1) {
      while (x >= 10) { x /= 10; ord *= 10; }
    } else  {
      while (x < 1) { x *= 10; ord /= 10; }
    }
    return sgn * ord;
  }
}