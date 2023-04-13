package basic;

public class Task {
	
	private int taskId;
	private Integer resourceConsumption;
	
	public Task(int id, int resourceComsumption) {
		taskId = id;
		this.resourceConsumption = resourceComsumption;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	public Integer getResourceConsumption() {
		return resourceConsumption;
	}

	public void setResourceConsumption(int resourceConsumption) {
		this.resourceConsumption = resourceConsumption;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + taskId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (taskId != other.taskId)
			return false;
		return true;
	}
	

}
