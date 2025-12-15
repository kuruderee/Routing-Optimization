package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class YQLearningAgent {
	private YNetworkTopology topology;
	private double[][] qTable;
	private int nodeCount;
	private Random random = new Random();

	private double alpha = 0.1;
	private double gamma = 0.9;
	private double epsilon = 0.5;

	public YQLearningAgent(YNetworkTopology topology) {
		this.topology = topology;
		this.nodeCount = topology.nodes.size();
		this.qTable = new double[nodeCount][nodeCount];
	}

	public void train(int startNodeId, int endNodeId, int episodes) {
		System.out.println("Q-Learning Eğitimi Başlıyor... (Ceza Sistemi Aktif)");

		for (int i = 0; i < episodes; i++) {
			int currentState = startNodeId;

			if (i > episodes * 0.5)
				epsilon = 0.1;

			int steps = 0;
			while (currentState != endNodeId && steps < 200) { // Adım limiti

				int nextState = chooseAction(currentState);

				if (nextState == -1)
					break;

				double cost = calculateTransitionCost(currentState, nextState);
				double reward = -cost;

				if (nextState == endNodeId) {
					reward = 10000.0;
				}

				double maxQNext = getMaxQValue(nextState);
				double currentQ = qTable[currentState][nextState];

				double newQ = currentQ + alpha * (reward + (gamma * maxQNext) - currentQ);
				qTable[currentState][nextState] = newQ;

				currentState = nextState;
				steps++;
			}
		}
		System.out.println("Eğitim Tamamlandı!");
	}

	public Route getOptimalPath(int startNodeId, int endNodeId) {
		Route route = new Route();
		route.nodeIds.add(startNodeId);

		int currentState = startNodeId;
		int steps = 0;

		Set<Integer> visited = new HashSet<>();
		visited.add(startNodeId);

		while (currentState != endNodeId && steps < nodeCount) {
			int bestNextNode = -1;
			double maxQ = -Double.MAX_VALUE;

			Node currentNode = topology.nodes.get(currentState);

			for (Link link : currentNode.outgoingLinks) {
				int neighborId = link.destination.id;

				if (!visited.contains(neighborId)) {
					if (qTable[currentState][neighborId] > maxQ) {
						maxQ = qTable[currentState][neighborId];
						bestNextNode = neighborId;
					}
				}
			}

			if (bestNextNode != -1) {
				route.nodeIds.add(bestNextNode);
				visited.add(bestNextNode); // Döngü engelleme
				currentState = bestNextNode;
			} else {
				break;
			}
			steps++;
		}

		route.calculateCost(1.0, 1.0, 1.0, topology.nodes);
		return route;
	}

	private int chooseAction(int currentState) {
		Node node = topology.nodes.get(currentState);
		if (node.outgoingLinks.isEmpty())
			return -1;

		if (random.nextDouble() < epsilon) {
			int randomIndex = random.nextInt(node.outgoingLinks.size());
			return node.outgoingLinks.get(randomIndex).destination.id;
		}

		int bestNode = -1;
		double maxVal = -Double.MAX_VALUE;

		for (Link l : node.outgoingLinks) {
			int neighbor = l.destination.id;
			if (qTable[currentState][neighbor] > maxVal) {
				maxVal = qTable[currentState][neighbor];
				bestNode = neighbor;
			}
		}

		if (bestNode == -1 || maxVal == 0.0) {
			int randomIndex = random.nextInt(node.outgoingLinks.size());
			return node.outgoingLinks.get(randomIndex).destination.id;
		}

		return bestNode;
	}

	private double getMaxQValue(int state) {
		Node node = topology.nodes.get(state);
		double maxQ = -Double.MAX_VALUE;
		boolean found = false;

		for (Link l : node.outgoingLinks) {
			int neighbor = l.destination.id;
			if (qTable[state][neighbor] > maxQ) {
				maxQ = qTable[state][neighbor];
				found = true;
			}
		}
		return found ? maxQ : 0.0;
	}

	private double calculateTransitionCost(int fromId, int toId) {
		Node fromNode = topology.nodes.get(fromId);
		Link link = fromNode.getLinkTo(toId);
		if (link == null)
			return 9999.0;

		double cost = link.linkDelay + (1000.0 / link.bandwidth) + (-Math.log(link.reliability));

		Node toNode = topology.nodes.get(toId);
		cost += toNode.processingDelay + (-Math.log(toNode.reliability));

		return cost;
	}
}