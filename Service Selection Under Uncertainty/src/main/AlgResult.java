package main;

public class AlgResult {
	
	private double bestResult;
	private double meanResult;
	private double timeToBest;
	private double countBests;
	private String instanceName;
	
	public AlgResult() {
		bestResult = 0;
		meanResult = 0;
		countBests = 0;
		timeToBest = 0;
	}
	
	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}

	public double getBestResult() {
		return bestResult;
	}
	public void setBestResult(double bestResult) {
		this.bestResult = bestResult;
	}
	public double getMeanResult() {
		return meanResult;
	}
	public void setMeanResult(double meanResult) {
		this.meanResult = meanResult;
	}
	public double getTimeToBest() {
		return timeToBest;
	}
	public void setTimeToBest(double timeToBest) {
		this.timeToBest = timeToBest;
	}
	public double getCountBests() {
		return countBests;
	}
	public void setCountBests(double countBests) {
		this.countBests = countBests;
	}
	
}
