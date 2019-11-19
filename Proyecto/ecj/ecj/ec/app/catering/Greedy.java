package ec.app.catering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ec.vector.IntegerVectorSpecies;

public class Greedy {

	private static int cantContenedores = 188;

	private static int cantCamiones = 3;
	private static double duracionTurno = 5000.0;
	private static int capacidadCamion = 300;
	private static double tiempParada= 50.0;
	private static String ruta_tiempos = "/home/andres/Escritorio/Fing/evolutivos2k19/Proyecto/ecj/ecj/ec/app/catering/tiempos_cordon.in";
	private static String ruta_generator = "/home/andres/Escritorio/Fing/evolutivos2k19/Proyecto/ecj/ecj/ec/app/catering/capacidad_inicial.in";
	private static int[] capacidadContenedores;
	private static String[][] contenedores;
	private static Double[][] tiempos;
	private static int[] capacidadCamiones = new int[cantCamiones];
	private static Double[] tiempoCamiones = new Double[cantCamiones];
	private static int[] ubicacionCamion = new int[cantCamiones];

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{

            //-----------CARGA DE CAPACIDADES INICIALES----------------------------
            
            //System.out.println(ruta_limite_barrios);
            File fin = new File(ruta_generator);
            FileInputStream fis = new FileInputStream(fin);

            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            br.readLine();
            System.out.println(cantContenedores);
            
            //Cargo el generador
            tiempos = new Double [cantContenedores+1][cantContenedores+1];
            capacidadContenedores = new int[cantContenedores];
            contenedores= new String[cantContenedores+1][2];
            
            for (int j = 0; j < cantContenedores; j++) {
            	capacidadContenedores[j]=Integer.parseInt(br.readLine());
			}
            
            System.out.println("CARGA DE CAPACIDADES INICIAL EXITOSA");

            br.close();
            
            //-----------CARGA DE TIEMPOS----------------------------
            //System.out.println(ruta_limite_barrios);
            fin = new File(ruta_tiempos);
            fis = new FileInputStream(fin);
            br = new BufferedReader(new InputStreamReader(fis));
            br.readLine();
            String line = null;
            String [] line_tokens=null;
            for (int j = 0; j <= cantContenedores ; j++) {
            	for (int j2 = 0; j2 <= cantContenedores ; j2++) {
            		if(j!=j2) {
                        line=br.readLine();
                        if(j2==0) {
                        	String punto = (line.split("\\*")[2].split(" ")[2]+"%2C"+line.split("\\*")[2].split(" ")[1]).replaceAll("\\(|\\)", "");
                        	String gid = line.split("\\*")[3].replaceAll(" ", "");
                        	contenedores[j][0]= gid;
                        	contenedores[j][1]= punto;
                        	System.out.println(Arrays.toString(contenedores[j]));
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
        }catch(IOException e){
            System.out.println(e+"hubo algun problema con la lectura del archivo");
        }
		
		
		//calculo de fitness
		int indiceCamion=0;
		int indiceContenedor=0;
		int mejorContenedor=0;
		int mejorCamion = 0;
		boolean hay_tiempo=true;
		int fitness=0;
		boolean[] visitados= new boolean[cantContenedores];
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
			mejorContenedor=0;
			for (indiceContenedor= 0; indiceContenedor < cantContenedores; indiceContenedor++) {
				if(capacidadContenedores[indiceContenedor] > capacidadContenedores[mejorContenedor] && !visitados[indiceContenedor]) {
					mejorContenedor=indiceContenedor;
				}
			}

			visitados[mejorContenedor]=true;
			
			if((mejorCamion = mejorCamion(mejorContenedor))>=0) {
				//si tiene que volver a deposito resto el tiempo del viaje de descarga
				if(capacidadCamiones[mejorCamion] < capacidadContenedores[mejorContenedor]) {
					tiempoCamiones[mejorCamion] -= tiempos[ubicacionCamion[mejorCamion]][cantContenedores]+tiempos[cantContenedores][mejorContenedor]+tiempParada;
				}else {
					tiempoCamiones[mejorCamion] -= tiempos[ubicacionCamion[mejorCamion]][mejorContenedor]+tiempParada;
				}
				capacidadCamiones[mejorCamion] -= capacidadContenedores[mejorContenedor];
				ubicacionCamion[mejorCamion]=mejorContenedor;
				fitness+=capacidadContenedores[mejorContenedor];
				capacidadContenedores[mejorContenedor]=0;
				contenedoresVisitados.get(mejorCamion).add(mejorContenedor);
			}else {
				hay_tiempo=false;
			}
			
		}
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
		
	}
	

	public static int mejorCamion(int mejorContenedor) {
		int mejorCamion= -1;
		Double mejorTiempo=99999.0;
		double tiempoCamion = 0.0;
		//para cada camion me fijo cual esta mas cerca solo si tiene tiempo para recogerlo dentro del turno, si ninguno puede queda en -1 y el algoritmo termina
		for (int i = 0; i < cantCamiones; i++) {
			
			if((tiempoCamion = puede_recoger(i, mejorContenedor))>=0) {
				if(mejorTiempo > tiempoCamion) {
					mejorTiempo = tiempoCamion;
					mejorCamion = i;
				}
			}
		}
		
		
		return mejorCamion;
	}
	
	public static double puede_recoger(int camion, int contenedor) {
		double puede = -1;
		//Si tiene que ir a deposito antes de recogerlo me fijo que le de el tiempo para ir al depsito, luego al contenedor y luego al deposito devuelta
		if(capacidadCamiones[camion]<capacidadContenedores[contenedor] && tiempoCamiones[camion]> (tiempos[ubicacionCamion[camion]][cantContenedores]+tiempos[cantContenedores][contenedor]+tiempos[contenedor][cantContenedores])) {
				puede = tiempos[ubicacionCamion[camion]][cantContenedores]+tiempos[cantContenedores][contenedor]+tiempos[contenedor][cantContenedores];
		}else {
			//Si no tiene que ir al deposito me fijo que le de el tiempo para ir hasta el contenedor y volver al deposito.
			if(tiempoCamiones[camion]> tiempos[ubicacionCamion[camion]][contenedor]+tiempos[contenedor][cantContenedores]) {
				puede= tiempos[ubicacionCamion[camion]][contenedor]+tiempos[contenedor][cantContenedores];
			};
		}
		return puede;
	}
}
