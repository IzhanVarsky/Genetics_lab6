package lab5;

import org.uncommons.watchmaker.framework.*;
import org.uncommons.watchmaker.framework.islands.IslandEvolution;
import org.uncommons.watchmaker.framework.islands.IslandEvolutionObserver;
import org.uncommons.watchmaker.framework.islands.Migration;
import org.uncommons.watchmaker.framework.islands.RingMigration;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class IslandsAlg {
    static int res_gen = 0;
    static double res_value = 0;
    static long res_time;
    static long startTime;
    static long endTime;

    public static void main(String[] args) {
        startTime = System.currentTimeMillis();
        int dimension = 100; // dimension of problem
        int complexity = 5; // fitness estimation time multiplicator

        int islandCount = 2;
        int realPopSize = 20; // size of population
//        int populationSize = realPopSize / islandCount;
        int populationSize = 1;

        int epochLength = 50;
        int realIterationCount = 30000; // number of generations
        int generations = realIterationCount / epochLength;

        Random random = new Random(); // random

        CandidateFactory<double[]> factory = new MyFactory(dimension); // generation of solutions

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<EvolutionaryOperator<double[]>>();
        operators.add(new MyCrossover()); // Crossover
        operators.add(new MyMutation()); // Mutation
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<double[]>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection(); // Selection operator

        FitnessEvaluator<double[]> evaluator = new MultiFitnessFunction(dimension, complexity); // Fitness function

        final RingMigration ringMigration = new RingMigration();
        IslandEvolution<double[]> island_model = new IslandEvolution<double[]>(
                islandCount, ringMigration,
                factory,
                pipeline,
                evaluator,
                selection,
                random
        ); // your model;

        island_model.addEvolutionObserver(new IslandEvolutionObserver() {
            public void populationUpdate(PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
                if (bestFit > 9.5 && res_gen == 0) {
                    res_gen = populationData.getGenerationNumber();
                    res_value = bestFit;
                    res_time = System.currentTimeMillis();
                }
                System.out.println("Epoch " + populationData.getGenerationNumber() + ": " + bestFit);
                System.out.println("\tEpoch best solution = " + Arrays.toString((double[]) populationData.getBestCandidate()));
            }

            public void islandPopulationUpdate(int i, PopulationData populationData) {
                double bestFit = populationData.getBestCandidateFitness();
                if (bestFit > 9.5 && res_gen == 0) {
                    res_gen = populationData.getGenerationNumber();
                    res_value = bestFit;
                    res_time = System.currentTimeMillis();
                }
                System.out.println("Island " + i);
                System.out.println("\tGeneration " + populationData.getGenerationNumber() + ": " + bestFit);
                System.out.println("\tBest solution = " + Arrays.toString((double[]) populationData.getBestCandidate()));
            }
        });

        TerminationCondition terminate = new GenerationCount(generations);
        island_model.evolve(populationSize, 0, epochLength, 2, terminate);

        endTime = System.currentTimeMillis();
        System.out.println("============================================");
        System.out.printf("Fitness value %f (>9.5) achieved at iteration num: %d in %d ms.%n",
                res_value, res_gen, res_time - startTime);
        System.out.println("Total estimated time: " + (endTime - startTime) + " ms");
    }
}
