// Nueva clase

public class HistorialServidor {
   
	public String msj; //creacion variable
	
	public HistorialServidor(){
        msj = ""; //Inicializamos
    }

	public void appendMensaje(String nuevoMsj){//concatena los mensajes
        msj = msj + "\n" + nuevoMsj; //HistorialServidor
    }

    public String getMensajes(){
        return msj;
    }	
}


