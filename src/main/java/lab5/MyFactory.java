package lab5;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

import java.util.Random;

public class MyFactory extends AbstractCandidateFactory<double[]> {

    private final int dimension;

    public MyFactory(int dimension) {
        this.dimension = dimension;
    }

    public double[] generateRandomCandidate(Random random) {
        double[] solution = new double[dimension];
        // x from -5.0 to 5.0
        // your implementation:
        double bound = 5.0;
        for (int i = 0; i < solution.length; i++) {
            solution[i] = random.nextDouble(-bound, bound);
        }
        return solution;
    }
}

