package sensors;

import mathutils.Vector2d;

import robots.JumpingRobot;
import simulation.Simulator;
import simulation.robot.Robot;
import simulation.robot.sensors.Sensor;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

/**
 * Alignment sensor to see the relative orientation regarding a fixed orientation
 * (average of the neighbours robots orientation)
 * @author Rita Ramos
 */

public class OrientationFixedPointSensor extends Sensor {
	private static final double FIXED_ORIENTATION = 0.0;	
	private Robot robot;

	public OrientationFixedPointSensor(Simulator simulator, int id, Robot robot,
			Arguments args) {
		super(simulator, id, robot, args);
		this.robot = robot;
	}

	@Override
	public double getSensorReading(int sensorNumber) {
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
	
}
