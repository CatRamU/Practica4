import java.net.*;
import java.io.*;
import java.lang.Thread;
import java.util.ArrayList;

public class MyServer{


	protected static ArrayList<Servicio> servicios = new ArrayList<Servicio>();
	protected static ArrayList<Socket> clientes = new ArrayList<Socket>();
	public static HistorialServidor historial = new HistorialServidor();  //Objeto de la clase HistorialServidor mensajes
	 
    public static void main(String[] args){
		
		ServerSocket server = null;
		
		try{
			server = new ServerSocket(5001);
		}catch(Exception e){
            e.printStackTrace();
        }
		
		while(true){
			try{
				Socket cliente = server.accept();
				clientes.add(cliente);
				Servicio s = new Servicio(cliente, historial); //Agregamos otro parametro para el historial
				servicios.add(s);
				Thread t = new Thread(s);
				t.start();

				for(Socket client : clientes)
					System.out.println("actual: "+client);


				for(Servicio ser : servicios)
					ser.actualizar(clientes);
				

			}catch(Exception e){
				e.printStackTrace();
			}
		}
    }
	
}


class Servicio implements Runnable{
	protected Socket cliente;
	protected PrintWriter out;                        
    protected BufferedReader in;
    protected ArrayList<Socket> clientes = new ArrayList<Socket>();
	protected String msg;
	protected HistorialServidor historial; // declaramos variable para el historial

	public Servicio(Socket cliente, HistorialServidor historial){ //Agregamos el parametro para el historial
		this.cliente = cliente;
		this.historial = historial; //historial de este metodo
	}

	public void actualizar(ArrayList<Socket> clientes){
		this.clientes = clientes;
	}
	
	@Override
	public void run(){
		try{                                                                                                             
			in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));   
			
			out = new PrintWriter(this.cliente.getOutputStream(),true);// Ducto salida para historial
			out.println(historial.getMensajes()); //Imprime en pantalla el historial
			
			while((msg = in.readLine())!=null && !msg.equals("DSCNCTR")){
				for(Socket cliente : this.clientes){
					out = new PrintWriter(cliente.getOutputStream(),true);
					out.println(msg);
					historial.appendMensaje(msg); //Concatena HistorialServidor + mensajes nuevos
					System.out.println("Enviando a: "+cliente);
				}				
			}

			clientes.remove(cliente);

		}catch(Exception e){
			e.printStackTrace();
		}
	}
}



