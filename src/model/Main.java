package model;

import model.Route;
import model.YGeneticOptimizer;
import model.YNetworkTopology;
import model.YDijkstraOptimizer;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        
        
        System.out.println("Sistem başlatılıyor...");
        
        YNetworkTopology topology = new YNetworkTopology();

        YGeneticOptimizer optimizer = new YGeneticOptimizer(topology);

        int startNodeID = 0;  
        int endNodeID = 1;    
        int populationSize = 50; 
        int maxGenerations = 150; 

        
        // 3. İLK NÜFUSUN YARATILIŞI (BIG BANG)
        List<Route> currentPopulation = new ArrayList<>();
        System.out.println("İlk rastgele nüfus oluşturuluyor...");

        while (currentPopulation.size() < populationSize) {
            Route randomRoute = topology.generateRandomRoute(startNodeID, endNodeID);
            
            if (randomRoute != null && !randomRoute.nodeIds.isEmpty()) {
            	randomRoute.calculateCost(1.0, 1.0, 1.0, topology.nodes);
                currentPopulation.add(randomRoute);
                
            }
            
        }
        
        System.out.println("İlk nüfus hazır! Evrim başlıyor...");
        System.out.println("------------------------------------------------");

        System.out.println(">>> BAŞLANGIÇ POPÜLASYONU DETAYI <<<");
        int sayac = 1;
        for (Route r : currentPopulation) {
            System.out.println("Birey " + sayac + ": " + r.nodeIds.toString() + " | Skor (Maliyet): " + r.fitnessScore);
            sayac++;
        }
        System.out.println(">>> LİSTE SONU <<<");
        System.out.println("------------------------------------------------");
        // ---------------------------------------------------------
        // 4. EVRİM DÖNGÜSÜ (THE LOOP)
        // ---------------------------------------------------------
        for (int i = 0; i < maxGenerations; i++) {
            
            currentPopulation = optimizer.evolve(currentPopulation);
            
            Route bestOfGen = optimizer.getBestRoute(currentPopulation);
            
            System.out.println("Jenerasyon " + (i + 1) + 
                               " | En iyi Skor : " + bestOfGen.fitnessScore);
            System.out.println(bestOfGen.nodeIds.toString());
            
        }

        System.out.println("------------------------------------------------");
        System.out.println("Evrim Tamamlandı!");
        for (Route r : currentPopulation) {
            System.out.println("Birey " + sayac + ": " + r.nodeIds.toString() + " | Skor (Maliyet): " + r.fitnessScore);
            sayac++;
        }
        Route finalBest = optimizer.getBestRoute(currentPopulation);
        
        System.out.println("EN İYİ ROTA BULUNDU:");
        System.out.println("Nihai Skor : " + finalBest.fitnessScore);
        System.out.println("Rota: " + finalBest.nodeIds.toString());
        System.out.println("------------------------------------------------");
        System.out.println(">>> DOĞRULAMA (VALIDATION) <<<");
        System.out.println("Dijkstra (Kesin Çözüm) hesaplanıyor...");

        YDijkstraOptimizer solver = new YDijkstraOptimizer();
        
        Route exactRoute = solver.findOptimalRoute(topology, startNodeID, endNodeID);

        if (exactRoute != null) {
            System.out.println("\nSONUÇLAR:");
            System.out.println("Genetik Algoritma Yolu : " + finalBest.nodeIds);
            System.out.println("Genetik Algoritma Skoru: " + finalBest.fitnessScore);
            System.out.println("---------------------------");
            System.out.println("Dijkstra Yolu          : " + exactRoute.nodeIds);
            System.out.println("Dijkstra Skoru         : " + exactRoute.fitnessScore);
            
            double fark = finalBest.fitnessScore - exactRoute.fitnessScore;
            
            if (fark < 0.0001) { // Double karşılaştırma hassasiyeti
                System.out.println("\n>>> TEBRİKLER! Algoritman MÜKEMMEL sonucu buldu! <<<");
            } else {
                System.out.println("\n>>> FARK VAR: Algoritman " + String.format("%.2f", fark) + " puan geride.");
                System.out.println("Bu, GA'nın yerel bir minimuma (tuzağa) düştüğünü gösterir.");
            }
        } else {
            System.out.println("HATA: Dijkstra bile yol bulamadı! Haritada kopukluk var.");
        }
        System.out.println("------------------------------------------------");
        System.out.println(">>> Q-LEARNING ANALİZİ BAŞLIYOR <<<");
        
        YQLearningAgent qAgent = new YQLearningAgent(topology);
        
        qAgent.train(startNodeID, endNodeID, 50000);
        
        Route qRoute = qAgent.getOptimalPath(startNodeID, endNodeID);
        
        System.out.println("Q-Learning Yolu  : " + qRoute.nodeIds);
        System.out.println("Q-Learning Skoru : " + qRoute.fitnessScore);
        
        if (Math.abs(qRoute.fitnessScore - exactRoute.fitnessScore) < 0.1) {
             System.out.println("SONUÇ: Q-Learning de Mükemmel Yolu Buldu! 🤖");
        } else {
             System.out.println("SONUÇ: Q-Learning biraz farkla kaçırdı.");
        }
    }
}