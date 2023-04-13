package search;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import basic.Allocation;
import basic.Service;
import basic.Task;
import enums.ImprovementCondition;
import enums.ImprovementMode;
import enums.ProbabilityScenario;
import instance.InstanceMatrix;
import util.RandomUtil;
import validator.SolutionValidator;

public class GenericSearcher {
	
	SolutionValidator validator = new SolutionValidator();
	
	public GenericSearcher() {
		
	}
	
	public boolean costImprovement(Allocation all, InstanceMatrix matrix, 
			int Vmax, int Smax, double Pmax, 
			ProbabilityScenario pScenario, ImprovementCondition condition, ImprovementMode mode){
		
		boolean globallyImproved = false;
		
		if (mode == ImprovementMode.MOVE) {
			
			boolean locallyImproved;
			
			do {
				
				locallyImproved = false;
				Task bestTaskToMove= null;
				Service bestServiceToMove= null;
				int bestCostGain = -1;
				
				for (Iterator<Task> iterator = all.getAllocation().keySet().iterator(); iterator.hasNext();) {
					Task task =  iterator.next();
					
					int init = RandomUtil.getRandomNumber(matrix.getNumberOfServices());
					
					for (int i=init; i<matrix.getNumberOfServices(); i++) {
						
						Service currentService = all.getAllocation().get(task);
						
						Service newService = new Service(i);
						
						if (!newService.equals(currentService) && 
								(matrix.getTaskCost(task.getTaskId(),newService.getServID()) < matrix.getTaskCost(task.getTaskId(),currentService.getServID()))) {
							
							//replace task allocation for the new service
							all.replaceService(task, newService, matrix, "costImprovement 1");
							
							boolean probRestrictionRequired = matrix.getTaskProb(newService.getServID()) > matrix.getTaskProb(currentService.getServID());
							
							if (validator.isFeasible(all, matrix, Vmax, Smax, Pmax, pScenario, probRestrictionRequired)) {
								if (condition == ImprovementCondition.FirstImprovement) {
									locallyImproved = true;
									globallyImproved = true;
									break;
								} else { //Best Improvement
									
									//replace task allocation for the former service
									all.replaceService(task, currentService, matrix, "costImprovement 2");
									
									//update the best move so far
									int costGain = matrix.getTaskCost(task.getTaskId(),currentService.getServID()) - matrix.getTaskCost(task.getTaskId(),newService.getServID());
									if (costGain > bestCostGain) {
										bestCostGain = costGain;
										bestTaskToMove = task;
										bestServiceToMove = newService;
									}
								}
							} else {
								//replace task allocation for the former service
								all.replaceService(task, currentService, matrix, "costImprovement 2");
								//improved = false;
							}
							
						}
						
					}
					if (condition == ImprovementCondition.BestImprovement || (condition == ImprovementCondition.FirstImprovement && locallyImproved == false)) 
					for (int i=0; i<init; i++) {
						
						Service currentService = all.getAllocation().get(task);
						
						Service newService = new Service(i);
						
						if (!newService.equals(currentService) && 
								(matrix.getTaskCost(task.getTaskId(),newService.getServID()) < matrix.getTaskCost(task.getTaskId(),currentService.getServID()))) {
							
							//replace task allocation for the new service
							all.replaceService(task, newService, matrix, "costImprovement 1");
							//System.out.println("replace move 2");
							
							boolean probRestrictionRequired = matrix.getTaskProb(newService.getServID()) > matrix.getTaskProb(currentService.getServID());
							
							if (validator.isFeasible(all, matrix, Vmax, Smax, Pmax, pScenario, probRestrictionRequired)) {
								if (condition == ImprovementCondition.FirstImprovement) {
									locallyImproved = true;
									globallyImproved = true;
									break;
								} else { //Best Improvement
									
									//replace task allocation for the former service
									all.replaceService(task, currentService, matrix, "costImprovement 2");
									
									//update the best move so far
									int costGain = matrix.getTaskCost(task.getTaskId(),currentService.getServID()) - matrix.getTaskCost(task.getTaskId(),newService.getServID());
									if (costGain > bestCostGain) {
										bestCostGain = costGain;
										bestTaskToMove = task;
										bestServiceToMove = newService;
									}
								}
							} else {
								//replace task allocation for the former service
								all.replaceService(task, currentService, matrix, "costImprovement 2");
								//improved = false;
							}
							
						}
						
					}
				
				}
				
				if (bestCostGain != -1 && condition == ImprovementCondition.BestImprovement) {
					locallyImproved = true;
					globallyImproved = true;
					all.replaceService(bestTaskToMove, bestServiceToMove, matrix, "costImprovement 2");
				}
				
			} while (locallyImproved && condition == ImprovementCondition.BestImprovement);
			
			return globallyImproved;
		
		} else { //SWAP

			boolean locallyImproved;
			
			do {
				
				locallyImproved = false;
				Task T1, T2;
				Service S1, S2;
				T1 = T2 = null;
				S1 = S2 = null;
				int bestCostGain = -1;
				
				for (Iterator<Task> iterator = all.getAllocation().keySet().iterator(); iterator.hasNext();) {
					
					Task currentTask =  iterator.next();
					Service currentTaskService = all.getAllocation().get(currentTask);
					
					int init = RandomUtil.getRandomNumber(matrix.getNumberOfTasks());
					
					for (int i=init; i<matrix.getNumberOfTasks(); i++) {
						
						if (i != currentTask.getTaskId()) {
						
							int randomTaskId = i;
							Task randomTask = new Task(randomTaskId,matrix.getTaskConsumption(randomTaskId));
							Service randomTaskService = all.getAllocation().get(randomTask);
							
							if (randomTaskService.equals(currentTaskService))
								continue;
							
							int costBeforeSwap = matrix.getTaskCost(currentTask.getTaskId(),currentTaskService.getServID()) + matrix.getTaskCost(randomTask.getTaskId(),randomTaskService.getServID());
							int costAfterSwap = matrix.getTaskCost(currentTask.getTaskId(),randomTaskService.getServID()) + matrix.getTaskCost(randomTask.getTaskId(),currentTaskService.getServID());
							if (costAfterSwap < costBeforeSwap) {
								
								//swap the services of the tasks
								all.replaceService(currentTask, randomTaskService, matrix, "costImprovement 1");
								all.replaceService(randomTask, currentTaskService, matrix, "costImprovement 1");
								
								boolean probRestrictionRequired = false;
								
								if (validator.isFeasible(all, matrix, Vmax, Smax, Pmax, pScenario, probRestrictionRequired)) {
									
									if (condition == ImprovementCondition.FirstImprovement) {
										locallyImproved = true;
										globallyImproved = true;
										break;
									} else { //bestImprovement
										//undo the swap
										all.replaceService(currentTask, currentTaskService, matrix, "costImprovement 1");
										all.replaceService(randomTask, randomTaskService, matrix, "costImprovement 1");
										
										int currentCostGain = costBeforeSwap - costAfterSwap;
										if (currentCostGain > bestCostGain) {
											T1 = currentTask;
											T2 = randomTask;
											S1 = currentTaskService;
											S2 = randomTaskService;
											bestCostGain = currentCostGain;
										}
									}
								} else {
									//undo the swap
									all.replaceService(currentTask, currentTaskService, matrix, "costImprovement 1");
									all.replaceService(randomTask, randomTaskService, matrix, "costImprovement 1");
								}
								
							}
						
						}
						
					}
					if (condition == ImprovementCondition.BestImprovement || (condition == ImprovementCondition.FirstImprovement && locallyImproved == false))
					for (int i=0; i<init; i++) {
						
						if (i != currentTask.getTaskId()) {
							
							int randomTaskId = i;
							Task randomTask = new Task(randomTaskId,matrix.getTaskConsumption(randomTaskId));
							Service randomTaskService = all.getAllocation().get(randomTask);
							
							if (randomTaskService.equals(currentTaskService))
								continue;
							
							int costBeforeSwap = matrix.getTaskCost(currentTask.getTaskId(),currentTaskService.getServID()) + matrix.getTaskCost(randomTask.getTaskId(),randomTaskService.getServID());
							int costAfterSwap = matrix.getTaskCost(currentTask.getTaskId(),randomTaskService.getServID()) + matrix.getTaskCost(randomTask.getTaskId(),currentTaskService.getServID());
							if (costAfterSwap < costBeforeSwap) {
								
								//swap the services of the tasks
								all.replaceService(currentTask, randomTaskService, matrix, "costImprovement 1");
								all.replaceService(randomTask, currentTaskService, matrix, "costImprovement 1");
								
								boolean probRestrictionRequired = false;
								
								if (validator.isFeasible(all, matrix, Vmax, Smax, Pmax, pScenario, probRestrictionRequired)) {
									
									if (condition == ImprovementCondition.FirstImprovement) {
										locallyImproved = true;
										globallyImproved = true;
										break;
									} else { //bestImprovement
										//replace task allocation for the former service
										all.replaceService(currentTask, currentTaskService, matrix, "costImprovement 1");
										all.replaceService(randomTask, randomTaskService, matrix, "costImprovement 1");
										
										int currentCostGain = costBeforeSwap - costAfterSwap;
										if (currentCostGain > bestCostGain) {
											T1 = currentTask;
											T2 = randomTask;
											S1 = currentTaskService;
											S2 = randomTaskService;
											bestCostGain = currentCostGain;
										}
									}
								} else {
									//undo the swap
									all.replaceService(currentTask, currentTaskService, matrix, "costImprovement 1");
									all.replaceService(randomTask, randomTaskService, matrix, "costImprovement 1");
									//improved = false;
								}
							
							}
							
						}
					}
				}
				
				if (bestCostGain != -1 && condition == ImprovementCondition.BestImprovement) {
					locallyImproved = true;
					globallyImproved = true;
					all.replaceService(T1, S2, matrix, "costImprovement 1");
					all.replaceService(T2, S1, matrix, "costImprovement 1");
				}
				
			} while (locallyImproved && condition == ImprovementCondition.BestImprovement);
			
			return globallyImproved;
		}
		
	}	
	public Allocation VND(Allocation all, InstanceMatrix matrix, int Vmax, int Smax, double Pmax, ProbabilityScenario pScenario, ImprovementCondition condition, ImprovementMode mode){
		
		int k = 1;
		boolean improved = false;
		
		while (k <= 2) {
			if (k == 1) {
				improved = costImprovement(all, matrix, Vmax, Smax, Pmax, pScenario, ImprovementCondition.FirstImprovement, ImprovementMode.MOVE);
				if (improved) {
					k = 1;
				} else k++;
			}
		
			if (k == 2) {
				improved = costImprovement(all, matrix, Vmax, Smax, Pmax, pScenario, ImprovementCondition.BestImprovement, ImprovementMode.MOVE);
				if (improved) {
					k = 1;
				} else k++;
			}
		}

		return all;
		
	}
}
