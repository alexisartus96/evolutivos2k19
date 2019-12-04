package ec.app.proyecto;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class Generator {

	public static void main(String[] args) {
		try {
			
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("./ec/app/catering/capacidad_inicial.in"));
        int i = 0;
    	writer.newLine();
    	writer.write(Integer.toString(i));
    	writer.flush();
        Random rand = new Random(); 
        for (int j = 0; j < 188; j++) {
        	i=rand.nextInt(100);
        	writer.newLine();
        	writer.write(Integer.toString(i));
        	writer.flush();
			
		}
        writer.close();
        System.out.println("CARGA EXITOSA");
        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
