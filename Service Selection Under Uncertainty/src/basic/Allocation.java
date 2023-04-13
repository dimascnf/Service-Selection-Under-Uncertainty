package basic;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import instance.InstanceMatrix;

public class Allocation {
	
	private HashMap<Task, Service> allocation;
	
	//<Service,Consumption>
	private HashMap<Service, Integer> resourcePerService;
	
	//<Service,NumberOfAllocatedTasks> 
	private HashMap<Service, Integer> employedServices;
	
	private double currentCost;
	private double currentProb;
	private double timeToBest;
	
	public Allocation() {
		allocation = new HashMap<Task,Service>();
		resourcePerService = new HashMap<Service, Integer>();
		employedServices = new HashMap<Service,Integer>();
		currentCost = 0;
	}
	
	public int numberOfTasksAlloacted() {
		return allocation.size();
	}
	
	public void addTask(Task t, Service s, InstanceMatrix instance) {
		
		currentCost += instance.getTaskCost(t.getTaskId(), s.getServID());
		
		if (!allocation.containsKey(t))
			allocation.put(t, s);
		
		if (!employedServices.containsKey(s))
			employedServices.put(s,1);
		else employedServices.put(s,employedServices.get(s)+1); 
		
		if (!resourcePerService.containsKey(s))
			resourcePerService.put(s, t.getResourceConsumption());
		else resourcePerService.put(s, resourcePerService.get(s)+t.getResourceConsumption());
		
	}
	
	public void replaceService(Task t, Service newS, InstanceMatrix instance, String caller) {
		
		Service oldS = allocation.get(t);
		
		currentCost -= instance.getTaskCost(t.getTaskId(), oldS.getServID());
		
		if (employedServices.get(oldS) == 1)
			employedServices.remove(oldS);
		else employedServices.put(oldS,employedServices.get(oldS)-1); 
		
		resourcePerService.put(oldS, resourcePerService.get(oldS)-t.getResourceConsumption());
		
		allocation.put(t, newS);
		
		currentCost += instance.getTaskCost(t.getTaskId(), newS.getServID());
		
		if (!employedServices.containsKey(newS))
			employedServices.put(newS,1);
		else employedServices.put(newS,employedServices.get(newS)+1); 
		
		if (!resourcePerService.containsKey(newS))
			resourcePerService.put(newS, t.getResourceConsumption());
		else resourcePerService.put(newS, resourcePerService.get(newS)+t.getResourceConsumption());
		
	}

	public HashMap<Task, Service> getAllocation() {
		return allocation;
	}


	public void setAllocation(HashMap<Task, Service> allocation) {
		this.allocation = allocation;
	}


	public double getCurrentCost() {
		return currentCost;
	}
	

	public double getTimeToBest() {
		return timeToBest;
	}

	public void setTimeToBest(double timeToBest) {
		this.timeToBest = timeToBest;
	}

	public void setCurrentCost(double currentCost) {
		this.currentCost = currentCost;
	}


	public double getCurrentProb() {
		return currentProb;
	}


	public void setCurrentProb(double currentProb) {
		this.currentProb = currentProb;
	}
	
	public int getNumberOfEmployedServices() {
		return employedServices.keySet().size();
	}
	
	public static int diffValues(Object[] numArray){

	    ArrayList<Object> diffNum = new ArrayList<>();

	    for(int i=0; i<numArray.length; i++){
	        if(!diffNum.contains(numArray[i])){
	            diffNum.add(numArray[i]);
	        }
	    }

	   return diffNum.size();
	}
	
	public boolean respectsResourceRestriction(InstanceMatrix instance) {
		for (Iterator<Service> iterator = resourcePerService.keySet().iterator(); iterator.hasNext();) {
			Service s = iterator.next();
			if (resourcePerService.get(s) >
					instance.getVres())
				return false;
		}
		return true;
	}

	public void removeTask(Task t, InstanceMatrix instance) {
		
		Service s = allocation.get(t);
		
		allocation.remove(t);
		
		currentCost -= instance.getTaskCost(t.getTaskId(), s.getServID());
		
		if (employedServices.get(s) == 1)
			employedServices.remove(s);
		else employedServices.put(s,employedServices.get(s)-1); 
		
		resourcePerService.put(s, resourcePerService.get(s)-t.getResourceConsumption());
		
	}
	
}


