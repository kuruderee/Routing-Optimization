package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class RouteTest {

	@Test
    void testMaliyetHesaplama() {
        Node n0 = new Node(0); 
        n0.processingDelay = 10.0;
        n0.reliability = 1.0;
        
        Node n1 = new Node(1); 
        n1.processingDelay = 20.0;
        n1.reliability = 1.0;

        Link link0to1 = new Link(n0, n1); 
        link0to1.linkDelay = 5.0;      
        link0to1.bandwidth = 1000.0;   
        link0to1.reliability = 1.0;    
        
        n0.outgoingLinks.add(link0to1);
        
        List<Node> topologyNodes = new ArrayList<>();
        topologyNodes.add(n0);
        topologyNodes.add(n1);

        Route route = new Route();
        route.nodeIds.add(0);
        route.nodeIds.add(1);

        double sonuc = route.calculateCost(1.0, 1.0, 1.0, topologyNodes);

        double beklenenDeger = 36.0;

        System.out.println("Hesaplanan: " + sonuc + " | Beklenen: " + beklenenDeger);

        assertEquals(beklenenDeger, sonuc, 0.001, "Maliyet hesabı yanlış çıktı!");
    }
}
