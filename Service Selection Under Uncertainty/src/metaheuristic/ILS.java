package metaheuristic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import basic.Allocation;
import basic.Service;
import basic.Task;
import enums.ImprovementCondition;
import enums.ImprovementHeuristic;
import enums.ImprovementMode;
import enums.PerturbationMode;
import enums.ProbabilityScenario;
import enums.SearchMode;
import instance.InstanceMatrix;
import search.GenericSearcher;
import util.RandomUtil;
import validator.SolutionValidator;

public class ILS {
	
SolutionValidator validator = new SolutionValidator();
	
	public ILS() {
		
	}
	
public Allocation ILSWithRestart(InstanceMatrix instance, int IT, double alpha, SearchMode mode, ImprovementHeuristic h, ProbabilityScenario pScenario, ImprovementCondition condition, 
		double initTime, double instanceInitTime, double execTimePerInstance, PerturbationMode perturbationMode, ImprovementMode impMode) {
		
		Allocation bestAllocation = null;
		Double bestCost = null;
		
		//always returns a feasible solution
		Allocation initSolution = GreedyInitialSolution(instance,alpha,instance.getNumberOfTasks(),instance.getvMax(),instance.getSmax(),instance.getpMax(),pScenario);
		
		bestCost = initSolution.getCurrentCost();
		bestAllocation = initSolution;
		
		//always returns a feasible solution
		Allocation improvedSolution = neighborhoodSearch(initSolution, instance, mode, h, instance.getvMax(),instance.getSmax(),instance.getpMax(), pScenario, condition, impMode);
		
		if(improvedSolution.getCurrentCost() < bestCost) {
			bestCost = improvedSolution.getCurrentCost();
			bestAllocation = improvedSolution;
			bestAllocation.setTimeToBest(System.currentTimeMillis() - instanceInitTime);
		}
		
		int contNotImproved = 0;

		for (int i=0; i < IT; i++) {
			
			Allocation improvedSolution_1 = perturbation(bestAllocation, instance, instance.getvMax(), instance.getSmax(), instance.getpMax(), pScenario, i, IT, perturbationMode);
			
			Allocation improvedSolution_2 = neighborhoodSearch(improvedSolution_1, instance, mode, h, instance.getvMax(), instance.getSmax(), instance.getpMax(), pScenario, condition, impMode);

			//update best solution
			if (improvedSolution_2.getCurrentCost() < bestCost) {
				contNotImproved = 0;
				bestCost = improvedSolution_2.getCurrentCost();
				bestAllocation = improvedSolution_2;
				bestAllocation.setTimeToBest(System.currentTimeMillis() - instanceInitTime);
			} else contNotImproved++;
			
			if (contNotImproved > IT/10) {
				bestAllocation = GreedyInitialSolution(instance,alpha,instance.getNumberOfTasks(),instance.getvMax(),instance.getSmax(),instance.getpMax(),pScenario);
				bestAllocation.setTimeToBest(System.currentTimeMillis() - instanceInitTime);
				bestCost = bestAllocation.getCurrentCost();
			}
		}
		
		return bestAllocation;
	}
	
	private Allocation perturbation(Allocation bestAllocation, InstanceMatrix instance, int getvMax, int smax,
		double getpMax, ProbabilityScenario pScenario, int i, int iT, PerturbationMode perturbationMode) {
		if (perturbationMode == PerturbationMode.SWAP)
			return perturbationSwap(bestAllocation, instance, getvMax, smax, getpMax, pScenario, i, iT);
		else if (perturbationMode == PerturbationMode.GRANADE)
			return perturbationGranade(bestAllocation, instance, getvMax, smax, getpMax, pScenario, i, iT); 
		else return perturbationMove(bestAllocation, instance, getvMax, smax, getpMax, pScenario, i, iT);
}
	
	public Allocation GreedyMinProbAllocation(InstanceMatrix instance, ProbabilityScenario pScenario) {
	
		
		return ProbabilityBasedGreedyInitialSolution(instance, instance.getNumberOfServices(), instance.getvMax(), instance.getSmax(), instance.getpMax(), pScenario);
		
	}
	
	

	public Allocation ILS(InstanceMatrix instance, int IT, double alpha, SearchMode mode, ImprovementHeuristic h, ProbabilityScenario pScenario, ImprovementCondition condition, 
			double initTime, double instanceInitTime, double execTimePerInstance, PerturbationMode perturbationMode, ImprovementMode impMode) {
		
		Allocation bestAllocation = null;
		Double bestCost = null;
		
		//always returns a feasible solution
		Allocation initSolution = ProbGreedyInitialSolution(instance,alpha,instance.getNumberOfTasks(),instance.getvMax(),instance.getSmax(),instance.getpMax(),pScenario);
		
		Allocation localSearchSolution = neighborhoodSearch(initSolution, instance, mode, h, instance.getvMax(), instance.getSmax(), instance.getpMax(), pScenario, condition, impMode);
		
		bestCost = localSearchSolution.getCurrentCost();
		bestAllocation = localSearchSolution;
		bestAllocation.setTimeToBest(System.currentTimeMillis() - instanceInitTime);

		for (int i=0; i < IT; i++) {
			
			//if (i % 1000 == 0)
				//System.out.println(i);
			
			Allocation improvedSolution = perturbation(bestAllocation, instance, instance.getvMax(), instance.getSmax(), instance.getpMax(), pScenario, i, IT, perturbationMode);
			
			Allocation improvedSolutionAfterLocalSearch = neighborhoodSearch(improvedSolution, instance, mode, h, instance.getvMax(), instance.getSmax(), instance.getpMax(), pScenario, condition, impMode);
		
			if (improvedSolutionAfterLocalSearch.getCurrentCost() < bestCost) {
				bestCost = improvedSolutionAfterLocalSearch.getCurrentCost();
				bestAllocation = improvedSolutionAfterLocalSearch;
				bestAllocation.setTimeToBest(System.currentTimeMillis() - instanceInitTime);
			}
		}
		
		return bestAllocation;
	}
	
	private Allocation perturbationGranade(Allocation improvedSolution, InstanceMatrix matrix, int vmax, int smax, double pmax,
			ProbabilityScenario pScenario, int i, int IT) {
		
		int numberOfServices = improvedSolution.getAllocation().values().size();
		
		Service randomService = RandomUtil.getRandomService(numberOfServices);
		
		for (Iterator<Task> iterator = improvedSolution.getAllocation().keySet().iterator(); iterator.hasNext();) {
			Task task= (Task) iterator.next();
			if (improvedSolution.getAllocation().get(task).equals(randomService)) {
				Service oldService = improvedSolution.getAllocation().get(task);
				//passar o serviço da task e desconsiderar o serviço passado
				Service newService = matrix.getServiceWithLowestProb(task.getTaskId());
				//Service newService = matrix.getServiceWithLowestCost(task.getTaskId());
				if (!newService.equals(oldService)) {
					improvedSolution.replaceService(task, newService, matrix, "Swap");
				boolean probResRequired = matrix.getServiceProb(newService.getServID()) > matrix.getServiceProb(oldService.getServID());
				if (!validator.isFeasible(improvedSolution, matrix, vmax, smax, pmax, pScenario,probResRequired)) {
					improvedSolution.replaceService(task, oldService, matrix, "Swap");
					//j--;
				}
				}
			}
		}
		
		return improvedSolution;
	}
	
	private Allocation perturbationSwap(Allocation improvedSolution, InstanceMatrix matrix, int vmax, int smax, double pmax,
			ProbabilityScenario pScenario, int i, int IT) {
		
		int numberOfTasks = improvedSolution.getAllocation().keySet().size();
		
		//int l = 3;
		int l = (int) (6-(i/(IT*1.0))*5);
		
		for (int j=0; j<l; j++) {
			
			int randomTaskId1 = RandomUtil.getRandomTaskId(numberOfTasks);
			Task t1 = new Task(randomTaskId1,matrix.getTaskConsumption(randomTaskId1));
			Service oldT1Service = improvedSolution.getAllocation().get(t1);
			
			int randomTaskId2 = RandomUtil.getRandomTaskId(numberOfTasks);
			Task t2 = new Task(randomTaskId2,matrix.getTaskConsumption(randomTaskId2));
			Service oldT2Service = improvedSolution.getAllocation().get(t2);
			
			if (t1.equals(t2)) {
				j--;
				continue;
			}
			
			improvedSolution.replaceService(t1, oldT2Service, matrix, "Swap 1");
			improvedSolution.replaceService(t2, oldT1Service, matrix, "Swap 2");
			
			if (!validator.isFeasible(improvedSolution, matrix, vmax, smax, pmax, pScenario,false)) {
				improvedSolution.replaceService(t2, oldT2Service, matrix, "Swap 3");
				improvedSolution.replaceService(t1, oldT1Service, matrix, "Swap 4");
				j--;
			}
				
		}
		
		return improvedSolution;
		
	}

	private Allocation perturbationMove(Allocation improvedSolution, InstanceMatrix matrix, int vmax, int smax, double pmax,
			ProbabilityScenario pScenario, int i, int IT) {
		
		int numberOfTasks = improvedSolution.getAllocation().keySet().size();
		
		//System.out.println("BEFORE PERTURBATION: prob "+improvedSolution.getCurrentProb());
		//System.out.println("BEFORE PERTURBATION: pmax "+matrix.getpMax());
		
		//variar nos experimentos
		int l = (int) (6-(i/(IT*1.0))*5);
		//int l = (int) (1+(i/(IT*1.0))*5);
		//int l = 2;
		//int l = 3;
		
		for (int j=0; j<l; j++) {
			//System.out.println(j);
			int randomTaskId = RandomUtil.getRandomTaskId(numberOfTasks);
			Task t1 = new Task(randomTaskId,matrix.getTaskConsumption(randomTaskId));
			
			Service oldService = improvedSolution.getAllocation().get(t1);
			Service newService = RandomUtil.getRandomService(numberOfTasks);
			improvedSolution.replaceService(t1, RandomUtil.getRandomService(numberOfTasks), matrix, "perturbation 2");
			
			boolean probRestrictionRequired = matrix.getTaskProb(newService.getServID()) > matrix.getTaskProb(oldService.getServID());
			
			if (!validator.isFeasible(improvedSolution, matrix, vmax, smax, pmax, pScenario,probRestrictionRequired)) {
				improvedSolution.replaceService(t1, oldService, matrix, "perturbation 2");
				j--;
			}
		}
		
		//System.out.println("AFTER PERTURBATION: prob "+improvedSolution.getCurrentProb());
		//System.out.println("AFTER PERTURBATION: pmax "+matrix.getpMax());
		
		return improvedSolution;
	}

	private Allocation neighborhoodSearch(Allocation allocation, InstanceMatrix matrix, SearchMode mode, ImprovementHeuristic h, 
			int Vmax, int Smax, double Pmax, ProbabilityScenario pScenario, ImprovementCondition condition, ImprovementMode impMode) {
		if (mode == SearchMode.LOCAL_SEARCH) {
			if (h == ImprovementHeuristic.costImprovement)
				new GenericSearcher().costImprovement(allocation, matrix, Vmax, Smax, Pmax, pScenario, condition, impMode);
		}
		else if (mode == SearchMode.VND)
			return new GenericSearcher().VND(allocation, matrix, Vmax, Smax, Pmax, pScenario, condition, impMode);
		
		return allocation;
	}
	
	private Allocation ProbGreedyInitialSolution(InstanceMatrix instance, double alpha, int numberOfTasks,int Vmax, int Smax, double Pmax, ProbabilityScenario pScenario) {
		return ProbabilityBasedGreedyInitialSolution(instance, numberOfTasks, Vmax, Smax, Pmax, pScenario);
	}
	
	private Allocation GreedyInitialSolution(InstanceMatrix instance, double alpha, int numberOfTasks,int Vmax, int Smax, double Pmax, ProbabilityScenario pScenario) {
		
		Allocation all = new Allocation();
		
		List<Integer> tasksToAllocate = new LinkedList<Integer>();
		for (int i=0; i < numberOfTasks; i++)
			tasksToAllocate.add(i);
		
		int cont = 0;
		
		while (all.numberOfTasksAlloacted() < numberOfTasks) {
			
			if (cont > 3*numberOfTasks)
				return ProbabilityBasedGreedyInitialSolution(instance, numberOfTasks, Vmax, Smax, Pmax, pScenario);
		
			//System.out.println("all.numberOfTasksAlloacted() "+ all.numberOfTasksAlloacted());
		
			//System.out.println("Allocation.size() "+all.numberOfTasksAlloacted());
			//System.out.println("numberOfTasks "+numberOfTasks);
			int randomTaskId = RandomUtil.getRandomTaskIDFromList(tasksToAllocate);
			//System.out.println("Random Task ID: "+randomTaskId);
			//System.out.println("TasksToAllocate: "+tasksToAllocate);
			
			double minCost = instance.getMinTaskCost(randomTaskId);
			double maxCost = instance.getMaxTaskCost(randomTaskId);
			
			//double m = minCost + alpha*(maxCost - minCost);
			
			List<Service> restrictedCandidateServiceSet = instance.getServicesWithMaxCost(randomTaskId, minCost + alpha*(maxCost - minCost));
			
			Service randomService = RandomUtil.getRandomService(restrictedCandidateServiceSet);
			
			Task randomTask = new Task(randomTaskId,instance.getTaskConsumption(randomTaskId));
			tasksToAllocate.remove(new Integer(randomTaskId));
			
			all.addTask(randomTask, randomService, instance);
			
			/*try {
				Thread.sleep(900);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			//System.out.println("Task: "+randomTaskId);
			//System.out.println("Service: "+randomService);
			
			boolean isFeasible = validator.isFeasible(all,instance,Vmax,Smax,Pmax,pScenario, true);
			//System.out.println("isFeasible: "+isFeasible);
			if (!isFeasible) {
				all.removeTask(randomTask, instance);
				tasksToAllocate.add(new Integer(randomTaskId));
			}
			
			cont++;
				
		}
		
		return all;
	}
	
	private Allocation ProbabilityBasedGreedyInitialSolution(InstanceMatrix instance, int numberOfTasks,
			int vmax, int smax, double pmax, ProbabilityScenario pScenario) {
		
		Allocation all = new Allocation();
		
		//System.out.println("CRIOU ProbabilityBasedGreedyInitialSolution");
		
		while (all.numberOfTasksAlloacted() < numberOfTasks) {
			
			for (int iId=0; iId<numberOfTasks;iId++) {
				Task task = new Task(iId, instance.getTaskConsumption(iId));
				
				List<Integer> orderedServiceIndexes = new ArrayList<Integer>();
				
				for (int i =0; i<instance.getNumberOfServices();i++)
					orderedServiceIndexes.add(i);
					
				sortServicesByProbability(orderedServiceIndexes,instance);
				
				do {
					Service serviceWithMinimumProb = new Service(orderedServiceIndexes.get(0));
					all.addTask(task, serviceWithMinimumProb, instance);
					boolean isFeasible = validator.isFeasible(all,instance,instance.getvMax(),instance.getSmax(),instance.getpMax(),pScenario,true);
					//System.out.println("Task "+task.getTaskId());
					//System.out.println("Feasible "+isFeasible);
					//System.out.println("orderedServiceIndexes.size() "+orderedServiceIndexes.size());
					if (!isFeasible) {
						all.removeTask(task, instance);
						orderedServiceIndexes.remove(0);
					}
				} while (!all.getAllocation().containsKey(task));
				//Repeat until the task is allocated
			}
			
		}
		
		return all;
	}

	/*private Allocation ProbabilityBasedGreedyInitialSolution(InstanceMatrix instance, double alpha, int numberOfTasks,
			int vmax, int smax, double pmax, ProbabilityScenario pScenario) {
		
		Allocation all = new Allocation();
		
		//System.out.println("CHAMOU ProbabilityBasedGreedyInitialSolution");
		
		while (all.numberOfTasksAlloacted() < numberOfTasks) {
			
			for (int iId=0; iId<numberOfTasks;iId++) {
				Task task = new Task(iId, instance.getTaskConsumption(iId));
				
				List<Integer> orderedServiceIndexes = new ArrayList<Integer>();
				
				for (int i =0; i<instance.getNumberOfServices();i++)
					orderedServiceIndexes.add(i);
					
				orderedServiceIndexes = sortServicesByProbability(orderedServiceIndexes,instance);
				
				//System.out.println("Allocating Task "+task.getTaskId()+" to Service "+(orderedServiceIndexes.get(0))+" (Minimum Probability Available)");
				
				do {
					Service serviceWithMinimumProb = new Service(orderedServiceIndexes.get(0));
					all.addTask(task, serviceWithMinimumProb, instance);
					boolean isFeasible = validator.isFeasible(all,instance,instance.getvMax(),instance.getSmax(),instance.getpMax(),pScenario, true);
					if (!isFeasible) {
						all.removeTask(task, instance);
						orderedServiceIndexes.remove(0);
					}
				} while (!all.getAllocation().containsKey(task));
				//Repeat until the task is allocated
			}
			
		}
		
		return all;
	}*/

	private List<Integer> sortServicesByProbability(List<Integer> orderedServices, InstanceMatrix instance) {
		int n = orderedServices.size(); 
		for (int i = 0; i <n;i++) {
			for (int j=0; j <n-1; j++) {
				if (instance.getServiceProb(orderedServices.get(j)) > instance.getServiceProb(orderedServices.get(j+1))) {
					Integer tmpIdx = orderedServices.get(j+1);
					orderedServices.remove(j+1);
					orderedServices.add(j, tmpIdx);
				}
					
			}
		}
		return orderedServices;
		
	}

}
