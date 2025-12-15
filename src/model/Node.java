package model;
import java.util.*;

public class Node {
	int id;
	double processingDelay;
	double reliability;
	List<Link> outgoingLinks;
	
	public Node (int id) {
		this.id = id;
		this.outgoingLinks = new ArrayList<>();
		generateRandomProperties();
		
	}
	private void generateRandomProperties() {
		Random rand = new Random();
		this.processingDelay = 0.5+(2.0-0.5)* rand.nextDouble();
		this.reliability = 0.95 + (0.999 - 0.95) * rand.nextDouble();
		
	}
	public Link getLinkTo(int targetNodeId) {
	    for (Link l : this.outgoingLinks) {
	        if (l.destination.id == targetNodeId) {
	            return l;
	        }
	    }
	    return null; // Bağlantı yoksa
	}
}
