package ec.app.proyecto;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ec.*;
import ec.simple.*;
import ec.vector.*;
import jdk.nashorn.internal.runtime.ListAdapter;

public class Proyecto extends Problem implements SimpleProblemForm
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
        
        
        //PRUEBO SI LOS FITNESS COINCIDEN
        int[] prueba= ind2.genome;
       
//        for (int i = 0; i < prueba.length; i++) {
//			ind2.genome[i]=prueba[i];
//		}
//        
        
        Double[][] tiempos = t_spe.getTiempos();
        String[][] contenedores= t_spe.getContenedores();
        int[] capacidadesIniciales = t_spe.getCapacidadInicial();
        int cantContenedores = t_spe.getCantContenedores();
        int cantCamiones= t_spe.getCantCamiones();
        int CAPACIDAD_CAMION= t_spe.getCapacidadCamion();
        int TIEMPO_PARADA= t_spe.getTiempoParada();
        Double DURACION_TURNO= t_spe.getDuracionTurno();
        //FILTRO Y ARREGLO LAS SOLICIONES INVALIDAS
        //SE PASAN DE PRESUPUESTO
        
        int indiceContenedor = 0;
        Double tiempoCamion = DURACION_TURNO;
        double capacidadActual = CAPACIDAD_CAMION;
        List<List<Integer>> contenedoresPorCamion= new ArrayList<List<Integer>>();
        List<Integer> listRecorrido = null;
        int ubicacionCamion=cantContenedores;
        double tiempo=0;
        double basura=0;
        double incremento=0;

        int fitness = 0;
        try {
        while(cantCamiones > 0) {
        	listRecorrido = new ArrayList<Integer>();
    		listRecorrido.add(cantContenedores);
        	tiempoCamion = DURACION_TURNO;
            capacidadActual = CAPACIDAD_CAMION;
            ubicacionCamion = cantContenedores;
            //aca en el while despues le agregamos la distancia que le llelva desde el proximo hasta el felipe cardozo
//            System.out.println(Arrays.toString(ind2.genome));
        	while(indiceContenedor < cantContenedores  && tiempoCamion >= tiempos[ubicacionCamion][ind2.genome[indiceContenedor]] + tiempos[ind2.genome[indiceContenedor]][cantContenedores] + TIEMPO_PARADA) {
//                System.out.println(indiceContenedor+"###"+tiempoCamion+"###"+capacidadActual+ "---"+capacidadesIniciales[indiceContenedor] +"genoma:"+ind2.genome[indiceContenedor]);
        		int contenedor= ind2.genome[indiceContenedor];
//        		System.out.println(tiempoCamion);
//        		System.out.println(tiempos[ubicacionCamion][ind2.genome[indiceContenedor]]+tiempos[ind2.genome[indiceContenedor]][cantContenedores]);
//        		System.out.println(contenedor);
        		
        		tiempo=tiempos[ubicacionCamion][contenedor];
        		incremento=(DURACION_TURNO-tiempoCamion+tiempo)*Double.valueOf(contenedores[contenedor][2]);
        		basura=capacidadesIniciales[contenedor] + incremento;
        		if(basura>100) basura = 100;
        		if(capacidadActual >= basura) {
//            		if(cantCamiones==1)System.out.println("contenedor:"+contenedor+"basura:"+basura+"-"+incremento+"-"+(DURACION_TURNO-tiempoCamion+tiempo));
//        			System.out.println("###capacidad:"+ capacidadActual);
//        			System.out.println("###tiempo:"+ tiempo);
//        			System.out.println("###basura:"+ basura);
        			if(tiempoCamion>=tiempos[ubicacionCamion][contenedor] + tiempos[contenedor][cantContenedores] + TIEMPO_PARADA) {
	//                    System.out.println(indiceContenedor+"###"+tiempoCamion+"###"+capacidadActual+ "---"+capacidadesIniciales[indiceContenedor] +"genoma:"+ind2.genome[indiceContenedor]);
	        			tiempoCamion -= tiempo;
	        			tiempoCamion -= TIEMPO_PARADA;
	        			capacidadActual -= basura;
	            		fitness += basura;
//	            		if(cantCamiones==1)System.out.println("fitns:"+fitness);
	            		indiceContenedor+=1;
	//            		System.out.println(indiceContenedor);
	            		listRecorrido.add(contenedor);
	            		ubicacionCamion=contenedor;
	        		}

        		}else {
                    //volvemos al felipe cardozo
//        			System.out.println("###capacidad2:"+ capacidadActual);
        			tiempoCamion -= tiempos[ubicacionCamion][cantContenedores];
        			capacidadActual = CAPACIDAD_CAMION;
            		listRecorrido.add(cantContenedores);
            		ubicacionCamion=cantContenedores;
        			
        		}

//                System.out.println(indiceContenedor+"###"+tiempoCamion+"###"+capacidadActual+ "---"+capacidadesIniciales[indiceContenedor] +"genoma:"+ind2.genome[indiceContenedor]);

        	}

//			System.out.println(tiempoCamion);
        	if(listRecorrido.get(listRecorrido.size()-1)!=cantContenedores) listRecorrido.add(cantContenedores);
        	contenedoresPorCamion.add(listRecorrido);
        	cantCamiones--;
        }

        }catch (Exception e) {
        	System.out.println(listRecorrido.toString());
        	System.out.println(indiceContenedor);
        	System.out.println(cantContenedores);
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
