package main;
import evolutionaryrobotics.evaluationfunctions.EvaluationFunction;
import evolutionaryrobotics.evolution.Evolution;
import evolutionaryrobotics.neuralnetworks.NeuralNetwork;
import evolutionaryrobotics.populations.Population;
import gui.renderer.Renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import simulation.Simulator;
import simulation.environment.Environment;
import simulation.robot.Robot;
import simulation.robot.actuators.Actuator;
import simulation.robot.sensors.Sensor;
import simulation.util.Arguments;
import simulation.util.ArgumentsAnnotation;
import taskexecutor.TaskExecutor;
import utils.AutomatorOptions;
import utils.AutomatorOptionsAttribute;
import utils.ConfigurationResult;
import utils.RobotsResult;
import controllers.Controller;

public class ConfigurationAutomatorGui {
	
	private static final int OPTIONS_GRID_LAYOUT_SIZE = 15;
	private String[] keys = {"--output","--robots", "--controllers", "--population", "--environment", "--executor", "--evolution", "--evaluation", "--random-seed"};

	private JFrame frame;
	
	private Renderer renderer;
	
	private JPanel optionsPanelLeft;
	private JPanel optionsPanelCenter;
	private JPanel optionsPanelRight;
	
	private JPanel rendererPanel;
	
	private JTextArea configResult;
	
	private JTextArea helpArea;
	
	private JTextField outputTextField;
	private JTextField randomSeedTextField;
	
	private JButton optionsButton;
	private JButton saveArgumentsFileButton;
	private JButton jListRemoveButton;

	private JComboBox<String> robotsClassNameComboBox;
	private JComboBox<String> sensorsComboBox;
	private JComboBox<String> actuatorsComboBox;
	private JComboBox<String> controllersClassNameComboBox;
	private JComboBox<String> networkComboBox;
	private JComboBox<String> populationClassNameComboBox;
	private JComboBox<String> environmentClassNameComboBox;
	private JComboBox<String> executorClassNameComboBox;
	private JComboBox<String> evolutionClassNameComboBox;
	private JComboBox<String> evaluationClassNameComboBox;
	
	private ArrayList<AutomatorOptionsAttribute> optionsAttributes;
	
	private DefaultListModel<String> sensorsActuatorsListModel;
	private JList<String> sensorsActuatorsList;

	private AutomatorOptions currentOptions;
	private String currentClassName;
	
	private String editedAttributeName;
	
	private RobotsResult robotConfig;
	private ConfigurationResult result ;

	public ConfigurationAutomatorGui() {
		
		optionsAttributes = new ArrayList<AutomatorOptionsAttribute>();
		
		robotConfig = new RobotsResult();
		result = new ConfigurationResult(keys);
		
		frame = new JFrame("Configuration File Automator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1280, 860);
	
		frame.getContentPane().add(initLeftWrapperPanel(), BorderLayout.WEST);
		frame.getContentPane().add(initCenterWrapperPanel(), BorderLayout.CENTER);
		frame.getContentPane().add(initRightWrapperPanel(), BorderLayout.EAST);
		
		initListeners();
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
	}

	private Component initCenterWrapperPanel() {
		JPanel sidePanel = new JPanel(new BorderLayout());
		
		JPanel argumentsWrapper = new JPanel();
		argumentsWrapper.setLayout(new BorderLayout());
				
		JPanel north = new JPanel(new BorderLayout());
		JPanel subNorth = new JPanel(new BorderLayout());
		
		JPanel subPanelNorth = new JPanel(new GridLayout(1,1));
		subPanelNorth.add(initOutputWrapperPanel()); // 1. Output

		JPanel subPanelNorth2 = new JPanel(new GridLayout(1,1));
		subPanelNorth2.add(initRobotsWrapperPanel()); // 2. Robots
		
		JPanel subPanelNorth3 = new JPanel(new GridLayout(1,1));
		subPanelNorth3.add(initControllersWrapperPanel()); // 3. Controllers
		
		subNorth.add(subPanelNorth, BorderLayout.NORTH);
		subNorth.add(subPanelNorth2);
		subNorth.add(subPanelNorth3, BorderLayout.SOUTH);
		
		north.add(subNorth, BorderLayout.NORTH);
		north.add(initMiscWrapperPanel());
		
		argumentsWrapper.add(north, BorderLayout.NORTH);
		argumentsWrapper.setBorder(BorderFactory.createTitledBorder("Arguments"));

		sidePanel.add(argumentsWrapper, BorderLayout.NORTH);
		
		rendererPanel = new JPanel(new BorderLayout());
		rendererPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
		sidePanel.add(rendererPanel);
		
		return sidePanel;
	}
	
	private Component initLeftWrapperPanel() {
		JPanel fileContentWrapper = new JPanel();
		fileContentWrapper.setPreferredSize(new Dimension(370,370));
		fileContentWrapper.setLayout(new BorderLayout());
		
		JPanel resultContentWrapper = new JPanel(new BorderLayout());
		configResult = new JTextArea();
		configResult.setEditable(false);
		updateConfigurationText();
		resultContentWrapper.add(new JScrollPane(configResult));
		resultContentWrapper.setBorder(BorderFactory.createTitledBorder("Result"));
		
		resultContentWrapper.add(createResultContentButtons(), BorderLayout.SOUTH);
		fileContentWrapper.add(resultContentWrapper, BorderLayout.CENTER);
		
		JPanel jListContentWrapper = new JPanel(new BorderLayout());
		sensorsActuatorsListModel = new DefaultListModel<String>();
		sensorsActuatorsList = new JList<String>(sensorsActuatorsListModel);
		jListRemoveButton = new JButton("Remove");
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(jListRemoveButton);
		
		jListContentWrapper.add(new JScrollPane(sensorsActuatorsList));
		jListContentWrapper.add(buttonsPanel, BorderLayout.SOUTH);
		jListContentWrapper.setBorder(BorderFactory.createTitledBorder("Attributes List"));
		fileContentWrapper.add(jListContentWrapper, BorderLayout.SOUTH);
	
		return fileContentWrapper;
	}
	
	private Component initRightWrapperPanel() {
		JPanel rightWrapper = new JPanel();
		rightWrapper.setPreferredSize(new Dimension(410,frame.getHeight()));
		rightWrapper.setLayout(new BorderLayout());
		
		optionsPanelLeft = new JPanel();
		optionsPanelCenter = new JPanel();
		optionsPanelRight = new JPanel();
		
		optionsButton = new JButton("Add to File");
		optionsButton.setVisible(false);
		
		JPanel borderLayout = new JPanel(new BorderLayout());
		
		JPanel splitLayout = new JPanel(new BorderLayout());
		splitLayout.add(optionsPanelLeft, BorderLayout.WEST);
		splitLayout.add(optionsPanelCenter, BorderLayout.CENTER);
		splitLayout.add(optionsPanelRight, BorderLayout.EAST);
		borderLayout.add(new JScrollPane(splitLayout,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		
		helpArea = new JTextArea();
		Border border = BorderFactory.createLineBorder(Color.BLACK, 1);
		helpArea.setWrapStyleWord(true);
		helpArea.setLineWrap(true);
		helpArea.setEnabled(false);
		helpArea.setBorder(border);
		helpArea.setVisible(false);
		helpArea.setPreferredSize(new Dimension(borderLayout.getWidth(), 45));
		borderLayout.add(helpArea,BorderLayout.SOUTH);
		
		rightWrapper.add(borderLayout);
		rightWrapper.add(optionsButton, BorderLayout.SOUTH);
		rightWrapper.setBorder(BorderFactory.createTitledBorder("Options"));
		return rightWrapper;
	}

	private Component initOutputWrapperPanel() {
		JPanel outputWrapper = new JPanel();
		outputWrapper.setLayout(new GridLayout(2,1));
		
		outputTextField = new JTextField(20);
		randomSeedTextField = new JTextField(10);
		
		outputWrapper.add(new JLabel("Output Name: "));
		outputWrapper.add(outputTextField);
		outputWrapper.add(new JLabel("Random Seed: "));
		outputWrapper.add(randomSeedTextField);

		outputWrapper.setBorder(BorderFactory.createTitledBorder(""));
		return outputWrapper;
	}

	private Component initRobotsWrapperPanel() {
		JPanel robotsWrapper = new JPanel();
		robotsWrapper.setLayout(new GridLayout(3,1));
		
		robotsClassNameComboBox = loadRobotsClassNamesToComboBox(Robot.class);
		sensorsComboBox = loadRobotsClassNamesToComboBox(Sensor.class);
		actuatorsComboBox = loadRobotsClassNamesToComboBox(Actuator.class);
		
		robotsWrapper.add(new JLabel("Robot: "));
		robotsWrapper.add(robotsClassNameComboBox);
		
		robotsWrapper.add(new JLabel("Sensors: "));
		robotsWrapper.add(sensorsComboBox);
		
		robotsWrapper.add(new JLabel("Actuators: "));
		robotsWrapper.add(actuatorsComboBox);

		robotsWrapper.setBorder(BorderFactory.createTitledBorder(""));
		return robotsWrapper;
	}
	
	private Component initControllersWrapperPanel() {
		JPanel controllersWrapper = new JPanel();
		controllersWrapper.setLayout(new GridLayout(2,1));
		
		controllersClassNameComboBox = loadRobotsClassNamesToComboBox(Controller.class);
		networkComboBox = loadRobotsClassNamesToComboBox(NeuralNetwork.class);
		
		controllersWrapper.add(new JLabel("Controller: "));
		controllersWrapper.add(controllersClassNameComboBox);
		
		controllersWrapper.add(new JLabel("Network: "));
		controllersWrapper.add(networkComboBox);
		
		controllersWrapper.setBorder(BorderFactory.createTitledBorder(""));
		return controllersWrapper;
	}

	private Component initMiscWrapperPanel() {
		JPanel miscWrapper = new JPanel();
		miscWrapper.setLayout(new GridLayout(5,1));
		
		populationClassNameComboBox = loadRobotsClassNamesToComboBox(Population.class);
		miscWrapper.add(new JLabel("Population: "));
		miscWrapper.add(populationClassNameComboBox);
		
		environmentClassNameComboBox = loadRobotsClassNamesToComboBox(Environment.class);
		miscWrapper.add(new JLabel("Environment: "));
		miscWrapper.add(environmentClassNameComboBox);
		
		executorClassNameComboBox = loadRobotsClassNamesToComboBox(TaskExecutor.class);
		miscWrapper.add(new JLabel("Executor: "));
		miscWrapper.add(executorClassNameComboBox);
		
		evolutionClassNameComboBox = loadRobotsClassNamesToComboBox(Evolution.class);
		miscWrapper.add(new JLabel("Evolution: "));
		miscWrapper.add(evolutionClassNameComboBox);
		
		evaluationClassNameComboBox = loadRobotsClassNamesToComboBox(EvaluationFunction.class);
		miscWrapper.add(new JLabel("Evaluation: "));
		miscWrapper.add(evaluationClassNameComboBox);
		
		miscWrapper.setBorder(BorderFactory.createTitledBorder(""));
		return miscWrapper;
	}
	
	private void initListeners() {
		outputTextField.getDocument().addDocumentListener(new TextFieldListener(outputTextField, AutomatorOptions.OUTPUT));
		randomSeedTextField.getDocument().addDocumentListener(new TextFieldListener(randomSeedTextField, AutomatorOptions.RANDOM_SEED));
		robotsClassNameComboBox.addActionListener(new OptionsAtributesListener(findClassesContainingName(Robot.class), AutomatorOptions.ROBOTS));
		sensorsComboBox.addActionListener(new OptionsAtributesListener(findClassesContainingName(Sensor.class), AutomatorOptions.SENSORS));
		actuatorsComboBox.addActionListener(new OptionsAtributesListener(findClassesContainingName(Actuator.class), AutomatorOptions.ACTUATORS));
		controllersClassNameComboBox.addActionListener(new OptionsAtributesListener(findClassesContainingName(Controller.class), AutomatorOptions.CONTROLLERS));
		networkComboBox.addActionListener(new OptionsAtributesListener(findClassesContainingName(NeuralNetwork.class), AutomatorOptions.NETWORK));
		populationClassNameComboBox.addActionListener(new OptionsAtributesListener(findClassesContainingName(Population.class), AutomatorOptions.POPULATION));
		environmentClassNameComboBox.addActionListener(new OptionsAtributesListener(findClassesContainingName(Environment.class), AutomatorOptions.ENVIRONMENT));
		executorClassNameComboBox.addActionListener(new OptionsAtributesListener(findClassesContainingName(TaskExecutor.class), AutomatorOptions.EXECUTOR));
		evolutionClassNameComboBox.addActionListener(new OptionsAtributesListener(findClassesContainingName(Evolution.class), AutomatorOptions.EVOLUTION));
		evaluationClassNameComboBox.addActionListener(new OptionsAtributesListener(findClassesContainingName(EvaluationFunction.class), AutomatorOptions.EVALUATION));
		
		optionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getOptionsFromPanel();
			}
		});
		
		sensorsActuatorsList.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent event) { }
			@Override
			public void mousePressed(MouseEvent event) { }
			@Override
			public void mouseExited(MouseEvent event) { }
			@Override
			public void mouseEntered(MouseEvent event) { }
			@Override
			public void mouseClicked(MouseEvent event) {
				editOptionPanelAttribute();
				seeSensors();
			}
		});
		
		jListRemoveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeElementFromSensorsActuatorsList();
			}
		});
		
		saveArgumentsFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				createArgumentFile();
				
			}
		});
		
	}

	private void updateConfigurationText() {
		configResult.setText(result.toString());
	}
	
	private JPanel createResultContentButtons() {
		JPanel panel = new JPanel();
		saveArgumentsFileButton = new JButton("Save to File");
		panel.add(saveArgumentsFileButton);
		return panel;
	}
	
	private void amplifyPreview() {
		if(renderer != null){
			JFrame previewFrame = new JFrame("Preview");
			
			previewFrame.getContentPane().add(renderer);
			
			previewFrame.setSize(800, 800);
			previewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			previewFrame.setVisible(true);
		}
	}
	
	private void seeSensors() {
		if(robotConfig.getCompleteArguments().getNumberOfArguments() > 0 && result.getArgument("--environment").getNumberOfArguments() > 0){
			try {
				String extraRendererArguments = "conesensorid=";
				
				String selectedID = sensorsActuatorsList.getSelectedValue();
				if(selectedID != null && selectedID.contains("Sensor")){
					String id = robotConfig.getSensor(selectedID).getArgumentAsString("id");
					extraRendererArguments += id;
				}else{
					extraRendererArguments += 0;
				}
				
				showPreview(extraRendererArguments);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
//	private void showPreview() throws Exception{
//		Arguments rendererArgs = new Arguments("classname=TwoDRendererDebug",true);
//		renderer = Renderer.getRenderer(rendererArgs);
//		
//		String[] args = Arguments.readOptionsFromString(
//						"--robots " + result.getArgument("--robots") + "\n" +
//						"--environment " + result.getArgument("--environment"));
//		show(args);
//	}
	
	private void showPreview(String rendererExtraArgs) throws Exception{
		String arguments = "classname=TwoDRendererDebug," +rendererExtraArgs;
		Arguments rendererArgs = new Arguments(arguments,true);
		renderer = Renderer.getRenderer(rendererArgs);
		
		String[] args = Arguments.readOptionsFromString(
				"--robots " + result.getArgument("--robots") + "\n" +
				"--environment " + result.getArgument("--environment"));
		show(args);
	}

	private void show(String[] args) throws IOException, ClassNotFoundException {
		for(String s : args)
			System.out.println(s);
		try {
			rendererPanel.removeAll();
			
			HashMap<String, Arguments> arguments = Arguments.parseArgs(args);
			
			Simulator simulator = new Simulator(new Random(), arguments);
			
			ArrayList<Robot> robots = Robot.getRobots(simulator, arguments.get("--robots"));
			simulator.addRobots(robots);
			
			simulator.setupEnvironment();
			
			if (renderer != null) {
				renderer.addMouseListener(new MouseListener() {
					public void mouseReleased(MouseEvent e) {}
					public void mousePressed(MouseEvent e) {}
					public void mouseExited(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {}
					public void mouseClicked(MouseEvent e) {
						amplifyPreview();
					}
				});
				
				renderer.enableInputMethods(true);
				renderer.setSimulator(simulator);
				renderer.drawFrame();
				
				rendererPanel.add(renderer);
				rendererPanel.revalidate();
				rendererPanel.repaint();
				
				frame.revalidate();
				frame.repaint();
			}
		} catch(Exception e) {
			//Gotta catch 'em all! Silence the exceptions that might occur because of problems in the
			//initialization of Environments
		}
	}
	
	private void createArgumentFile() {
		if(outputTextField.getText().trim().length() > 0){
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Save File");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
				PrintWriter printWriter = null;
				try {
					String filePath = fileChooser.getSelectedFile().getAbsolutePath() + "/" + outputTextField.getText() + ".conf";
					
					printWriter = new PrintWriter(new File(filePath));
			    	printWriter.println(result.toString());
			    	JOptionPane.showMessageDialog(frame, "File " + outputTextField.getText() + ".conf, created!");
			    } catch (FileNotFoundException e) {
			    	e.printStackTrace();
			    } finally {
			    	printWriter.close ();
			    }
			}
		}else{
			JOptionPane.showMessageDialog(frame, "No Setup Name defined.");
		}
	}
	
	private void removeElementFromSensorsActuatorsList() {
		
		String selectedID = sensorsActuatorsList.getSelectedValue();
		
		System.out.println("_REMOVE BUTTON_ "+selectedID);
		
		if(selectedID == null)
			return;
		
		if(selectedID.contains("Sensor")) {
			robotConfig.removeSensor(selectedID);
			result.setArgument("--robots", robotConfig.getCompleteArguments());
		} else if(selectedID.contains("Actuator")) {
			robotConfig.removeActuator(selectedID);
			result.setArgument("--robots", robotConfig.getCompleteArguments());
		}
		
		updateSensorsActuatorsList();
		
		updateConfigurationText();
	}
	
	private JComboBox<String> loadRobotsClassNamesToComboBox(Class<?> className) {
		JComboBox<String> comboBox = new JComboBox<String>();
		ArrayList<Class<?>> aux = findClassesContainingName(className);
		
		comboBox.addItem("");
		
		for (Class<?> cl : aux)
			comboBox.addItem(cl.getSimpleName());
		
		return comboBox;
	}

	public ArrayList<Class<?>> findClassesContainingName(Class<?> objectClass) {
    	ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
    	
        String classpath = System.getProperty("java.class.path");
        String[] paths = classpath.split(System.getProperty("path.separator"));

        for (String path : paths) {
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
            	findClasses(file, objectClass,classes);
            }
        }
        
        return classes;
    }

	private void findClasses(File file, Class<?> objectClass, ArrayList<Class<?>> cls) {
		try {
			for (File f : file.listFiles()) {
	    		if(f.isDirectory()){
	    			findClasses(f, objectClass,cls);
	    		}else{
	    			if(f.getName().endsWith(".class")){
	    				String path = "";
	    				
	    				if(System.getProperty("os.name").contains("Windows"))
	    					path = f.getAbsolutePath().replace("\\", "/").split("/bin/")[1].replaceAll(".class", "").replaceAll("/", ".");
	    				else
	    					path = f.getAbsolutePath().split("/bin/")[1].replaceAll(".class", "").replaceAll("/", ".");
	    				
		    			Class<?> cl = Class.forName(path);

		    			if(isIntanceOf(cl, objectClass)){
		    				cls.add(cl);
		    			}
		    			
	    			}
	    		}
	      	}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isIntanceOf(Class<?> cls, Class<?> objectClass) {
		Class<?> superClass = cls.getSuperclass();
		
		if(superClass != null){			
			if(superClass.equals(Object.class)){
				return false;
			}else if(superClass.equals(objectClass)){
				return true;
			}else{
				return isIntanceOf(superClass, objectClass);
			}
		}
		
		return false;
	}
	
	private void getOptionsFromPanel() {
		
		String arguments ="classname=" + currentClassName;
		for (AutomatorOptionsAttribute attribute : optionsAttributes) {
			if(!arguments.isEmpty())
				arguments+= createAttributeString(attribute, true);
		}
		
		writeAttributes(arguments);
		
		seeSensors();
		
		updateSensorsActuatorsList();
		
		updateConfigurationText();
		optionsAttributes.clear();
		
		cleanOptionsPanel();
	}
	
	private void updateSensorsActuatorsList() {
		sensorsActuatorsListModel.clear();
		for (String id : robotConfig.getSensorIds()) {
			sensorsActuatorsListModel.addElement(id);
		}
		for (String id : robotConfig.getActuatorsIds()) {
			sensorsActuatorsListModel.addElement(id);
		}
	}
	
	private void writeAttributes(String arguments) {
		switch (currentOptions) {
			case ROBOTS:
				robotConfig.setAttributes(arguments);
				result.setArgument("--robots",robotConfig.getCompleteArguments());
				break;
			case SENSORS:
				if(editedAttributeName.isEmpty())
					robotConfig.addSensor(currentClassName, arguments);
				else
					robotConfig.edit(editedAttributeName, arguments);
				result.setArgument("--robots",robotConfig.getCompleteArguments());
				break;
			case ACTUATORS:
				if(editedAttributeName.isEmpty())
					robotConfig.addActuator(currentClassName, arguments);
				else 
					robotConfig.edit(editedAttributeName, arguments);
				result.setArgument("--robots",robotConfig.getCompleteArguments());
				break;
			case CONTROLLERS:
				result.setArgument("--controllers", new Arguments(arguments, false));
				break;
			case NETWORK:
				Arguments controllerArgs = result.getArgument("--controllers");
				controllerArgs.setArgument("network", arguments + ",inputs=auto,outputs=auto");
				break;
			case POPULATION:
				result.setArgument("--population", new Arguments(arguments, false));
				break;
			case ENVIRONMENT:
				result.setArgument("--environment", new Arguments(arguments, false));
				break;
			case EXECUTOR:
				result.setArgument("--executor", new Arguments(arguments, false));
				break;
			case EVOLUTION:
				result.setArgument("--evolution", new Arguments(arguments, false));
				break;
			case EVALUATION:
				result.setArgument("--evaluation", new Arguments(arguments, false));
				break;
		default:
			System.err.println("The currentOption variable is not available for writeAttributes()!");
			break;
		}
	}

	private String createAttributeString(AutomatorOptionsAttribute attribute, boolean insertComma) {
		String result = "";
		if(attribute.getComponent() instanceof JTextField){
			JTextField textField = (JTextField)attribute.getComponent();
			if(attribute.getCheckBox().isSelected() && !textField.getText().isEmpty() && !attribute.getName().isEmpty()){
				if(insertComma)
					result = "," + attribute.getName() + "=" + textField.getText();
				else
					result = attribute.getName() + "=" + textField.getText();
			}
		}else if(attribute.getComponent() instanceof JComboBox<?>){
			JComboBox<?> comboBox = (JComboBox<?>) attribute.getComponent();
			String selection = (String)comboBox.getSelectedItem();
			if(attribute.getCheckBox().isSelected() && !selection.isEmpty() && !attribute.getName().isEmpty()){
				if(insertComma)
					result = "," + attribute.getName() + "=" + comboBox.getSelectedItem();
				else
					result = attribute.getName() + "=" + comboBox.getSelectedItem();
			}
		}else{
			System.err.println("Component received on the method createAttributeString not supported!");
		}
		
		return result;
	}

	private void fullFillOptionsPanel(ArrayList<Class<?>> classesList, String className, ArrayList<AutomatorOptionsAttribute> optAttributes) throws ClassNotFoundException {
		cleanOptionsPanel();
		
		Class<?> cls = null;
		for (Class<?> c : classesList) {
			if(c.getName().contains(className)){
				cls = Class.forName(c.getName());
				break;
			}
		}
		
		ArrayList<ArgumentsAnnotation> annotations = addAnnotationsToList(cls.getDeclaredFields());
		
		Class<?> upperClass = cls.getSuperclass();
		boolean stop = false;
		while(!stop){
			if(upperClass.getSuperclass().equals(Object.class)){
				stop = true;
			}
			annotations.addAll(addAnnotationsToList(upperClass.getDeclaredFields()));
			upperClass = upperClass.getSuperclass();
		}
		
		int spaceToFillGrid = 0;
		
		if(annotations.size() >= OPTIONS_GRID_LAYOUT_SIZE){
			optionsPanelLeft.setLayout(new GridLayout(annotations.size()+1,1));
			optionsPanelCenter.setLayout(new GridLayout(annotations.size()+1,1));
			optionsPanelRight.setLayout(new GridLayout(annotations.size()+1,1));
		}else{
			spaceToFillGrid = OPTIONS_GRID_LAYOUT_SIZE - (annotations.size()+1);
			
			optionsPanelLeft.setLayout(new GridLayout(15,1));
			optionsPanelCenter.setLayout(new GridLayout(15,1));
			optionsPanelRight.setLayout(new GridLayout(15,1));	
		}
		
		JLabel jl = new JLabel("classname");
		JTextField jtf = new JTextField(currentClassName);
		jtf.setEnabled(false);
		JCheckBox jcb = new JCheckBox();
		jcb.setEnabled(false);
		jcb.setVisible(false);
		optionsPanelLeft.add(jl);
		optionsPanelCenter.add(jtf);
		optionsPanelRight.add(jcb);
		
		for (ArgumentsAnnotation annotation : annotations) {
			JLabel label = new JLabel(annotation.name());
				
			if(!annotation.help().isEmpty())
				label.addMouseListener(new LabelMouseListener(annotation.help()));
			
			optionsPanelLeft.add(label);
				
			AutomatorOptionsAttribute attribute = new AutomatorOptionsAttribute();
			attribute.setName(annotation.name());
				
			JTextField textField = null;
			JComboBox<String> combo = null;
			String defaultValue = annotation.defaultValue();
			
			AutomatorOptionsAttribute attributeToEdit = null;
			
			if(optAttributes != null){
				for (AutomatorOptionsAttribute optAttribute : optAttributes) {
					if(optAttribute.getName().equals(annotation.name()))
						attributeToEdit = optAttribute;
				}
			}
			
			boolean isEdited = false;
			
			if(annotation.values().length == 0){
				if(attributeToEdit == null)
					textField = createOptionPanelTextField(attribute,defaultValue, false);
				else{
					textField = createOptionPanelTextField(attribute,attributeToEdit.getDefaultValue(), true);
					isEdited = true;
				}
			}else{
				if(attributeToEdit == null)
					combo = createOptionPanelComboBox(annotation, attribute);
				else{
					combo = editOptionPanelComboBox(annotation, attribute,attributeToEdit.getDefaultValue());
					isEdited = true;
				}
			}
				
			if(textField != null){
				optionsPanelRight.add(createCheckBox(textField, attribute, isEdited));
			}else if(combo != null){
				optionsPanelRight.add(createCheckBox(combo, attribute, isEdited));
			}
				
			attribute.setDefaultValue(defaultValue);
			optionsAttributes.add(attribute);
		}
		
		for (int i = 0; i < spaceToFillGrid; i++) {
			JLabel left = new JLabel();
			left.setVisible(false);
			optionsPanelLeft.add(left);
			JLabel center = new JLabel();
			left.setVisible(false);
			optionsPanelCenter.add(center);
			JLabel right = new JLabel();
			left.setVisible(false);
			optionsPanelRight.add(right);
		}
		
		optionsButton.setVisible(true);
		helpArea.setVisible(true);
		frame.invalidate();
		frame.validate();
	}

	private JTextField createOptionPanelTextField(AutomatorOptionsAttribute attribute, String defaultValue, boolean editMode) {
		JTextField textField;
		textField = new JTextField();
		textField.setEnabled(editMode);
		textField.setText(defaultValue);
		attribute.setComponent(textField);
		optionsPanelCenter.add(textField);
		return textField;
	}
	
	private JComboBox<String> createOptionPanelComboBox(ArgumentsAnnotation annotation, AutomatorOptionsAttribute attribute) {
		JComboBox<String> combo;
		combo = new JComboBox<String>();
		combo.setEnabled(false);
		attribute.setComponent(combo);
		for (String v : annotation.values()) 
			combo.addItem(v);
		
		optionsPanelCenter.add(combo);
		return combo;
	}
	
	private JComboBox<String> editOptionPanelComboBox(ArgumentsAnnotation annotation, AutomatorOptionsAttribute attribute, String value) {
		JComboBox<String> combo;
		combo = new JComboBox<String>();
		combo.setEnabled(true);
		attribute.setComponent(combo);
		ArrayList<String> rest = new ArrayList<String>();
		for (String v : annotation.values()){
			if(v.equals(value))
				combo.addItem(v);
			else
				rest.add(v);
		}
		for (String r : rest){ 
			combo.addItem(r);
		}
		optionsPanelCenter.add(combo);
		return combo;
	}

	private void cleanOptionsPanel() {
		optionsPanelLeft.removeAll();
		optionsPanelCenter.removeAll();
		optionsPanelRight.removeAll();
		optionsButton.setVisible(false);
		helpArea.setVisible(false);
		frame.repaint();
	}

	private ArrayList<ArgumentsAnnotation> addAnnotationsToList(Field[] fields){
		ArrayList<ArgumentsAnnotation> annotationsList = new ArrayList<ArgumentsAnnotation>();
		for (Field f : fields) {
			if(f.isAnnotationPresent(ArgumentsAnnotation.class))
				annotationsList.add(f.getAnnotation(ArgumentsAnnotation.class));
		}
		return annotationsList;
	}
	
	private Component createCheckBox(Component component, AutomatorOptionsAttribute attribute, boolean editedAttribute){
		JCheckBox checkBox = new JCheckBox();
		checkBox.setSelected(editedAttribute);
		checkBox.addActionListener(new CheckBoxListener(component));
		attribute.setCheckBox(checkBox);
		return checkBox;
	}
	
	private void editOptionPanelAttribute(){
		try {
			String attributeID = sensorsActuatorsList.getSelectedValue();
			if(attributeID != null){
				Arguments args = null;
				
				Class<?> c = null;
				if(attributeID.contains("Sensor")){
					c = Sensor.class;
					args = robotConfig.getSensor(attributeID);
				}else if(attributeID.contains("Actuator")){
					c = Actuator.class;
					args = robotConfig.getActuator(attributeID);
				}else{
					System.err.println("Error on the editOptionPanelAttribute(), attributeID don't contain the word Sensor or Actuator");
				}
				
				editedAttributeName = attributeID;
				
				//Preencher com as opções existentes
				ArrayList<AutomatorOptionsAttribute> optionsAttribute = getAttributesFromArgumentsString(args);
				
				fullFillOptionsPanel(findClassesContainingName(c), args.getArgumentAsString("classname"), optionsAttribute);
			}	
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<AutomatorOptionsAttribute> getAttributesFromArgumentsString(Arguments args) {
		ArrayList<AutomatorOptionsAttribute> optionsAttribute = new ArrayList<AutomatorOptionsAttribute>();
		
		for (String key : args.getArguments()) { 
			
			String val = args.getArgumentAsString(key);
			
			if(!key.equals("classname")) {
				AutomatorOptionsAttribute oA = new AutomatorOptionsAttribute();
				oA.setName(key);
				oA.setDefaultValue(val);
				optionsAttribute.add(oA);
			} else {
				currentClassName = val;
				if(val.contains("Sensor")){
					currentOptions = AutomatorOptions.SENSORS;
				}else if(val.contains("Actuator")){
					currentOptions = AutomatorOptions.ACTUATORS;
				}else{
					System.err.println("Error on the getAttributesFromArgumentsString(), attributeComplete[1] don't contain the word Sensor or Actuator");
				}
			}
			
		}
		return optionsAttribute;
	}
	
	class OptionsAtributesListener implements ActionListener{
		
		private JComboBox<String> comboBox;
		private ArrayList<Class<?>> classesList;
		private AutomatorOptions selected;

		public OptionsAtributesListener(ArrayList<Class<?>> classesList, AutomatorOptions selected) {
			this.classesList = classesList;
			this.selected = selected;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent event) {
			editedAttributeName="";
			try {
				currentOptions = selected;
				comboBox = (JComboBox<String>) event.getSource();
				if(!((String)comboBox.getSelectedItem()).isEmpty()){
					currentClassName = (String)comboBox.getSelectedItem();
					fullFillOptionsPanel(classesList, currentClassName, null);
				}else{
					switch (selected) {
					case NETWORK:
						result.getArgument("--controllers").removeArgument("network");;
						break;
					case POPULATION:
						result.setArgument("--population", new Arguments(""));
						break;
					case ENVIRONMENT:
						result.setArgument("--environment", new Arguments(""));
						break;
					case EXECUTOR:
						result.setArgument("--executor", new Arguments(""));
						break;
					case EVOLUTION:
						result.setArgument("--evolution", new Arguments(""));
						break;
					case EVALUATION:
						result.setArgument("--evaluation", new Arguments(""));
						break;
					default:
						break;
					}
					updateConfigurationText();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class TextFieldListener implements DocumentListener{

		private JTextField textfield;
		private AutomatorOptions option;
		
		public TextFieldListener(JTextField textfield, AutomatorOptions option) {
			this.textfield = textfield;
			this.option = option;
		}
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			updateTextfieldInformation();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateTextfieldInformation();
		}

		@Override
		public void changedUpdate(DocumentEvent e) { }
		
		public void updateTextfieldInformation(){
			switch (option) {
			case OUTPUT:
				result.setArgument("--output", new Arguments(textfield.getText(),false));
				break;
			case RANDOM_SEED:
				result.setArgument("--random-seed", new Arguments(textfield.getText(),false));
				break;
			default:
				System.err.println("option variable received on the listener TextFieldListener is not supported");
				break;
			}
			
			updateConfigurationText();
		}
		
	}
	
	class LabelMouseListener implements MouseListener {
		private String helpText;
		
		public LabelMouseListener(String helpText) {
			this.helpText = helpText;
		}
		
		@Override
		public void mouseReleased(MouseEvent e) { }
		@Override
		public void mousePressed(MouseEvent e) { }
		@Override
		public void mouseExited(MouseEvent e) { 
			helpArea.setText("");
		}
		@Override
		public void mouseEntered(MouseEvent e) { 
			helpArea.setText(helpText);
		}
		@Override
		public void mouseClicked(MouseEvent e) { }
	}
	
	class CheckBoxListener implements ActionListener{
		
		Component component;
		
		public CheckBoxListener(Component component) {
			this.component = component;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JCheckBox checkBox = (JCheckBox)e.getSource();
			if(checkBox.isSelected())
				component.setEnabled(true);
			else
				component.setEnabled(false);
		}
		
	}
	
	public static void main(String[] args) {
		new ConfigurationAutomatorGui();
	}
	
}	