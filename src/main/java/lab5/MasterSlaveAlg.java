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
        int dimension = MyConfig.dimension; // dimension of problem
        int complexity = MyConfig.complexity; // fitness estimation time multiplicator
        int populationSize = MyConfig.populationSize; // size of population
        int generations = MyConfig.generations; // number of generations

        Random random = new Random(); // random

        CandidateFactory<double[]> factory = new MyFactory(dimension); // generation of solutions

        ArrayList<EvolutionaryOperator<double[]>> operators = new ArrayList<>();
        operators.add(new MyCrossover()); // Crossover
        operators.add(new MyMutation()); // Mutation
        EvolutionPipeline<double[]> pipeline = new EvolutionPipeline<>(operators);

        SelectionStrategy<Object> selection = new RouletteWheelSelection(); // Selection operator

        FitnessEvaluator<double[]> evaluator = new MultiFitnessFunction(dimension, complexity); // Fitness function

        AbstractEvolutionEngine<double[]> algorithm = new SteadyStateEvolutionEngine<double[]>(
                factory, pipeline, evaluator, selection, populationSize, false, random);

        algorithm.setSingleThreaded(false);

        algorithm.addEvolutionObserver(populationData -> {
            double bestFit = populationData.getBestCandidateFitness();
            if (bestFit > MyConfig.targetValue && res_gen == 0) {
                res_gen = populationData.getGenerationNumber();
                res_value = bestFit;
                res_time = System.currentTimeMillis();
            }
            System.out.println("Generation " + populationData.getGenerationNumber() + ": " + bestFit);
            System.out.println("\tBest solution = " + Arrays.toString(populationData.getBestCandidate()));
            System.out.println("\tPop size = " + populationData.getPopulationSize());
        });

        TerminationCondition terminate = new GenerationCount(generations);

        algorithm.evolve(populationSize, MyConfig.eliteCount, terminate);

        endTime = System.currentTimeMillis();
        System.out.println("============================================");
        System.out.printf("Fitness value %f (>%f) achieved at iteration num: %d in %d ms.%n",
                res_value, MyConfig.targetValue, res_gen, res_time - startTime);
        System.out.println("Total estimated time: " + (endTime - startTime) + " ms");
    }
}
