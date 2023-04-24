package at.fhj.utils.graphics.plot;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * 
 * @author Ilya Boyandin
 *
 * $Revision: 1.1 $
 */
public interface ImagePainter {
  
  Dimension getSize();
  void paintImage(Graphics g, int x, int y);
  
}
