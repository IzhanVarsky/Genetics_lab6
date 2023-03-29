package lab5;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MasterSlaveAlg {

    static int res_gen = 0;
    static double res_value = 0;
    static long res_time;
    static long startTime;
    static long endTime;

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        int dimension = 100; // dimension of problem
        int complexity = 5; // fitness estimation time multiplicator
        int populationSize = 40; // size of population
        int generations = 30000; // number of generations

        Random random = new Random(); // random

        CandidateFactory<double[]> factory = new MyFactory(dimension); // generation of solutions

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<EvolutionaryOperator<double[]>>();
        operators.add(new MyCrossover()); // Crossover
        operators.add(new MyMutation()); // Mutation
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<double[]>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection(); // Selection operator

        FitnessEvaluator<double[]> evaluator = new MultiFitnessFunction(dimension, complexity); // Fitness function

        AbstractEvolutionEngine<double[]> algorithm = new SteadyStateEvolutionEngine<double[]>(
                factory, pipeline, evaluator, selection, populationSize, false, random);

        algorithm.setSingleThreaded(false);

        algorithm.addEvolutionObserver(new EvolutionObserver() {
            public void populationUpdate(PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
                if (bestFit > 9.5 && res_gen == 0) {
                    res_gen = populationData.getGenerationNumber();
                    res_value = bestFit;
                    res_time = System.currentTimeMillis();
                }
                System.out.println("Generation " + populationData.getGenerationNumber() + ": " + bestFit);
                System.out.println("\tBest solution = " + Arrays.toString((double[]) populationData.getBestCandidate()));
                System.out.println("\tPop size = " + populationData.getPopulationSize());
            }
        });

        TerminationCondition terminate = new GenerationCount(generations);

        algorithm.evolve(populationSize, 3, terminate);

        endTime = System.currentTimeMillis();
        System.out.println("============================================");
        System.out.printf("Fitness value %f (>9.5) achieved at iteration num: %d in %d ms.%n",
                res_value, res_gen, res_time - startTime);
        System.out.println("Total estimated time: " + (endTime - startTime) + " ms");
    }
}
