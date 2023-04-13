package validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import basic.Allocation;
import basic.Task;
import enums.ProbabilityScenario;
import instance.InstanceMatrix;
import util.MathOperations;

public class SolutionValidator {
	
	public static void main(String[] args) {
		int[] subSetOfTaskIndexes = {1,2,4};
		int[] remainingTasks;
    	int[] allTasks = IntStream.range(0, 10-1).toArray();
    	remainingTasks = removeEqual(allTasks, subSetOfTaskIndexes);
	}
	
	public boolean isFeasible(Allocation all, InstanceMatrix instance, 
			int Vmax, int Smax, double Pmax, 
			ProbabilityScenario pScenario, boolean verfyProbRestriction) {
		
		boolean probRestriction = false;
		
		if (all.getNumberOfEmployedServices() > Smax) {
			return false;
		}
		
		if (!all.respectsResourceRestriction(instance)){
			return false;
		}
		
		//Probability Restriction
		if (pScenario == ProbabilityScenario.P) {
			
			double probSum = 0;
			
			int numberOfTasks = all.getAllocation().keySet().size();
			
			for (int k =0; k<=Vmax; k++) {
				double p = instance.getProbabilityPerService()[0];
				double probabilityOfKViolations = MathOperations.binomial(numberOfTasks, k) * Math.pow(p, k) * Math.pow((1 - p), numberOfTasks - k);
				probSum += probabilityOfKViolations;
			}
			
			probRestriction = (1 - probSum) <= Pmax; 
			
		} else if (pScenario == ProbabilityScenario.Ps) {
			
			if (verfyProbRestriction) {
			
				int vMax = instance.getvMax();
				int n = instance.getNumberOfTasks();
				double[] prob = instance.getProbabilityPerService();
	
				double[][] a = new double[n+1][vMax+1];
				
				for(int i=0; i<=n; ++i) {
					double sum =0.0;
					double probi_1;
					
					if (i>0 && all.getAllocation().containsKey(new Task(i-1,instance.getTaskConsumption(i-1)))) {
						probi_1 = instance.getProbabilityPerService()[all.getAllocation().get(new Task(i-1,instance.getTaskConsumption(i-1))).getServID()];
					} else probi_1 = 0;
						
					for(int k=0; k<=Vmax; ++k) {
						if(i==0 && k==0) 
							a[i][k] = 1.0;
						else if (k > i) 
							a[i][k] = 0.0;
						else if (k==0)
							a[i][k] = (1.0 - probi_1)* a[i-1][k];
						else a[i][k] = probi_1*a[i-1][k-1] + (1.0 - probi_1)* a[i-1][k];
						
						sum+= a[i][k];
						
					}	
					if(1.0 - sum > Pmax) {
						return false;
					}
				}
	
				double pNotViolated = 0.0;
	
				// Sum of the last line
				for(int k=0;k<=Vmax;k++)
					pNotViolated += a[n][k];
				
				if((1.0 - pNotViolated) <= Pmax) {
					all.setCurrentProb((1.0 - pNotViolated));
					return true;
				}	else return false;
			
			} else return true;
			
		} else if (pScenario == ProbabilityScenario.Pts) {
			//TODO Future work
		}
		
		return true;
	}

	public static int[] removeEqual(int[] a_arr, int[] b_arr) {
	    List<Integer> list = new ArrayList<>();
	    for (int a : a_arr) { list.add(a); }

	    Iterator<Integer> iter = list.iterator();
	    while(iter.hasNext()) {
	        int a = iter.next();
	        for (int b : b_arr) {
	            if (a == b) {iter.remove();}
	        }
	    }

	    int[] result = new int[list.size()];
	    for (int i = 0; i < list.size(); i++) {result[i] = list.get(i);}

	    return result;
	}
}
