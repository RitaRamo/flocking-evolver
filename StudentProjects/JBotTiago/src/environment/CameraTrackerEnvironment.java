package environment;

import java.util.LinkedList;
import java.util.Random;

import mathutils.Vector2d;
import simulation.Simulator;
import simulation.environment.Environment;
import simulation.physicalobjects.PhysicalObjectType;
import simulation.physicalobjects.Prey;
import simulation.physicalobjects.Wall;
import simulation.robot.Robot;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;
import camera.CameraTracker;

public class CameraTrackerEnvironment extends Environment {
	private static final double PREY_RADIUS = 0.025;
	private static final double PREY_MASS = 1;
	
	private LinkedList<Wall> walls;
	private Random random;
	private int preysCaught = 0;
	@ArgumentsAnnotation(name="numberofpreys", defaultValue = "1")
	private int numberOfPreys = 1;
	private double consumingDistance = 0.15;
	@ArgumentsAnnotation(name="lag", defaultValue = "0")
	private int lag = 0;
	@ArgumentsAnnotation(name="orientationerror", defaultValue = "0.0")
	private double orientationError = 0;
	
	public CameraTrackerEnvironment(Simulator simulator, Arguments args) {
		super(simulator, args);
		this.random = simulator.getRandom();
		
		walls = new LinkedList<Wall>();
		numberOfPreys = args.getArgumentAsIntOrSetDefault("numberofpreys", numberOfPreys);
		lag = args.getArgumentAsIntOrSetDefault("lag", lag);
		orientationError = args.getArgumentAsDoubleOrSetDefault("orientationerror", orientationError);
	}

	@Override
	public void setup(Simulator simulator) {
		super.setup(simulator);
		
		for (int i = 0; i < numberOfPreys; i++)
			addPrey(new Prey(simulator, "Prey_"+i, newRandomPosition(), 0, PREY_MASS, PREY_RADIUS));
		
		// Parede do mapa
		walls.add(new Wall(simulator, "topWall", 0, height/2, Math.PI, 1, 1, 0, width, 0.05, PhysicalObjectType.WALL));
		walls.add(new Wall(simulator, "bottomWall", 0, -height/2, Math.PI, 1, 1, 0, width, 0.05, PhysicalObjectType.WALL));
		walls.add(new Wall(simulator, "leftWall", -width/2, 0, Math.PI, 1, 1, 0, 0.05, height, PhysicalObjectType.WALL));
		walls.add(new Wall(simulator, "rightWall", width/2, 0, Math.PI, 1, 1, 0, 0.05, height, PhysicalObjectType.WALL));
		for (Wall wall : walls) 
			addObject(wall);
		
		for (Robot r : getRobots()) {
			r.setOrientation(random.nextDouble()*(2*Math.PI));
			
			double max = 0.5;
			r.setPosition(random.nextDouble()*max-max/2,random.nextDouble()*max-max/2);	
		}
		
		CameraTracker tracker = new CameraTracker(simulator, lag, orientationError);
		simulator.addCallback(tracker);
	}
	
	private Vector2d newRandomPosition() {
		double x = random.nextDouble() * (width - 0.1) - ((width-0.1)/2);
		double y = random.nextDouble() * (height - 0.1) - ((height-0.1)/2);
		return new Vector2d(x, y);
	}
	
	@Override
	public void update(double time) {
		for (Prey p : getPrey()) {
			for(Robot r : getRobots()) {
				if(r.getPosition().distanceTo(p.getPosition()) < consumingDistance){
					placePrey(p);
					preysCaught++;
				}
			}
		}
	}
	
	private void placePrey(Prey prey) {
		prey.teleportTo(newRandomPosition());
	}

	public int getPreysCaught() {
		return preysCaught;
	}
	
}
