package sensors;

import mathutils.Vector2d;

import robots.JumpingRobot;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.robot.sensors.Sensor;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

/**
 * Alignment sensor to see the relative orientation regarding others robots
 * (average of the neighbours robots orientation)
 * @author Rita Ramos
 */

public class OrientationFixedPointSensor extends Sensor {
	private static final double FIXED_ORIENTATION = 0.0;
	
	private Simulator simulator;
	private Robot robot;
	protected boolean rangedIncreased = false;

	
	@ArgumentsAnnotation(name = "range", help = "Range of the sensor.", defaultValue = "1.0")
	protected double range = 1.0;

	@ArgumentsAnnotation(name = "increaseRange", help = "Increase range of the sensor while jumping.", defaultValue = "1.0")
	protected double increaseRange = 1.0;

	public OrientationFixedPointSensor(Simulator simulator, int id, Robot robot,
			Arguments args) {
		super(simulator, id, robot, args);
		this.simulator = simulator;
		this.robot = robot;
		range = (args.getArgumentIsDefined("range")) ? args
				.getArgumentAsDouble("range") : 1.0;
		increaseRange = (args.getArgumentIsDefined("increaseRange")) ? args
				.getArgumentAsDouble("increaseRange") : 1.0;
	}

	@Override
	public double getSensorReading(int sensorNumber) {
		Vector2d robotPosition = robot.getPosition();

		double differenceOfOrientation = calculateDifferenceBetweenAngles(
				Math.toDegrees(FIXED_ORIENTATION),
				Math.toDegrees(robot.getOrientation())); // [-180,180]
		
		return 0.5 * (differenceOfOrientation) / 180 + 0.5; // [0,1] 
		// if diff -179 -> 0;
		// if diff 180 -> 1;
		// if diff 0 -> 0.5 (max score)
	}

	private double calculateDifferenceBetweenAngles(double secondAngle,
			double firstAngle) {
		double difference = secondAngle - firstAngle;
		while (difference < -180)
			difference += 360;
		while (difference > 180)
			difference -= 360;
		return difference;
	}

	@Override
	public String toString() {
		return "RotationRobotsGlobalSensor [" + getSensorReading(0) + "]";
	}
	
	private void rangeBackToDefault() {
		if (rangedIncreased == true) {
			rangedIncreased = false;
			range = range - increaseRange;
		}
	}
}
