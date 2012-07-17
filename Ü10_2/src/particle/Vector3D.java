package particle;

public class Vector3D implements Cloneable {
	public double x;
	public double y;
	public double z;
	
	public Vector3D() {
		this(0, 0, 0);
	}
	
	public Vector3D(Vector3D v) {
		this(v.x, v.y, v.z);
	}
	
	public Vector3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void move(Vector3D v) {
		move(v.x, v.y, v.z);
	}
	
	private void move(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
	}
	
	public Vector3D add(Vector3D v) {
		return add(v.x, v.y, v.z);
	}
	
	public Vector3D add(double x, double y, double z) {
		return new Vector3D(this.x+x, this.y+y, this.z+z);
	}
	
	public Vector3D clone() {
		return new Vector3D(x, y, z);
	}
}
