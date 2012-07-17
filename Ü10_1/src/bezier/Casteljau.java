package bezier;

import java.awt.geom.Point2D;

public class Casteljau {
	public Point2D.Double[] ctrlPoints;
	public Point2D.Double[] curvePoints;
	public int steps;
	
	public Casteljau(Point2D.Double[] ctrlPoints, int steps) {
		this.ctrlPoints = ctrlPoints;
		this.curvePoints = new Point2D.Double[steps];
		this.steps = steps;
		
		final double step = 1.0 / steps;
		for (int s=0; s<steps; s++)
			curvePoints[s] = getCasteljauPoint(s * step);
	}
	
	public Point2D.Double getCasteljauPoint(double t) {
		Point2D.Double[] tempPoints = new Point2D.Double[ctrlPoints.length];
		
		ctrlPoints[0].clone();
		
		for (int i = 0; i < ctrlPoints.length; i++)
			tempPoints[i] = (Point2D.Double) ctrlPoints[i].clone();
		
		for (int k = 1; k < ctrlPoints.length; k++)
			for (int i = 0; i < ctrlPoints.length - k; i++) {
				tempPoints[i].x = t * tempPoints[i].x + (1 - t) * tempPoints[i + 1].x;
				tempPoints[i].y = t * tempPoints[i].y + (1 - t) * tempPoints[i + 1].y;
			}
		
		return tempPoints[0];
	}
}
