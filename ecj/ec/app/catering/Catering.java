package ec.app.catering;
import ec.*;
import ec.simple.*;
import ec.vector.*;

public class Catering extends Problem implements SimpleProblemForm
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

        int [][] catering = t_spe.getCatering();
        
        //CALCULO EL FITNESS
        
        int fitness = 1;

        //Asigno el fitness al individuo
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Error. No es un SimpleFitness",null);
        ((SimpleFitness)ind2.fitness).setFitness(state,fitness*(-1), fitness < 0.0001);
        ind2.evaluated = true;
        }
    }
