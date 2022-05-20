// Nueva clase

public class HistorialMensajes {
   
	public String Mensajes; //creacion variable
	
	public HistorialMensajes(){
        Mensajes = ""; //Inicializamos
    }

	public void appendMensaje(String nuevoMensaje){//concatena los mensajes
        Mensajes = Mensajes + "\n" + nuevoMensaje; //historial
    }

    public String getMensajes(){
        return Mensajes;
    }	
}


