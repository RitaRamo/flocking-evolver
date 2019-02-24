
# Replicate the results


The arguments that we used for all our experiments:

* Robot: DifferentialDriveRobot

* Sensor: OrientationRobotsAverageSensor and RobotSensor

* Actuator: TwoWheelActuator

* Controller: NeuralNetworkController

* NeuralNetwork: CTRNNMultilayer

* Population: MuLambdaPopulation

* Environment: EmptyEnviromentsWithFixPositions

* Evolution: GenerationalEvolution


Only the evaluation function was choosen differently for each experimental setup:

* Evaluation function - Local Setup: ReynoldsLocally

* Evaluation function - Global Setup: ReynoldsGlobal

* Evaluation function - No Alignment Setup: ReynoldsGlobalWithoutAlignment


The complete configuration file of each setup can be seen inside each subfolder (GlobalSetup, LocalSetup or NoAlignmentSetup), within the file _arguments.conf 


See the results with GUI:

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
* **show_best:** includes configuration files that allow you to quickly check the behavior of the best controllers at each generation

Reference:
[1]- https://github.com/BioMachinesLab/jbotevolver/wiki/Configuration-Panel

