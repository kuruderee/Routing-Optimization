package model;

import java.util.*;

public class YDijkstraOptimizer {

    private class NodeState implements Comparable<NodeState> {
        int nodeId;
        double totalCost;

        public NodeState(int nodeId, double totalCost) {
            this.nodeId = nodeId;
            this.totalCost = totalCost;
        }

        @Override
        public int compareTo(NodeState other) {
            return Double.compare(this.totalCost, other.totalCost);
        }
    }

    public Route findOptimalRoute(YNetworkTopology topology, int startId, int endId) {
        int size = topology.nodes.size();
        
        
        double[] minCosts = new double[size];
        int[] cameFrom = new int[size];
        boolean[] visited = new boolean[size];

        
        Arrays.fill(minCosts, Double.MAX_VALUE);
        Arrays.fill(cameFrom, -1);
        minCosts[startId] = 0;

        
        PriorityQueue<NodeState> queue = new PriorityQueue<>();
        queue.add(new NodeState(startId, 0));

        
        while (!queue.isEmpty()) {
            
            NodeState current = queue.poll();
            int uId = current.nodeId;

            
            if (visited[uId]) continue;
            visited[uId] = true;
            
            if (uId == endId) break; 

            Node uNode = topology.nodes.get(uId);

            
            for (Link link : uNode.outgoingLinks) {
                int vId = link.destination.id;
                Node vNode = link.destination;

                if (!visited[vId]) {
                    double linkCost = link.linkDelay + (1000.0 / link.bandwidth) + (-Math.log(link.reliability));
                    double nodeCost = vNode.processingDelay + (-Math.log(vNode.reliability));
                    
                    double newCost = minCosts[uId] + linkCost + nodeCost;

                    if (newCost < minCosts[vId]) {
                        minCosts[vId] = newCost;
                        cameFrom[vId] = uId; 
                        queue.add(new NodeState(vId, newCost));
                    }
                }
            }
        }
        if (minCosts[endId] == Double.MAX_VALUE) {
            return null; 
        }

        Route optimalRoute = new Route();
        List<Integer> path = new ArrayList<>();
        int curr = endId;
        
        while (curr != -1) {
            path.add(0, curr); // Listeye başa ekle (çünkü sondan geliyoruz)
            curr = cameFrom[curr];
        }
        
        
        for(Integer id : path) {
            optimalRoute.nodeIds.add(id);
        }
        
        optimalRoute.calculateCost(1.0, 1.0, 1.0, topology.nodes);
        
        return optimalRoute;
    }
}