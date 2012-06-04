import javax.media.opengl.GL2;

class RobotDrawer {
	GL2 gl;
	double r1, r2;
	
	RobotDrawer(GL2 gl, double r1, double r2) {
		this.gl = gl;
		this.r1 = r1;
		this.r2 = r2;
	}
	
	enum Part {
		HEAD(1, 1),
		BODY(2, 3),
		ARM( 1, 3),
		LEG( 1, 3);
		
		double w, h;
		
		Part(double w, double h) {
			this.w = w;
			this.h = h;
		}
	}
	
	void draw() {
		drawPart(Part.LEG, -(Part.BODY.w-Part.LEG.w)/2, 0, 0);
		drawPart(Part.LEG,  (Part.BODY.w-Part.LEG.w)/2, 0, 0);
		drawPart(Part.BODY,                          0, Part.LEG.h, 0);
		drawPart(Part.ARM, -(Part.BODY.w+Part.ARM.w)/2, Part.LEG.h+Part.BODY.h-Part.ARM.h, 0, r1);
		drawPart(Part.ARM,  (Part.BODY.w+Part.ARM.w)/2, Part.LEG.h+Part.BODY.h-Part.ARM.h, 0, r2);
		drawPart(Part.HEAD,                          0, Part.BODY.h + Part.LEG.h, 0);
	}
	
	void drawPart(Part part, double x, double y, double z, double r) {
		gl.glPushMatrix();
		gl.glTranslated(x, y, z);
		
		//TODO
		gl.glTranslated(0, part.h-part.w/2, 0);
		gl.glRotated(r, 1, 0, 0);
		gl.glTranslated(0, part.w/2-part.h, 0);
		
		drawCuboid(part);
		gl.glPopMatrix();
	}
	
	void drawPart(Part part, double x, double y, double z) {
		drawPart(part, x, y, z, 0);
	}
	
	void drawCuboid(Part p) {
		gl.glBegin(GL2.GL_LINES);
		
		gl.glColor3d(1, 1, 1);
		
		for (double vx : new double[] { -p.w/2, p.w/2 }) {
			for (double vz : new double[] { -p.w/2, p.w/2 }) {
				//vertical lines
				gl.glVertex3d(vx, 0,   vz);
				gl.glVertex3d(vx, p.h, vz);
			}
			
			for (double vy : new double[] { 0, p.h }) {
				//hoizontal lines parallel to z axis
				gl.glVertex3d(vx, vy, -p.w/2);
				gl.glVertex3d(vx, vy,  p.w/2);
				
				//hoizontal lines parallel to x axis
				gl.glVertex3d(-p.w/2, vy, vx);
				gl.glVertex3d( p.w/2, vy, vx);
			}
		}
		
		gl.glEnd();
	}
}
