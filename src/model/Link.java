package model;
import java.util.Random;

class Link {
    Node source;
    Node destination;
    double bandwidth;   // 100 Mbps - 1000 Mbps [cite: 33]
    double linkDelay;   // 3 ms - 15 ms [cite: 34]
    double reliability; // 0.95 - 0.999 [cite: 35]

    public Link(Node source, Node destination) {
        this.source = source;
        this.destination = destination;
        generateRandomProperties();
    }

    private void generateRandomProperties() {
        Random rand = new Random();
        this.bandwidth = 100 + (1000 - 100) * rand.nextDouble();
        this.linkDelay = 3 + (15 - 3) * rand.nextDouble();
        this.reliability = 0.95 + (0.999 - 0.95) * rand.nextDouble();
    }
}
