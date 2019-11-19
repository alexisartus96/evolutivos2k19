package ec.app.catering;
import java.util.ArrayList;
import java.util.List;

import ec.*;
import ec.simple.*;
import ec.vector.*;
import jdk.nashorn.internal.runtime.ListAdapter;

public class Catering extends Problem implements SimpleProblemForm
    {
//	private static int DURACION_TURNO = 5000;
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
        int TIEMPO_PARADA= t_spe.getTiempoParada();
        Double DURACION_TURNO= t_spe.getDuracionTurno();
        //FILTRO Y ARREGLO LAS SOLICIONES INVALIDAS
        //SE PASAN DE PRESUPUESTO
        
        int indiceContenedor = 1;
        Double tiempoCamion = DURACION_TURNO;
        int capacidadActual = CAPACIDAD_CAMION;
        List<List<Integer>> contenedoresPorCamion= new ArrayList<List<Integer>>();
        List<Integer> listRecorrido = null;
        

//        System.out.println(tiempos[ind2.genome[0]][ind2.genome[1]]);
        int fitness = 0;
        try {
        while(cantCamiones > 0) {
        	listRecorrido = new ArrayList<Integer>();
    		listRecorrido.add(cantCotenedores);
        	tiempoCamion = DURACION_TURNO - tiempos[cantCotenedores][ind2.genome[indiceContenedor-1]];
            capacidadActual = CAPACIDAD_CAMION;
            //aca en el while despues le agregamos la distancia que le llelva desde el proximo hasta el felipe cardozo
//            System.out.println(ind2);
        	while(indiceContenedor < cantCotenedores  && tiempoCamion > tiempos[ind2.genome[indiceContenedor-1]][ind2.genome[indiceContenedor]]+tiempos[ind2.genome[indiceContenedor]][cantCotenedores]) {
//                System.out.println(indiceContenedor+"###"+tiempoCamion+"###"+capacidadActual+ "---"+capacidadesIniciales[indiceContenedor] +"genoma:"+ind2.genome[indiceContenedor]);
        		if(capacidadActual > capacidadesIniciales[indiceContenedor]) {
//                    System.out.println(indiceContenedor+"###"+tiempoCamion+"###"+capacidadActual+ "---"+capacidadesIniciales[indiceContenedor] +"genoma:"+ind2.genome[indiceContenedor]);
        			tiempoCamion -= tiempos[ind2.genome[indiceContenedor-1]][ind2.genome[indiceContenedor]];
        			tiempoCamion -= TIEMPO_PARADA;
        			capacidadActual -= capacidadesIniciales[ind2.genome[indiceContenedor]];
            		fitness += capacidadesIniciales[ind2.genome[indiceContenedor]];
            		indiceContenedor+=1;
            		listRecorrido.add(ind2.genome[indiceContenedor]);
            		

        		}else {
                    //volvemos al felipe cardozo
        			tiempoCamion -= tiempos[ind2.genome[indiceContenedor-1]][cantCotenedores];
        			capacidadActual = CAPACIDAD_CAMION;
            		listRecorrido.add(cantCotenedores);
        			
        		}

//                System.out.println(indiceContenedor+"###"+tiempoCamion+"###"+capacidadActual+ "---"+capacidadesIniciales[indiceContenedor] +"genoma:"+ind2.genome[indiceContenedor]);

        	}
        	if(listRecorrido.get(listRecorrido.size()-1)!=cantCotenedores) listRecorrido.add(cantCotenedores);
        	contenedoresPorCamion.add(listRecorrido);
        	cantCamiones--;
        }

        }catch (Exception e) {
        	System.out.println(listRecorrido.toString());
        	System.out.println(indiceContenedor);
        	System.out.println(cantCotenedores);
        	System.out.println(tiempoCamion);
        	System.out.println(ind2.genome[indiceContenedor-1]);
        	System.out.println(ind2.genome[indiceContenedor]);
            System.out.println(tiempos[ind2.genome[indiceContenedor-1]][ind2.genome[indiceContenedor]]);
            System.out.println(capacidadesIniciales[indiceContenedor]);

			e.printStackTrace();
		}
//        System.out.println("salgo del while");
        
        ind2.setRecorrido(contenedoresPorCamion);
        
        
        
        //Asigno el fitness al individuo
        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Error. No es un SimpleFitness",null);
        ((SimpleFitness)ind2.fitness).setFitness(state,fitness, fitness > 9999999);
        ind2.evaluated = true;
        }
    }
