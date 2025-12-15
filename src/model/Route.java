package model;

import java.util.ArrayList;
import java.util.List;

public class Route {
	public ArrayList<Integer> nodeIds;
	public double fitnessScore;

	public Route() {
		this.nodeIds = new ArrayList<>();
	}

	public double calculateCost(double wDelay, double wRel, double wRes, List<Node> topologyNodes) {
	    double totalDelay = 0;
	    double reliabilityCost = 0;
	    double resourceCost = 0;

	    // Güvenlik kontrolü
	    if (nodeIds.isEmpty() || nodeIds.size() < 2) {
	        return Double.MAX_VALUE;
	    }

	    // -------------------------------------------------------
	    // DÖNGÜ 1: SADECE Bağlantıları (Linkleri) Hesapla
	    // -------------------------------------------------------
	    for (int i = 0; i < nodeIds.size() - 1; i++) {
	        int currentId = nodeIds.get(i);
	        int nextId = nodeIds.get(i + 1);

	        Node currentNode = topologyNodes.get(currentId);
	        Link connection = currentNode.getLinkTo(nextId);

	        if (connection == null) {
	            return Double.MAX_VALUE;
	        }

	        // Sadece linkin değerlerini topla
	        totalDelay += connection.linkDelay;
	        reliabilityCost += (-Math.log(connection.reliability));
	        resourceCost += (1000.0 / connection.bandwidth);
	    } // İlk döngü burada BİTTİ.

	    // -------------------------------------------------------
	    // DÖNGÜ 2: SADECE Düğümleri (Node'ları) Hesapla
	    // -------------------------------------------------------
	    for (int id : nodeIds) {
	        Node node = topologyNodes.get(id);

	        // Sadece node'un değerlerini topla (Artık tekrar etmeyecek)
	        totalDelay += node.processingDelay;
	        reliabilityCost += (-Math.log(node.reliability));
	    } // İkinci döngü burada BİTTİ.

	    // -------------------------------------------------------
	    // SONUÇ: Hepsini tek seferde hesapla
	    // -------------------------------------------------------
	    this.fitnessScore = (wDelay * totalDelay) + (wRel * reliabilityCost) + (wRes * resourceCost);

	    return fitnessScore;
	}

	public Route copy() {
		Route newRoute = new Route();
		// Sayıların (ID) kopyasını al
		newRoute.nodeIds = new ArrayList<>(this.nodeIds);
		newRoute.fitnessScore = this.fitnessScore;
		return newRoute;
	}
}
