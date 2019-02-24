
The following explanation can help you replicated (or extended) the experiments of the study "Evolving flocking in embodied agents based on local and global application of Reynolds' rules".

# Replicate the results


The arguments that we used for all our experiments:

* Robot: [DifferentialDriveRobot](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotSim/src/simulation/robot/DifferentialDriveRobot.java)

* Sensor: [OrientationRobotsAverageSensor](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotRita/src/sensors/OrientationRobotsAverageSensor.java) and [RobotSensor](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotSim/src/simulation/robot/sensors/RobotSensor.java)

* Actuator: [TwoWheelActuator](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotSim/src/evolutionaryrobotics/neuralnetworks/outputs/TwoWheelNNOutput.java)

* Controller: [NeuralNetworkController](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotSim/src/evolutionaryrobotics/neuralnetworks/NeuralNetworkController.java)

* NeuralNetwork: [CTRNNMultilayer](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotSim/src/evolutionaryrobotics/neuralnetworks/CTRNNMultilayer.java)

* Population: [MuLambdaPopulation](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotEvolver/src/evolutionaryrobotics/populations/MuLambdaPopulation.java)
 
* Environment: [EmptyEnviromentsWithFixPositions](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotRita/src/environment/EmptyEnviromentsWithFixPositions.java)

* Evolution: [GenerationalEvolution](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotEvolver/src/evolutionaryrobotics/evolution/GenerationalEvolution.java)


Only the evaluation function was choosen differently for each experimental setup:

* Evaluation function - Local Setup: [ReynoldsLocally](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotRita/src/evaluationfunctions/flocking/ReynoldsLocally.java)

* Evaluation function - Global Setup: [ReynoldsGlobal](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotRita/src/evaluationfunctions/flocking/ReynoldsGlobal.java)

* Evaluation function - No Alignment Setup: [ReynoldsGlobalWithoutAlignment](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotRita/src/evaluationfunctions/flocking/ReynoldsGlobalWithoutAlignment.java)

The complete configuration file of each setup can be seen inside each subfolder (GlobalSetup, LocalSetup or NoAlignmentSetup), within the file _arguments.conf 

<br>
<h1> See Results using GUI: </h1>

Navigate to JBotRita directory and launch the program using ritaMain.java, in which you can then see our experiment results. Choose one of the folders (GlobalSetup, LocalSetup or NoAlignmentSetup) and click the respective _showbest_current.conf. 
We will then see the evolved behaviour of the highest scoring controller of the respective setup. 

![alt text](https://github.com/RitaRamo/flocking-evolver/blob/rita/JBotRita/experiments/GUI.png)


More info [1]:
* **_showbest_current.conf:** loads the best controller for the current generation
* **_arguments.conf:** has a copy of the original configuration file used to execute the experiment
* **_generationnumber:** indicates the current generation number
* **_fitness.log:** saves the best, average and minimum fitness for the controllers of each generation
* **_restartevolution.conf:** this file can be used to resume an experiment that was interrupted (not with the GUI)
* **populations:** includes the serialized version of the controllers at each generation (In our case, we just included the last generation, given the amount of time to copy all generations to the respective folder)
* **posMetrics:** includes the metrics used for the study (<em> Order Metric</em> - Alignment.txt; <em> Number of Groups  </em> - NumberOfGroupsAsNumber.txt ; <em>Swarm Cohesion </em>- NG_PercentageOfFragmentation.txt

Reference:
[1]- https://github.com/BioMachinesLab/jbotevolver/wiki/Configuration-Panel

