package evaluationfunctions.flocking;

import java.util.HashMap;
import java.util.Set;

import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;

public class ReynoldsLocallyWeights extends ReynoldsLocally {
	
	@ArgumentsAnnotation(name="alignmentWeight", defaultValue="1.0")	
	protected double alignmentWeight;
	
	@ArgumentsAnnotation(name="cohesionWeight", defaultValue="1.0")	
	protected double cohesionWeight;
	

	public ReynoldsLocallyWeights(Arguments args) {
		super(args);
		
		alignmentWeight = args.getArgumentIsDefined("alignmentWeight") ? args
				.getArgumentAsDouble("alignmentWeight") : 1.0;
				
		cohesionWeight = args.getArgumentIsDefined("cohesionWeight") ? args
						.getArgumentAsDouble("cohesionWeight") : 1.0;

	}
	
	public double getFitness() {

		return alignmentWeight*fitnessForAlignment/simulator.getTime() + cohesionWeight*fitnessForCohesion/simulator.getTime()-fitnessForSeparation/simulator.getTime()+fitnessForMovement ;
	}
	
}