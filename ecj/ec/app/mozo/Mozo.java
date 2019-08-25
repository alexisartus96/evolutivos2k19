package ec.app.mozo;
import ec.*;
import ec.simple.*;
import ec.vector.*;

public class Mozo extends Problem implements SimpleProblemForm
    {
    public void evaluate(final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum)


        {
        if (ind.evaluated) return;

        if (!(ind instanceof IntegerVectorIndividual))
            state.output.fatal("Error. No es un vector de enteros!",null);
        
        IntegerVectorIndividual ind2 = (IntegerVectorIndividual)ind;
        IntegerVectorSpecies t_spe = (IntegerVectorSpecies)ind2.species;

        double suma = 0;
        double fitness=0;
        double [] precios = t_spe.getPrecios();

        //CALCULO EL FITNESS
        for (int i=0;i<ind2.genome.length;i++){
            suma += precios[ind2.genome[i]];
        }

        fitness = Math.abs(15.05 - suma);

        //Asigno el fitness al individuo
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Error. No es un SimpleFitness",null);
        ((SimpleFitness)ind2.fitness).setFitness(state,fitness*(-1), fitness == 0);
        ind2.evaluated = true;
        }
    }
