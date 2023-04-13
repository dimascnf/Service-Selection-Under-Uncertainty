package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import basic.Allocation;
import enums.ImprovementCondition;
import enums.ImprovementHeuristic;
import enums.ImprovementMode;
import enums.PerturbationMode;
import enums.ProbabilityScenario;
import enums.SearchMode;
import instance.InstanceMatrix;
import metaheuristic.ILS;

public class ArticleResult {
	
	public static void main(String[] args) throws FileNotFoundException {
		
		int executionsPerInstance = 3;
		int optialExecTimeDivisor  = 10;
		int ITERATIONS = 10000;
		int numberOfInstances = 94; //total: 94
		
		InstanceMatrix[] instanceArray = new InstanceMatrix[numberOfInstances];
		
		initiateInstanceArray(instanceArray);
		
		int instanceID = 1;
		
		HashMap<Integer,AlgResult> algResults = new HashMap<Integer,AlgResult>();
		
		for (int i = 1; i <= instanceArray.length; i++) {
			algResults.put(i, new AlgResult());
		}
		
		//ILS
		
		for (InstanceMatrix instance: instanceArray) {
			
			algResults.get(instanceID).setInstanceName(instance.getInstanceName());
			
			System.out.println("Executing instance "+instanceID);
			double bestCost = Double.MAX_VALUE;
			
			double timeToBest = -1;
			
			double execTimePerRepetition = instance.getOptimalExecTime()/optialExecTimeDivisor;
			
			if (instance.getOptimalExecTime() < 2) {
				ITERATIONS = 2000;
				optialExecTimeDivisor = 20;
			} else {
				ITERATIONS = 10000;
				optialExecTimeDivisor = 10;
			}
				
			double algInitTime = System.currentTimeMillis();
			
			for (int r = 0; r<executionsPerInstance; r++) {
				
				System.out.println("r "+r);
				
				double instanceInitTime = System.currentTimeMillis();
				Allocation all = null;
				do {
						ILS ils = new ILS();
						all = ils.ILS(instance, ITERATIONS, 0.4, 
								SearchMode.LOCAL_SEARCH, 
								ImprovementHeuristic.costImprovement,
								ProbabilityScenario.Ps,
								ImprovementCondition.FirstImprovement,
								algInitTime,
								instanceInitTime,
								execTimePerRepetition, PerturbationMode.MOVE, ImprovementMode.SWAP);
						
						if (all.getCurrentCost() < bestCost) {
							bestCost = all.getCurrentCost();
							timeToBest = all.getTimeToBest();
						}	
						if (all.getCurrentCost() == instance.getOptimalCost()) {
							timeToBest = all.getTimeToBest();
							break;
						}
				} while (((System.currentTimeMillis() - instanceInitTime)/1000 <= execTimePerRepetition)); // 60 seconds //|| (bestCost/instance.getOptimalCost() < 0.8)
				
				double repetitionExecTime = System.currentTimeMillis() - instanceInitTime;
				
				timeToBest = (bestCost == instance.getOptimalCost()) ? timeToBest : repetitionExecTime;
				
				//Allocation via GreedyMinProbAllocation
				/*ILS ils = new ILS();
				long before = System.currentTimeMillis();
				Allocation all = ils.GreedyMinProbAllocation(instance, ProbabilityScenario.Ps);
				long after = System.currentTimeMillis();
				long greedyExecTime = after - before;
				bestCost = all.getCurrentCost();
				timeToBest = greedyExecTime;*/
				
				if (algResults.get(instanceID).getBestResult() == 0) {
					algResults.get(instanceID).setBestResult(bestCost);
					algResults.get(instanceID).setMeanResult(bestCost/executionsPerInstance);
					algResults.get(instanceID).setTimeToBest(timeToBest/executionsPerInstance);
				} else { 
					algResults.get(instanceID).setMeanResult(bestCost/executionsPerInstance+algResults.get(instanceID).getMeanResult());
					algResults.get(instanceID).setTimeToBest(timeToBest/executionsPerInstance+algResults.get(instanceID).getTimeToBest());
					if (bestCost < algResults.get(instanceID).getBestResult())
						algResults.get(instanceID).setBestResult(bestCost);
				}
				
				if (bestCost == instance.getOptimalCost()) 
					algResults.get(instanceID).setCountBests(algResults.get(instanceID).getCountBests()+1);
				
			}
			instanceID++;
		}
		
		System.out.println("Instance | Optimal Cost | Optimal Time | Mean Best Cost | Best Cost |  Mean time to Best ");
		double instances = algResults.keySet().size();
		double meanBestCost = 0;
		for (Iterator<Integer> iterator = algResults.keySet().iterator(); iterator.hasNext();) {
			Integer instance = (Integer) iterator.next();
			System.out.print(algResults.get(instance).getInstanceName()+" ");
			System.out.print(instanceArray[instance-1].getOptimalCost()+" ");
			System.out.print(instanceArray[instance-1].getOptimalExecTime()+" ");
			AlgResult result = algResults.get(instance);
			System.out.print(result.getMeanResult()+" ");
			System.out.print(result.getBestResult()+" ");
			meanBestCost += result.getBestResult()/instances;
			System.out.println(result.getTimeToBest()/1000+" ");
		}
		System.out.println("MEAN BEST COSTS: "+meanBestCost);
	}
	
	private static void initiateInstanceArray(InstanceMatrix[] instanceArray) throws FileNotFoundException {
		
		File folder = new File("data\\Instances\\");
		File[] listOfFiles = folder.listFiles();
		
		int cont = 0;
		
		for (File inputFile: listOfFiles) {
			
			Scanner sc = null;
			try {
				sc = new Scanner(inputFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
			String lastLine = "";
		    while (sc.hasNextLine()) 
		    	lastLine = sc.nextLine();
		    
		    instanceArray[cont] = readInstance(Integer.parseInt((inputFile.getName().split("_")[3])));
		    
		    File logFile = new File("data\\Log\\"+inputFile.getName());
		    try {
				sc = new Scanner(logFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
		    while (sc.hasNextLine()) {
		    	lastLine = sc.nextLine();
		    	if (lastLine.startsWith("Total")) {
		    		instanceArray[cont].setOptimalExecTime(Double.parseDouble(lastLine.split("=")[1].trim().split("sec")[0]));
		    	}
		    }
		    instanceArray[cont++].setOptimalCost(Integer.parseInt(lastLine.split("= ")[2]));
		    
		}
		
	}

	private static InstanceMatrix readInstance(int instanceCont) throws FileNotFoundException {
				File file = new File("data\\Instances\\Instance_10_10_"+instanceCont);
				
				Scanner sc = new Scanner(file); 
				boolean initLine = true;
				
				InstanceMatrix instance = new InstanceMatrix();
				
				while (sc.hasNextLine()) { 
					
					String line = sc.nextLine();
					
					String[] splitLine = line.split(" ");
					
					if (initLine) {
						
						int numberOftasks = Integer.parseInt(splitLine[2]);
						int numberOfServices = Integer.parseInt(splitLine[1]);
						int vMax = Integer.parseInt(splitLine[3]);
						double pMax = Double.parseDouble(splitLine[4]);
						int Smax = Integer.parseInt(splitLine[5]);
						int Vres = Integer.parseInt(splitLine[6]);
						
						instance = new InstanceMatrix(numberOftasks, numberOfServices, vMax, Smax, pMax, Vres);
						
						initLine = false;
						
					} else {
						
						if (splitLine[0].equals("r")) {
							instance.setResourceConsumption(Integer.parseInt(splitLine[1])-1, Integer.parseInt(splitLine[2]));
						} else if (splitLine[0].equals("p")) {
							instance.setSlaViolationProbability(Integer.parseInt(splitLine[1])-1, Double.parseDouble(splitLine[2]));
						} else if (splitLine[0].equals("c")) {
							instance.setTaskCost(Integer.parseInt(splitLine[2])-1, Integer.parseInt(splitLine[1])-1, Integer.parseInt(splitLine[3]));
						}
					}
					
				}
				
				for (int i=0; i<instance.getNumberOfServices();i++) {
					instance.setServResourceCapacity(i, instance.getVres());
				}
				
				instance.setInstanceName(file.getName());
				
				return instance;
	}
	
}
