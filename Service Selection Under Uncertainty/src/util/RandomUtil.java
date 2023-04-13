package util;
import java.util.List;
import java.util.Random;

import basic.Service;
import basic.Task;

public class RandomUtil {
	
	public static Service getRandomService(int numberOfServices) {
		
		//int seed = ?;
		
		Random rn = new Random();
		//int range = maximum - minimum + 1;
		int range = numberOfServices - 0 + 1;
		//int randomNum =  rn.nextInt(range) + minimum;
		int randomNum =  rn.nextInt(range) + 0;
		return new Service(randomNum==0?0:randomNum-1);
	}
	
	public static int getRandomNumber(int number) {
		
		//int seed = ?;
		
		Random rn = new Random();
		//int range = maximum - minimum + 1;
		int range = number - 0 + 1;
		//int randomNum =  rn.nextInt(range) + minimum;
		int randomNum =  rn.nextInt(range) + 0;
		return (randomNum==0?0:randomNum-1);
	}
	
	public static int getRandomTaskId(int numberOfTasks) {
		
		//int seed = ?;
		
		Random rn = new Random();
		//int range = maximum - minimum + 1;
		int range = numberOfTasks - 0 + 1;
		//int randomNum =  rn.nextInt(range) + minimum;
		int randomNum =  rn.nextInt(range) + 0;
		return (randomNum==0?0:randomNum-1);
	}
	
	public static Service getRandomService(List<Service> restrictedCandidateServiceSet) {
		
		//int seed = ?
		
		Random rn = new Random();
		//int range = maximum - minimum + 1;
		int range = restrictedCandidateServiceSet.size() - 0 + 1;
		//int randomNum =  rn.nextInt(range) + minimum;
		int randomNum =  rn.nextInt(range) + 0;
		return restrictedCandidateServiceSet.get(randomNum>0?randomNum-1:0);
	}

	public static int getRandomTaskIDFromList(List<Integer> tasksToAllocate) {
		
		//int seed = ?
		
		Random rn = new Random();
		//int range = maximum - minimum + 1;
		int range = tasksToAllocate.size() - 0 + 1;
		//int randomNum =  rn.nextInt(range) + minimum;
		int randomNum =  rn.nextInt(range) + 0;
		return tasksToAllocate.get(randomNum>0?(randomNum-1):0);
		
	}

}
