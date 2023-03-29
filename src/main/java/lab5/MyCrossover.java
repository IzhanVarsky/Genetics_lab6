package lab5;

import org.uncommons.watchmaker.framework.operators.AbstractCrossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyCrossover extends AbstractCrossover<double[]> {
    protected MyCrossover() {
        super(1);
    }

    protected List<double[]> mate(double[] p1, double[] p2, int i, Random random) {
        ArrayList<double[]> children = new ArrayList<double[]>();

        // your implementation:
        assert i == 1;
        assert p1.length == p2.length;

        int sz = p1.length;
        double[] out1 = new double[sz];
        double[] out2 = new double[sz];
        for (int j = 0; j < sz; j++) {
            if (random.nextBoolean()) {
                out1[j] = p1[j];
                out2[j] = p2[j];
            } else {
                out1[j] = p2[j];
                out2[j] = p1[j];
            }
        }

        children.add(out1);
        children.add(out2);
        return children;
    }
}
