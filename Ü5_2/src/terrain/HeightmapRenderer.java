package terrain;

import static java.lang.Math.max;
import static javax.media.opengl.GL2.*;

import gl.Util;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.awt.TextRenderer;

class HeightmapRenderer implements GLEventListener {
	static final int FLATTEN = 2;
	static final GLU glu = new GLU();
	static final String[] hudLines = new String[] {"Click to Focus", "WASD to Move", "Shift/Alt for Speed", "Esc to Unfocus"};
	
	GL2 gl;
	FPCamera cam;
	
	int[][] map;
	int mapWidth;
	int mapHeight;
	int max;
	
	public static void main(String[] args) {
		new HeightmapRenderer("heightmap.png");
	}
	
	HeightmapRenderer(String mapFileName) {
		BufferedImage mapImage;
		try {
			mapImage = ImageIO.read(HeightmapRenderer.class.getResource(mapFileName));
		} catch (IOException e) {
			e.printStackTrace();
			mapImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		}
		
		map = HeightMapLoader.fromImage(mapImage);
		mapWidth  = map.length;
		mapHeight = map[0].length;
		max = 0;
		for (int x=0; x<mapWidth; x++)
			for (int y=0; y<mapHeight; y++)
				max = max(max, map[x][y]);
		
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		
		GLCanvas canvas = new GLCanvas(caps);
		
		canvas.addGLEventListener(this);
		cam = new FPCamera(canvas);
		
		Frame frame = new Frame("Heightmap");
		frame.add(canvas);
		frame.setSize(500, 500);
		frame.setIconImage(mapImage);
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
		
		gl.glEnable(GL_DEPTH_TEST);
		gl.glEnable(GL_BLEND);
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL_LINE_SMOOTH);
		gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
		
		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	}
	
	public void display(GLAutoDrawable drawable) {
		gl = drawable.getGL().getGL2();
		
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glMultMatrixf(cam.calcModelView(), 0);
		
		Util.drawGrid(gl, mapWidth, mapHeight);
		drawHeightMap();
		
		drawHud(drawable);
	}
	
	void drawHud(GLAutoDrawable drawable) {
		int unit = drawable.getHeight() / 30;
		TextRenderer textRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, unit), true, true);
		
		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		
		textRenderer.setColor(0, 0, 0, 1);
		for (int l=0; l<hudLines.length; l++)
			textRenderer.draw(hudLines[l], unit, drawable.getHeight() - (2+l)*unit);
		
		textRenderer.endRendering();
		gl.glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		gl = drawable.getGL().getGL2();
		
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(90, (double)w / h, .001, 200);
	}
	
	public void dispose(GLAutoDrawable drawable) {}
	
	void drawHeightMap() {
		gl.glPushMatrix();
		
		gl.glTranslated(.5 - mapWidth/2, 0, .5 - mapHeight/2);
		
		for (int col=0; col<mapWidth-1; col++) {
			gl.glBegin(GL_QUAD_STRIP);
			
			for (int row=0; row<mapHeight; row++) {
				gl.glColor3f(0, (float) map[col][row] / max, 0);
				gl.glVertex3i(col  , map[col][row]/FLATTEN, row);
				
				gl.glColor3f(0, (float) map[col+1][row] / max, 0);
				gl.glVertex3i(col+1, map[col+1][row]/FLATTEN, row);
			}
			
			gl.glEnd();
		}
		
		gl.glPopMatrix();
	}
}

