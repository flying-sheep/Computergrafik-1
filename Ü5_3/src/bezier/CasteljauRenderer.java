package bezier;

import static java.lang.Math.max;
import static javax.media.opengl.GL2.*;

import java.awt.Frame;
import java.awt.event.*;
import java.awt.geom.Point2D;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.awt.TextRenderer;

public class CasteljauRenderer implements GLEventListener {
	static final GLU glu = new GLU();
	/** Refinement steps to be performed */
	static final int REFINEMENTS = 6;
	
	/** Points of all refinements. End points of one curve are start points of another. */
	Point2D.Float[][] refined;
	/** Number of control points in the input curve */
	int arity;
	
	GL2 gl;
	
	public static void main(String[] args) {
		Point2D.Float[] points = new Point2D.Float[] {
			new Point2D.Float(-4,-4),
			new Point2D.Float(-3, 3),
			new Point2D.Float( 3, 5),
			new Point2D.Float( 5,-5),
		};
		new CasteljauRenderer(points);
	}
	
	CasteljauRenderer(Point2D.Float[] points) {
		arity = points.length;
		
		refined = new Point2D.Float[REFINEMENTS][];
		refined[0] = points;
		for (int r=1; r<REFINEMENTS; r++)
			refined[r] = refinedCurve(refined[r-1]);
		
		final GLProfile glp = GLProfile.getDefault();
		final GLCapabilities caps = new GLCapabilities(glp);
		
		final GLCanvas canvas = new GLCanvas(caps);
		
		canvas.addGLEventListener(this);
		
		final Frame frame = new Frame("Casteljau");
		frame.add(canvas);
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	
	public void init(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		
		gl.glClearColor(1, 1, 1, 1);
		
		gl.glEnable(GL_BLEND);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL_LINE_SMOOTH);
		gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(0, 0, 1, 0, 0, 0, 0, 1, 0);
	}
	
	public void display(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		
		gl.glClear(GL_COLOR_BUFFER_BIT);
		
		drawGrid(drawable);
		
		for (int r=0; r<refined.length; r++) {
			if (r<=2 || r==refined.length-1)
				drawBezierLines(r, r<=1);
		}
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		gl = drawable.getGL().getGL2();
		
		final int gridStep = h/12;
		
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-w/2/gridStep, w/2/gridStep, -h/2/gridStep, h/2/gridStep, 0, 1);
	}
	
	public void dispose(GLAutoDrawable drawable) {}
	
	void drawGrid(GLAutoDrawable drawable) {
		gl.glBegin(GL_LINES);
		
		gl.glColor4f(0, 0, 0, .1f);
		
		final int hwidth  = drawable.getWidth()  / 2;
		final int hheight = drawable.getHeight() / 2;
		
		for (int x=0; x<=hwidth; x++) {
			gl.glVertex2i( x,-hheight);
			gl.glVertex2i( x, hheight);
			gl.glVertex2i(-x,-hheight);
			gl.glVertex2i(-x, hheight);
		}
		for (int y=0; y<=hheight; y++) {
			gl.glVertex2i(-hwidth, y);
			gl.glVertex2i( hwidth, y);
			gl.glVertex2i(-hwidth,-y);
			gl.glVertex2i( hwidth,-y);
		}
		
		gl.glEnd();
	}
	
	void drawBezierLines(int step, boolean dots) {
		gl.glColor3f((float)step / 5, 0, 0);
		
		gl.glBegin(GL_LINE_STRIP);
		for (Point2D.Float p : refined[step])
			gl.glVertex2f(p.x, p.y);
		gl.glEnd();
		
		if (dots) {
			gl.glPointSize((refined.length - step) * 1.5f);
			gl.glBegin(GL_POINTS);
			for (Point2D.Float p : refined[step])
				gl.glVertex2f(p.x, p.y);
			gl.glEnd();
		}
	}
	
	/** Refines a series of points representing a series of bezier curves. Calls {@link refineCurve} per curve.
	 * 
	 * @param points Points of the current refinement
	 * @return Points of the next Casteljau iteration */
	Point2D.Float[] refinedCurve(Point2D.Float[] points) {
		int curves = (points.length - 1) / (arity - 1);
		Point2D.Float[] refined = new Point2D.Float[curves * (arity*2 - 2) + 1];
		
		for (int c=0; c<curves; c++)
			refineCurve(points, refined, c);
		
		return refined;
	}
	
	/** Performs one iteraton of the casteljau algorithm on the {@link c}th bezier curve
	 * 
	 * @param points Points of the unrefined bezier curves
	 * @param refined Points of the refined bezier curves currently being written
	 * @param c Number of bezier curve in {@link refined} to refine into {@link refined} */
	void refineCurve(Point2D.Float[] points, Point2D.Float[] refined, int c) {
		final int srcStart = c * (arity   - 1);
		final int trgStart = c * (arity*2 - 2);
		
		//holds all the curves’s controlpoints.
		//first the <arity> initial ones,
		//then the <arity>-1 middles, …
		Point2D.Float[][] controlPoints = new Point2D.Float[arity][];
		
		for (int depth=0; depth<arity; depth++) {
			controlPoints[depth] = new Point2D.Float[arity - depth];
			
			for (int m=0; m<arity-depth; m++) {
				controlPoints[depth][m] = (depth == 0)
					? points[srcStart+m]
					: middle(controlPoints[depth-1][m], controlPoints[depth-1][m+1]);
			}
			
			refined[trgStart                 + depth] = controlPoints[depth][0];
			refined[trgStart + (arity*2 - 2) - depth] = controlPoints[depth][controlPoints[depth].length-1];
		}
	}
	
	static Point2D.Float middle(Point2D.Float p1, Point2D.Float p2) {
		return new Point2D.Float((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
	}
}


