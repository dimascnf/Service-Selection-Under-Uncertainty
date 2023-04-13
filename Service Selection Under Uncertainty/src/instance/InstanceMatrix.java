package instance;
import java.util.LinkedList;
import java.util.List;

import basic.Service;

public class InstanceMatrix {
	
	private int[][] costMatrix;
	
	private String instanceName;
	
	private int vMax;
	private int Smax;
	private double pMax;
	
	private int optimalCost;
	private double optimalExecTime;
	
	// scenario p_ts
	//private int[][] probMatrix;
	
	//scenario p_s
	private double[] probabilityPerService;
	
	
	private int[] servResourceCapacity;
	private int[] taskResourceConsumption;
	
	private int numberOfServices;
	private int numberOfTasks;
	
	private int Vres;
	
	public InstanceMatrix() {
		
	}

	
	public InstanceMatrix(int numberOftasks, int numberOfServices, int vMax, int Smax, double pMax, int Vres) {
		costMatrix = new int[numberOftasks][numberOfServices];
		probabilityPerService = new double[numberOfServices];
		servResourceCapacity = new int[numberOfServices];
		taskResourceConsumption = new int[numberOftasks];
		this.numberOfServices = numberOfServices;
		this.numberOfTasks = numberOftasks;
		this.vMax = vMax;
		this.Smax = Smax;
		this.pMax = pMax;
		this.Vres = Vres;
	}
	
	public int getVres() {
		return Vres;
	}
	

	public String getInstanceName() {
		return instanceName;
	}
	

	public double getOptimalExecTime() {
		return optimalExecTime;
	}


	public void setOptimalExecTime(double optimalExecTime) {
		this.optimalExecTime = optimalExecTime;
	}


	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}


	public void setVres(int vres) {
		Vres = vres;
	}


	public int getvMax() {
		return vMax;
	}



	public void setvMax(int vMax) {
		this.vMax = vMax;
	}



	public int getSmax() {
		return Smax;
	}


	public int getOptimalCost() {
		return optimalCost;
	}


	public void setOptimalCost(int optimalCost) {
		this.optimalCost = optimalCost;
	}


	public void setSmax(int smax) {
		Smax = smax;
	}

	public double getServiceProb(int servId) {
		return probabilityPerService[servId];
	}

	public double getpMax() {
		return pMax;
	}



	public void setpMax(double pMax) {
		this.pMax = pMax;
	}



	public void setNumberOfServices(int numberOfServices) {
		this.numberOfServices = numberOfServices;
	}



	public void setNumberOfTasks(int numberOfTasks) {
		this.numberOfTasks = numberOfTasks;
	}

	public void setResourceConsumption(int taskIdx, int consumption) {
		taskResourceConsumption[taskIdx] = consumption;
	}
	
	public void setSlaViolationProbability(int servIdx, double prob) {
		probabilityPerService[servIdx] = prob;
	}

	//tasks are placed amongst the lines
	//services are plcaed amongst the collumns
	public Double getMinTaskCost(int taskId) {
		
		double minCost = Double.MAX_VALUE;
		
		for (int i=0; i<costMatrix[taskId].length; i++) {
			if (costMatrix[taskId][i] < minCost)
				minCost = costMatrix[taskId][i];
		}
		
		return minCost;
	}
	
	//tasks are placed amongst the lines
	//services are plcaed amongst the collumns
	public Double getMaxTaskCost(int taskId) {
		
		double maxCost = Double.MIN_VALUE;
		
		for (int i=0; i<costMatrix[taskId].length; i++) {
			if (costMatrix[taskId][i] > maxCost)
				maxCost = costMatrix[taskId][i];
		}
		
		
		return maxCost;
	}

	public List<Service> getServicesWithMaxCost(int taskId, double maxCost) {
		
		List<Service> services = new LinkedList<Service>();
		
		for (int i=0; i<costMatrix[taskId].length; i++) {
			if (costMatrix[taskId][i] <= maxCost)
				services.add(new Service(i,servResourceCapacity[i]));
		}
		
		return services;
	}
	
	public Service getServiceWithLowestCost(int taskId) {
		
		int lowesCost = Integer.MAX_VALUE;
		Service serviceWithLowestCost = null;
		
		for (int i=0; i<costMatrix[taskId].length; i++) {
			if (costMatrix[taskId][i] <= lowesCost)
				serviceWithLowestCost = new Service(i,servResourceCapacity[i]);
		}
		
		return serviceWithLowestCost;
	}
	
	public Service getServiceWithLowestProb(int taskId) {
		
		int lowestProb = Integer.MAX_VALUE;
		Service serviceWithLowestProb = null;
		
		for (int i=0; i<probabilityPerService.length; i++) {
			if (probabilityPerService[i] <= lowestProb)
				serviceWithLowestProb = new Service(i,servResourceCapacity[i]);
		}
		
		return serviceWithLowestProb;
	}

	public double[] getProbabilityPerService() {
		return probabilityPerService;
	}

	public void setProbMatrix(double[] probabilityPerService) {
		this.probabilityPerService = probabilityPerService;
	}

	public int[] getServResourceCapacity() {
		return servResourceCapacity;
	}

	public void setServiceFullResourceCapacity(int[] servResourceCapacity) {
		this.servResourceCapacity = servResourceCapacity;
	}
	
	public void setServResourceCapacity(int servIdx, int servResourceCapacity) {
		this.servResourceCapacity[servIdx] = servResourceCapacity;
	}

	public int[] getTaskConsumption() {
		return taskResourceConsumption;
	}
	
	public int getTaskConsumption(int tId) {
		return taskResourceConsumption[tId];
	}

	public void setTaskConsumption(int[] taskConsumption) {
		this.taskResourceConsumption = taskConsumption;
	}
	
	public void setTaskCost(int taskIdx, int servIdx, int taskCost) {
		costMatrix[taskIdx][servIdx] = taskCost;
	}

	public int getNumberOfServices() {
		return this.numberOfServices;
	}

	public int getNumberOfTasks() {
		return this.numberOfTasks;
	}

	public int getTaskCost(int taskId, int servID) {
		return costMatrix[taskId][servID]; 
	}

	public double getTaskProb(int servID) {
		return probabilityPerService[servID];
	}
	
}
