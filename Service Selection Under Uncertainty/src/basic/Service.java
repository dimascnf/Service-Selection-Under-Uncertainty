package basic;

public class Service {
	
	private int servID;
	private int resourcerCapacity;
	
	public Service(int id, int cap) {
		servID = id;
		resourcerCapacity = cap;
	}
	
	public Service(int id) {
		servID = id;
	}
	
	public int getServID() {
		return servID;
	}
	
	public String toString() {
		return ""+servID;
	}



	public void setServID(int servID) {
		this.servID = servID;
	}



	public int getResourcerCapacity() {
		return resourcerCapacity;
	}



	public void setResourcerCapacity(int resourcerCapacity) {
		this.resourcerCapacity = resourcerCapacity;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + servID;
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
		Service other = (Service) obj;
		if (servID != other.servID)
			return false;
		return true;
	}
	

}
