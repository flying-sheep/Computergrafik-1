package particle;

public class Particle {
	private static final int MAX_LIFE = 100;
	private static final double GRAVITY = .0007;
	
	public Vector3D initPos;
	public Vector3D initMomentum;
	public GLColor initColor;
	
	public Vector3D pos;
	public Vector3D momentum;
	public GLColor color;
	
	public int lifetime;
	
	public Particle(Vector3D initPos, Vector3D initMomentum, GLColor initColor) {
		this.initPos      = initPos;
		this.initMomentum = initMomentum;
		this.initColor    = initColor;
		reset();
	}
	
	public void reset() {
		pos      = initPos.clone();
		momentum = initMomentum.clone();
		color    = initColor.clone();
		lifetime = 0;
	}
	
	public void tick() {
		pos.move(momentum);
		momentum.y -= GRAVITY;
		color.a -= 1f / MAX_LIFE;
		if (++lifetime == MAX_LIFE)
			reset();
		if (pos.y < 0) {
			lifetime = MAX_LIFE - 1;
			color = new GLColor(1, 1, 1);
		}
	}
}
