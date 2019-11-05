package ec.app.catering;
import ec.*;
import ec.simple.*;
import ec.vector.*;

public class Catering extends Problem implements SimpleProblemForm
    {
	private static int DURACION_TURNO = 10000;
    public void evaluate(final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum)


        {
        if (ind.evaluated) return;

//        System.out.println("-----------------------------");
        if (!(ind instanceof IntegerVectorIndividual))
            state.output.fatal("Error. No es un vector de enteros!",null);
        
        IntegerVectorIndividual ind2 = (IntegerVectorIndividual)ind;
        IntegerVectorSpecies t_spe = (IntegerVectorSpecies)ind2.species;
        Double[][] tiempos = t_spe.getTiempos();
        int[] capacidadesIniciales = t_spe.getCapacidadInicial();
        int cantCotenedores = t_spe.getCantContenedores();
        int cantCamiones= t_spe.getCantCamiones();
        int CAPACIDAD_CAMION= t_spe.getCapacidadCamion();
        //FILTRO Y ARREGLO LAS SOLICIONES INVALIDAS
        //SE PASAN DE PRESUPUESTO
        
        int indiceContenedor = 1;
        int tiempoCamion = DURACION_TURNO;
        int capacidadActual = CAPACIDAD_CAMION;

//        System.out.println(tiempos[ind2.genome[0]][ind2.genome[1]]);
        int fitness = 0;
        try {
        while(cantCamiones > 0) {
        	tiempoCamion = DURACION_TURNO;
            capacidadActual = CAPACIDAD_CAMION;
            //aca en el while despues le agregamos la distancia que le llelva desde el proximo hasta el felipe cardozo
//            System.out.println(ind2);
        	while(indiceContenedor < cantCotenedores  && tiempoCamion > tiempos[ind2.genome[indiceContenedor-1]][ind2.genome[indiceContenedor]]) {
//                System.out.println(indiceContenedor+"###"+tiempoCamion+"###"+capacidadActual+ "---"+capacidadesIniciales[indiceContenedor] +"genoma:"+ind2.genome[indiceContenedor]);
        		if(capacidadActual > capacidadesIniciales[indiceContenedor]) {
//                    System.out.println(indiceContenedor+"###"+tiempoCamion+"###"+capacidadActual+ "---"+capacidadesIniciales[indiceContenedor] +"genoma:"+ind2.genome[indiceContenedor]);
        			tiempoCamion -= tiempos[ind2.genome[indiceContenedor-1]][ind2.genome[indiceContenedor]];
        			capacidadActual -= capacidadesIniciales[ind2.genome[indiceContenedor]];
            		fitness += capacidadesIniciales[ind2.genome[indiceContenedor]];
            		indiceContenedor+=1;

        		}else {
        			//volvemos al felipe cardozo
        			tiempoCamion -= tiempos[ind2.genome[indiceContenedor-1]][ind2.genome[cantCotenedores]];;
        			capacidadActual = CAPACIDAD_CAMION;
        			
        		}

//                System.out.println(indiceContenedor+"###"+tiempoCamion+"###"+capacidadActual+ "---"+capacidadesIniciales[indiceContenedor] +"genoma:"+ind2.genome[indiceContenedor]);

        	}
        	cantCamiones--;
        }

        }catch (Exception e) {
        	System.out.println(indiceContenedor);
        	System.out.println(cantCotenedores);
        	System.out.println(tiempoCamion);
        	System.out.println(ind2.genome[indiceContenedor-1]);
        	System.out.println(ind2.genome[indiceContenedor]);
        	System.out.println(tiempos[ind2.genome[indiceContenedor-1]][ind2.genome[indiceContenedor]]);

			e.printStackTrace();
		}
//        System.out.println("salgo del while");
        
        
        
        
        //Asigno el fitness al individuo
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Error. No es un SimpleFitness",null);
        ((SimpleFitness)ind2.fitness).setFitness(state,fitness, fitness > 9999999);
        ind2.evaluated = true;
        }
    }
