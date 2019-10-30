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

        System.out.println("CARGA EXITOSA");
        if (!(ind instanceof IntegerVectorIndividual))
            state.output.fatal("Error. No es un vector de enteros!",null);
        
        IntegerVectorIndividual ind2 = (IntegerVectorIndividual)ind;
        IntegerVectorSpecies t_spe = (IntegerVectorSpecies)ind2.species;
        System.out.println("estoy");
        Double[][] distancias = t_spe.getDistancias();
        
//        int [][] catering = t_spe.getCatering();
        
        //FILTRO Y ARREGLO LAS SOLICIONES INVALIDAS
        //SE PASAN DE PRESUPUESTO
        
        System.out.println(distancias.length);
        
        int fitness = 0;
        for (int i = 1; i < ind2.size(); i++) {
        	System.out.println(ind2.genome[i]);
        	fitness += distancias[ind2.genome[i-1]-1][ind2.genome[i]-1];
		}
        
        
        
        //Asigno el fitness al individuo
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Error. No es un SimpleFitness",null);
        ((SimpleFitness)ind2.fitness).setFitness(state,-1*fitness, fitness > 9999999);
        ind2.evaluated = true;
        }
    }
