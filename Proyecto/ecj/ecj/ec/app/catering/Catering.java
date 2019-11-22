package ec.app.catering;
import java.util.ArrayList;
import java.util.Arrays;
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
        
        
        //PRUEBO SI LOS FITNESS COINCIDEN
        int[] prueba= ind2.genome;
//        prueba[0]=122;
//        prueba[1]=154;
//        prueba[2]=136;
//        prueba[3]=167;
//        prueba[4]=164;
//        prueba[5]=162;
//        prueba[6]=86;
//        prueba[7]=87;
//        prueba[8]=88;
//        prueba[9]=96;
//        prueba[10]=47;
//        prueba[11]=91;
//        prueba[12]=119;
//        prueba[13]=112;
//        prueba[14]=85;
//        prueba[15]=81;
//        prueba[16]=80;
//        prueba[17]=83;
//        prueba[18]=115;
//        prueba[19]=89;
//        prueba[20]=23;
//        prueba[21]=163;
//        prueba[22]=161;
//        prueba[23]=170;
//        prueba[24]=169;
//        prueba[25]=150;
//        prueba[26]=98;
//        prueba[27]=100;
//        prueba[28]=95;
//        prueba[29]=155;
//        prueba[30]=152;
        
//        prueba[0]=163;
//        prueba[1]=173;
//        prueba[2]=153;
//        prueba[3]=152;
//        prueba[4]=76;
//        prueba[5]=78;
//        prueba[6]=81;
//        prueba[7]=120;
//        prueba[8]=150;
//        prueba[9]=161;
//        prueba[10]=154;
//        prueba[11]=132;
//        prueba[12]=131;
//        prueba[13]=149;
//        prueba[14]=77;
        for (int i = 0; i < prueba.length; i++) {
			ind2.genome[i]=prueba[i];
		}
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
        int capacidadActual = CAPACIDAD_CAMION;
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
        	while(indiceContenedor < cantContenedores  && tiempoCamion >= tiempos[ubicacionCamion][ind2.genome[indiceContenedor]]+tiempos[ind2.genome[indiceContenedor]][cantContenedores]) {
//                System.out.println(indiceContenedor+"###"+tiempoCamion+"###"+capacidadActual+ "---"+capacidadesIniciales[indiceContenedor] +"genoma:"+ind2.genome[indiceContenedor]);
        		int contenedor= ind2.genome[indiceContenedor];
//        		System.out.println(tiempoCamion);
//        		System.out.println(tiempos[ubicacionCamion][ind2.genome[indiceContenedor]]+tiempos[ind2.genome[indiceContenedor]][cantContenedores]);
//        		System.out.println(contenedor);
        		
        		tiempo=tiempos[ubicacionCamion][contenedor];
        		incremento=(DURACION_TURNO-tiempoCamion+tiempo)*Double.valueOf(contenedores[contenedor][2]);
        		basura=capacidadesIniciales[contenedor] + incremento;
        		if(capacidadActual >= basura) {
//            		if(cantCamiones==1)System.out.println("contenedor:"+contenedor+"basura:"+basura+"-"+incremento+"-"+(DURACION_TURNO-tiempoCamion+tiempo));
//        			System.out.println("###capacidad:"+ capacidadActual);
//        			System.out.println("###tiempo:"+ tiempo);
//        			System.out.println("###basura:"+ basura);
        			if(tiempoCamion>=tiempos[ubicacionCamion][contenedor] + tiempos[contenedor][cantContenedores]) {
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
