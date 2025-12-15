package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class YGeneticOptimizer {
	YNetworkTopology topology = new YNetworkTopology();
	private Random rand = new Random();
	private final double MUTATION_RATE = 0.2;
	private final double CROSSOVER_RATE = 0.8;
	private final int TOURNAMENT_SIZE = 5;

	public YGeneticOptimizer(YNetworkTopology topology) {
		this.topology = topology;
	}

	public List<Route> evolve(List<Route> currentPopulation) {
		List<Route> newGeneration = new ArrayList<>();

		Route best = getBestRoute(currentPopulation);
		newGeneration.add(best);

		while (newGeneration.size() < currentPopulation.size()) {
			Route child;
			Route parent1 = selectParent(currentPopulation);
			Route parent2 = selectParent(currentPopulation);
			if (parent1.nodeIds.equals(parent2.nodeIds)) {
				child=parent1.copy();
			    mutate(child); 
			}
			else {
				if (Math.random() < CROSSOVER_RATE) {
					child = crossover(parent1, parent2);
				} else {
					child = parent1.copy();
				}
				if (Math.random() < MUTATION_RATE) {
					mutate(child);
				}
			}
			
			child.calculateCost(1.0, 1.0, 1.0, topology.nodes);
			
			boolean isUniqe = true;
			for(Route r : newGeneration) {
				if(r.nodeIds.equals(child.nodeIds)) {
					isUniqe = false;
					break;
				}
			}
			
			if(isUniqe) {
				newGeneration.add(child);

			}
			
			//System.out.println("baba"+parent1.nodeIds.toString());

			//System.out.println("anne"+parent2.nodeIds.toString());
			//System.out.println("çocuk : "+child.nodeIds.toString());
		}
		return newGeneration;

	}

	private void mutate(Route route) {
		int startId = route.nodeIds.get(0);
		int endId = route.nodeIds.get(route.nodeIds.size() - 1);

		Route newRoute = topology.generateRandomRoute(startId, endId);

		if (newRoute != null) {
			route.nodeIds = new ArrayList<>(newRoute.nodeIds);
		}

	}

	public Route selectParent(List<Route> population) {
		Route best = population.get(rand.nextInt(population.size()));
		Route candidate = null;
		for (int i = 0; i < TOURNAMENT_SIZE; i++) {
			candidate = population.get(rand.nextInt(population.size()));
			if (candidate.fitnessScore < best.fitnessScore) {
				best = candidate;
			}

		}
		return best;
	}

	public Route crossover(Route parent1, Route parent2) {
		Route child = new Route();
		List<Integer> commonNodes = new ArrayList<>();

		for (int i = 1; i < parent1.nodeIds.size() - 1; i++) {
			if (parent2.nodeIds.contains(parent1.nodeIds.get(i))) {
				commonNodes.add(parent1.nodeIds.get(i));
			}
		}
		if (commonNodes.isEmpty()) {
			
			return parent1.copy();
		}

		int pivot = commonNodes.get(rand.nextInt(commonNodes.size()));

		for (int i = 0; i < parent1.nodeIds.size() - 1; i++) {
			child.nodeIds.add(parent1.nodeIds.get(i));
			if (parent1.nodeIds.get(i) == pivot) {
				break;
			}
		}

		int indexOfPivot = parent2.nodeIds.indexOf(pivot);

		for (int i = indexOfPivot + 1; i < parent2.nodeIds.size(); i++) {
			if (!child.nodeIds.contains(parent2.nodeIds.get(i))) {
				child.nodeIds.add(parent2.nodeIds.get(i));
			} else {
				//System.out.println("Crossover başarısız oldu (Döngü tespit edildi), kopya dönülüyor.");
				return parent1.copy();
			}
		}

		return child;
	}

	public Route getBestRoute(List<Route> population) {
		Route best = population.get(0);
		for (Route r : population) {
			if (r.fitnessScore < best.fitnessScore) {
				best = r;
			}
		}
		return best;
	}

}
