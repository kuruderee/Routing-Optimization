package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

public class YNetworkTopology {
	List<Node> nodes;
	private Random random = new Random();
	private final String fileSourceNSF = "NSFNET_New.txt";
	private final String fileSourcedeneme = "deneme.txt";

	public YNetworkTopology() {
		nodes = new ArrayList<>();
		createNodes();
		createLinks();
	}

	public void createNodes() {
		for (int i = 0; i < 14; i++) {
			Node newNode = new Node(i);
			nodes.add(newNode);
		}
	}

	public void createLinks() {
		int[][] matrix = readMatrixFromFile(fileSourceNSF);
		if (matrix != null) {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix[i].length; j++) {
					if (matrix[i][j] == 1) {
						Node source = nodes.get(i);
						Node dest = nodes.get(j);

						Link nodeSD = new Link(source, dest);
						source.outgoingLinks.add(nodeSD);
					}

				}
			}
			System.out.println("NSFNET Topolojisi Başarıyla Kuruldu!");
		} else {
			System.out.println("Topoloji kurulamadı çünkü matris okunamadı.");
		}

	}

	public static int[][] readMatrixFromFile(String filePath) {
		List<int[]> rows = new ArrayList<>();

		try {
			File file = new File(filePath);
			Scanner scanner = new Scanner(file);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().trim();
				if (line.isEmpty())
					continue;

				String[] parts = line.split(":");

				int[] row = new int[parts.length];
				for (int i = 0; i < parts.length; i++) {
					row[i] = Integer.parseInt(parts[i]);
				}
				rows.add(row);
			}
			scanner.close();

		} catch (FileNotFoundException e) {
			System.out.println("HATA: Dosya bulunamadı! Yol: " + filePath);
			e.printStackTrace();
			return null;
		} catch (NumberFormatException e) {
			System.out.println("HATA: Dosyada sayı olmayan karakterler var!");
			e.printStackTrace();
			return null;
		}

		int size = rows.size();
		int[][] matrix = new int[size][size];
		for (int i = 0; i < size; i++) {
			matrix[i] = rows.get(i);
		}

		System.out.println("Matris Başarıyla Okundu! Boyut: " + size + "x" + size);
		return matrix;
	}

	public Route generateRandomRoute(int sourceId, int destId) {
		Route randomRoute = new Route();
		List<Integer> visited = new ArrayList<>();
		PriorityQueue<Node> queue = new PriorityQueue<>();

		int currentId = sourceId;
		randomRoute.nodeIds.add(currentId);
		visited.add(currentId);

		int stepLimit = nodes.size() * 3;

		while (currentId != destId && stepLimit > 0) {
			Node currentNode = nodes.get(currentId);
			List<Node> komsular = new ArrayList<>();

			if (currentNode.outgoingLinks != null) {
				for (Link l : currentNode.outgoingLinks) {
					if (!visited.contains(l.destination.id)) {
						komsular.add(l.destination);
					}
				}
			}

			if (komsular.isEmpty()) {
				return null;
			}

			java.util.Collections.shuffle(komsular);

			Node nextNode = komsular.get(0);

			currentId = nextNode.id;
			randomRoute.nodeIds.add(currentId);
			visited.add(currentId);

			stepLimit--;
		}

		if (currentId != destId)
			return null;

		return randomRoute;
	}

}
