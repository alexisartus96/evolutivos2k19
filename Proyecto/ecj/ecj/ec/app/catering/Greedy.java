package ec.app.catering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ec.vector.IntegerVectorSpecies;

public class Greedy {

	private static int cantContenedores = 188;

	private static int cantCamiones = 3;
	private static double duracionTurno = 5000.0;
	private static double capacidadCamion = 300;
	private static double tiempoParada= 50.0;
	private static String ruta_tiempos = "/home/andres/Escritorio/Fing/evolutivos2k19/Proyecto/ecj/ecj/ec/app/catering/tiempos_cordon.in";
	private static String ruta_generator = "/home/andres/Escritorio/Fing/evolutivos2k19/Proyecto/ecj/ecj/ec/app/catering/capacidad_inicial.in";
	private static String ruta_personas_contenedor = "/home/andres/Escritorio/Fing/evolutivos2k19/Proyecto/ecj/ecj/ec/app/catering/personas_cont_cordon.in";
	private static int[] capacidadContenedores;
	private static String[][] contenedores;
	private static Double[][] tiempos;
	private static double[] capacidadCamiones = new double[cantCamiones];
	private static Double[] tiempoCamiones = new Double[cantCamiones];
	private static int[] ubicacionCamion = new int[cantCamiones];
	private static Map<Integer, String> personasContenedor= new HashMap<Integer, String>();

	private static boolean[] visitados;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			


            //-----------CARGA DE CAPACIDADES INICIALES----------------------------
            
            //System.out.println(ruta_limite_barrios);
            File fin = new File(ruta_generator);
            FileInputStream fis = new FileInputStream(fin);

            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            br.readLine();
            
            //Cargo el generador
            tiempos = new Double [cantContenedores+1][cantContenedores+1];
            capacidadContenedores = new int[cantContenedores];
            contenedores= new String[cantContenedores+1][3];
            
            for (int j = 0; j < cantContenedores; j++) {
            	capacidadContenedores[j]=Integer.parseInt(br.readLine());
			}
            
            System.out.println("CARGA DE CAPACIDADES INICIAL EXITOSA");

            br.close();
            
            //-----------CARGA DE PERSONAS POR CONTENEDOR----------------------------
            
            //System.out.println(ruta_limite_barrios);
            fin = new File(ruta_personas_contenedor);
            fis = new FileInputStream(fin);

            String line = null;
            
            br = new BufferedReader(new InputStreamReader(fis));
            br.readLine();
                        
            for (int j = 0; j < cantContenedores; j++) {
            	line= br.readLine();
            	personasContenedor.put(Integer.valueOf(line.split(";")[3].replaceAll(" ", "")), line.split(";")[4].replace(",", "."));
			}
            
            System.out.println("CARGA DE PERSONAS POR CONTENEDOR EXITOSA");

            br.close();
            
            //-----------CARGA DE TIEMPOS----------------------------
            //System.out.println(ruta_limite_barrios);
            fin = new File(ruta_tiempos);
            fis = new FileInputStream(fin);
            br = new BufferedReader(new InputStreamReader(fis));
            br.readLine();
            line = null;
            String [] line_tokens=null;
            for (int j = 0; j <= cantContenedores ; j++) {
            	for (int j2 = 0; j2 <= cantContenedores ; j2++) {
            		if(j!=j2) {
                        line=br.readLine();
                        if(j2<=1) {
                        	String punto = (line.split("\\*")[2].split(" ")[2]+"%2C"+line.split("\\*")[2].split(" ")[1]).replaceAll("\\(|\\)", "");
                        	String gid = line.split("\\*")[3].replaceAll(" ", "");
                        	contenedores[j][0]= gid;
                        	contenedores[j][1]= punto;
                        	if(Integer.valueOf(gid)!=0) contenedores[j][2]=Double.toString(Double.valueOf(personasContenedor.get(Integer.valueOf(gid)))/1000);
//                        	System.out.println(Arrays.toString(contenedores[j]));
                        }
                        line_tokens = line.split("\\*");
						if(line_tokens.length>4) {
							JSONObject jsonObj;
							try {
							jsonObj = new JSONObject(line_tokens[5]);
							JSONArray jsonArray = (JSONArray) jsonObj.get("routes");
							jsonObj = (JSONObject) jsonArray.get(0);
							tiempos[j][j2]=new Double(jsonObj.get("duration").toString());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}
            		}else {
            			tiempos[j][j]=0.0;
            		}
//            		System.out.println(Integer.toString(j) + '-' + Integer.toString(j2) + '-' + tiempos[j][j2]);
				}
				
			}
            System.out.println("CARGA DE TIEMPOS EXITOSA");

            br.close();
        }catch(Exception e){
            System.out.println(e+"hubo algun problema con la lectura del archivo");
        }
		
		
		//calculo de fitness
		int indiceCamion=0;
		int indiceContenedor=0;
		double[][] mejorContenedor= new double[cantCamiones][5];
		int mejorCamion = 0;
		boolean hay_tiempo=true;
		boolean hay_repetidos=true;
		int fitness=0;
		visitados= new boolean[cantContenedores];
		List<List<Integer>> contenedoresVisitados = new ArrayList<List<Integer>>();
		
		
		//inicializo los camiones con el tiempo y capacidad al maximo. Y origen en el felipe cardozo (0).
		
		for (int i = 0; i < cantContenedores; i++) {
			visitados[i]=false;
		}
		
		for (int i = 0; i < cantCamiones; i++) {
			contenedoresVisitados.add(new ArrayList<Integer>());
			capacidadCamiones[i]= capacidadCamion;
			tiempoCamiones[i]= duracionTurno;
			ubicacionCamion[i]=cantContenedores;
			contenedoresVisitados.get(i).add(cantContenedores);
			
		}
		
		while(hay_tiempo) {
			hay_tiempo=false;
			hay_repetidos=true;
			for (int i = 0; i < cantCamiones; i++) {
				mejorContenedor[i][4]=0.0;
			}
			while(hay_repetidos) {
				hay_repetidos=false;
				for (int i = 0; i < cantCamiones; i++) {
					/*
					 * mejorContenedor[0] id contenedor
					 * mejorContenedor[1] tiempo hasta el contenedor
					 * mejorContenedor[2] calidad precio
					 * mejorContenedor[3] si pasa por deposito
					 * */
					//Me fijo si ya tiene un conenedor asignado
					if(mejorContenedor[i][4]==0) {
						double[] mejorContenedorAux= mejorContenedor(i);
						mejorContenedor[i][0]=mejorContenedorAux[0];
						mejorContenedor[i][1]=mejorContenedorAux[1];
						mejorContenedor[i][2]=mejorContenedorAux[2];
						mejorContenedor[i][3]=mejorContenedorAux[3];
						if(mejorContenedor[i][0]>0) {
							mejorContenedor[i][4]=1.0;
							hay_tiempo=true;
						}
					}
				
					
				}
				//Me fijo si hay repetidos
				for(int i = 0; i < cantCamiones; i++) {
					if(mejorContenedor[i][4]>0){
						for (int j = 0; j < cantCamiones; j++) {
							if(j!=i && mejorContenedor[i][0]==mejorContenedor[j][0]) {
								hay_repetidos=true;
								if(mejorContenedor[i][2]>=mejorContenedor[j][2]) {
									mejorContenedor[j][4]=0.0;
									
								}
							}
							visitados[(int)mejorContenedor[i][0]]=true;
						}
					}
				}
			}
			for (int i = 0; i < cantCamiones; i++) {
				int contenedor= (int) mejorContenedor[i][0];
				if(contenedor>=0) {
					double tiempo = mejorContenedor[i][1];
					double incremento=(duracionTurno-tiempoCamiones[i]+tiempo)*Double.valueOf(contenedores[contenedor][2]);
					double basura= capacidadContenedores[contenedor] + incremento;
					if(i==2)System.out.println(basura+"-"+incremento+"-"+(duracionTurno-tiempoCamiones[i]+tiempo));
					//paso por deposito
					if(mejorContenedor[i][3]>0) {
						capacidadCamiones[i] = capacidadCamion - basura;
						contenedoresVisitados.get(i).add(cantContenedores);
					}else {
						capacidadCamiones[i] -= basura;
					}
					ubicacionCamion[i]=contenedor;
					fitness+=basura;
//					System.out.println(basura);
					tiempoCamiones[i]-=(tiempo+tiempoParada);
//					capacidadContenedores[contenedor]=0;
					contenedoresVisitados.get(i).add(contenedor);
				}
			}			
		}
		for (int i = 0; i < cantCamiones; i++) {
			contenedoresVisitados.get(i).add(cantContenedores);
		}
		
//		while(hay_tiempo) {
//			mejorContenedor=0;
//			for (indiceContenedor= 0; indiceContenedor < cantContenedores; indiceContenedor++) {
//				if(capacidadContenedores[indiceContenedor] > capacidadContenedores[mejorContenedor] && !visitados[indiceContenedor]) {
//					mejorContenedor=indiceContenedor;
//				}
//			}
//
//			visitados[mejorContenedor]=true;
//			
//			if((mejorCamion = mejorCamion(mejorContenedor))>=0) {
//				//si tiene que volver a deposito resto el tiempo del viaje de descarga
//				if(capacidadCamiones[mejorCamion] < capacidadContenedores[mejorContenedor]) {
//					tiempoCamiones[mejorCamion] -= tiempos[ubicacionCamion[mejorCamion]][cantContenedores]+tiempos[cantContenedores][mejorContenedor]+tiempoParada;
//				}else {
//					tiempoCamiones[mejorCamion] -= tiempos[ubicacionCamion[mejorCamion]][mejorContenedor] + tiempoParada;
//				}
//				capacidadCamiones[mejorCamion] -= capacidadContenedores[mejorContenedor];
//				ubicacionCamion[mejorCamion]=mejorContenedor;
//				fitness+=capacidadContenedores[mejorContenedor];
//				capacidadContenedores[mejorContenedor]=0;
//				contenedoresVisitados.get(mejorCamion).add(mejorContenedor);
//			}else {
//				hay_tiempo=false;
//			}
//			
//		}
		String s="";
		System.out.println("FITNESS: "+ fitness);
//		System.out.println(contenedoresVisitados.toString());
		for (List<Integer> list : contenedoresVisitados) {
        	s="http://localhost:9966/?z=16&center=-34.907807%2C-56.168708";
			for (Integer contenedor : list) {
				s+="&loc=";
	        	s+=contenedores[contenedor][1]; 
			}
			s+="&loc="+contenedores[cantContenedores][1];
			System.out.println(s);
		}
		int aux=cantContenedores;
		for (List<Integer> list : contenedoresVisitados) {
        	s="\n";
			for (Integer contenedor : list) {
				if(contenedor<cantContenedores) {
					s+=contenedor+"#"+capacidadContenedores[contenedor]+"+"+tiempos[aux][contenedor]+";";
				}else s+=contenedor+"+"+tiempos[aux][contenedor]+";";
				aux=contenedor;
			}

			System.out.println(s);
		}
		
	}
	

//	public static int mejorCamion(int mejorContenedor) {
//		int mejorCamion= -1;
//		Double mejorTiempo=99999.0;
//		double tiempoCamion = 0.0;
//		//para cada camion me fijo cual esta mas cerca solo si tiene tiempo para recogerlo dentro del turno, si ninguno puede queda en -1 y el algoritmo termina
//		for (int i = 0; i < cantCamiones; i++) {
//			
//			if((tiempoCamion = puede_recoger(i, mejorContenedor))>=0) {
//				if(mejorTiempo > tiempoCamion) {
//					mejorTiempo = tiempoCamion;
//					mejorCamion = i;
//				}
//			}
//		}
//		
//		
//		return mejorCamion;
//	}
	
	public static double[] puede_recoger(int camion, int contenedor) {
		double[] puede = new double[2];
		puede[0]=-1;
		puede[1]=0;
		if(!visitados[contenedor]) {
			//Si tiene que ir a deposito antes de recogerlo me fijo que le de el tiempo para ir al depsito, luego al contenedor y luego al deposito devuelta
			if(!tiene_capacidad(camion, contenedor, false) && tiempoCamiones[camion]> (getTiempoPorDeposito(camion, contenedor)+tiempos[contenedor][cantContenedores])) {
					puede[0] = getTiempoPorDeposito(camion, contenedor);
					puede[1]=1;
			}else {
				//Si no tiene que ir al deposito me fijo que le de el tiempo para ir hasta el contenedor y volver al deposito.
				if(tiene_capacidad(camion, contenedor, true) && tiempoCamiones[camion]> getTiempo(camion, contenedor)+tiempos[contenedor][cantContenedores]) {
					puede[0]= getTiempo(camion, contenedor);
				};
			}
		}
		return puede;
	}
	
	public static boolean tiene_capacidad(int camion, int contenedor, boolean porDeposito) {
		if(capacidadCamiones[camion] >= capacidadActual(camion, contenedor, porDeposito))
			return true;
		else return false;
	}
	
	public static double capacidadActual(int camion, int contenedor, boolean porDeposito) {
		if(porDeposito) return capacidadContenedores[contenedor] + getIncrementoPorDeposito(camion, contenedor);
		return capacidadContenedores[contenedor] + getIncrementoDirecto(camion, contenedor);
	}
	
	public static double getTiempo(int camion, int contenedor) {
		return tiempos[ubicacionCamion[camion]][contenedor];
	}
	public static double getTiempoPorDeposito(int camion, int contenedor) {
		return tiempos[ubicacionCamion[camion]][cantContenedores]+tiempos[cantContenedores][contenedor];
	}
	
	public static double getIncrementoDirecto(int camion, int contenedor) {
		return (duracionTurno-tiempoCamiones[camion]+getTiempo(camion, contenedor))*Double.valueOf(contenedores[contenedor][2]);
	}
	
	public static double getIncrementoPorDeposito(int camion, int contenedor) {
		return (duracionTurno-tiempoCamiones[camion]+getTiempoPorDeposito(camion, contenedor))*Double.valueOf(contenedores[contenedor][2]);
	}
	public static double[] mejorContenedor(int camion) {
		double[] mejorContenedor= new double[4];
		mejorContenedor[0]=-1;
		mejorContenedor[1]=-1;
		mejorContenedor[2]=-1;
		
		double[] tiempo=new double[2];
		tiempo[0]=-1;
		tiempo[1]=0;
		double mejorCalidadPrecio=0.0;
		double calidadPrecio=0.0;
		for (int indiceContenedor= 0; indiceContenedor < cantContenedores; indiceContenedor++) {
			if((tiempo=puede_recoger(camion, indiceContenedor))[0]>0) {
				if((calidadPrecio=capacidadContenedores[indiceContenedor]/tiempo[0]+Double.valueOf(contenedores[indiceContenedor][2]))>mejorCalidadPrecio) {
					mejorContenedor[0]=indiceContenedor;
					mejorContenedor[1]= tiempo[0];
					mejorContenedor[2]= calidadPrecio;
					mejorContenedor[3]= tiempo[1];
					mejorCalidadPrecio=calidadPrecio;
				}
			}
		}
		
		return mejorContenedor;
	}
}
