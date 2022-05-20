import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.util.*;

import java.net.*;
import java.io.*;
import java.lang.Thread;


public class UI_Cliente extends JFrame implements ActionListener{
    
    private JPanel panel;
	private JTextArea textArea;
	private JScrollPane jScrollPane;
	private JLabel lblUsuario;
	private JLabel lblIP;
	private JLabel lblPuerto;
	private JLabel lblNotificacion;
	private JTextField txtUsuario;
	private JTextField txtIP;
	private JTextField txtPuerto;
	private JButton btnConectar;
	private JTextField txtMensaje;
	private JButton btnEnviar;
	
	private Socket servidor;                   
    private PrintWriter out;
    private String ip;
    private int puerto;
    private String usuario;
    private String mensaje;

    private boolean conectado = false;
    private Monitor monitor;
    private Thread t;

    public UI_Cliente(){
        this.setTitle("MiCliente");
        panel = new JPanel();
        panel.setLayout(null);
		
		lblUsuario = new JLabel("Usuario:");
		txtUsuario = new JTextField();
		lblIP = new JLabel("IP destino:");
		txtIP = new JTextField();
		lblPuerto = new JLabel("Puerto:");
		txtPuerto = new JTextField();
		lblNotificacion = new JLabel("Desconectado");
		btnConectar = new JButton("Conectar");
		btnConectar.setBackground(Color.decode("#FF7171")); 
		textArea = new JTextArea("Mensajes:");
		textArea.setEditable(false);
		jScrollPane = new JScrollPane(textArea);
		txtMensaje = new JTextField();
		txtMensaje.setEnabled(false);
		btnEnviar =  new JButton("Enviar");
		btnEnviar.setEnabled(false);

		

		panel.add(lblUsuario);
		panel.add(txtUsuario);
		panel.add(lblIP);
		panel.add(txtIP);
		panel.add(lblPuerto);
		panel.add(txtPuerto);
		panel.add(lblNotificacion);
		panel.add(btnConectar);
		panel.add(jScrollPane);
		panel.add(txtMensaje);
		panel.add(btnEnviar);

		lblUsuario.setBounds(50, 25, 100, 20);
		txtUsuario.setBounds(130, 25, 200,20);
		lblIP.setBounds(50, 50, 100, 20);
		txtIP.setBounds(130, 50, 200, 20);
		lblPuerto.setBounds(50, 75, 100, 20);
		txtPuerto.setBounds(130, 75, 50, 20);
		lblNotificacion.setBounds(200, 75, 200, 20);
		btnConectar.setBounds(375, 25, 150, 50);
		jScrollPane.setBounds(50, 110, 525, 300);
		txtMensaje.setBounds(50, 415, 405, 75);
		btnEnviar.setBounds(455, 415, 120, 75);

		

        
        this.add(panel);
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        this.setSize(600,500);
        this.setVisible(true);
		
		btnConectar.addActionListener(this);
		btnEnviar.addActionListener(this);
		
    }

    
    public void actionPerformed(ActionEvent event){
        if(event.getSource() == btnConectar){
        	if(!conectado){
        		conectar();
        	}

    		else{
    			desconectar();
    		}
        }
        	
        else if(event.getSource() == btnEnviar){
        	enviar();
        }
    }
    
    public void conectar(){

		System.out.println("Conectando...");

		try
		{
			ip = txtIP.getText();
			puerto = Integer.parseInt(txtPuerto.getText());

			servidor = new Socket(ip, puerto); 
            monitor = new Monitor(servidor, textArea);
            t = new Thread(monitor);
            t.start();
            out = new PrintWriter(servidor.getOutputStream(),true);
            usuario = txtUsuario.getText();

            txtUsuario.setEnabled(false);
            txtIP.setEnabled(false);
            txtPuerto.setEnabled(false);
            btnConectar.setText("Desconectar");
            btnConectar.setBackground(Color.decode("#64FF69"));
            txtMensaje.setEnabled(true);
			btnEnviar.setEnabled(true);
			txtMensaje.requestFocusInWindow();

			lblNotificacion.setText("Conectado");
			conectado = true;

		}
		catch(Exception err){
            err.printStackTrace();
            lblNotificacion.setText("Error");
        }
    }

    public void desconectar(){
    	try
		{
			out.println("DSCNCTR");
			t.interrupt();
			servidor.close();
			txtUsuario.setEnabled(true);
            txtIP.setEnabled(true);
            txtPuerto.setEnabled(true);
            btnConectar.setText("Conectar");
            btnConectar.setBackground(Color.decode("#FF7171"));
            txtMensaje.setEnabled(false);
			btnEnviar.setEnabled(false);

			lblNotificacion.setText("Desconectado");
			conectado = false;
		}
		catch(Exception err){
            err.printStackTrace();
            lblNotificacion.setText("Error");
        }
    	
    }

    public void enviar(){
    	try
		{
			mensaje = txtMensaje.getText(); //el mensaje se toma del cuadro de texto
			//vamos a formar una cadena
			String cadena = usuario+": "+mensaje;//concatena el mensaje mas quien lo envia
			char[] chars = new char[cadena.length()];// crea una variable con la longitud de la cadena
			chars = cadena.toCharArray(); //convierte la varibale a un arreglo de caracteeres
			//out.println();
			for(int i=0; i<chars.length; i++){
				int ascii = (int) chars[i]+1;//convierte a ascci el valor de la letra, luego al valor entero le suma 1
				chars[i] = (char) ascii;//regresa el valor en literal
			}
			cadena = new String(chars);//
			out.println(cadena);
			txtMensaje.setText("");
			txtMensaje.requestFocusInWindow();
		}
		catch(Exception err){
            err.printStackTrace();
        }
    }
    
    
    public static void main(String[] args){
        UI_Cliente miCliente = new UI_Cliente();
    }



}


class Monitor implements Runnable{

    BufferedReader in;
    Socket servidor;
    JTextArea textArea;
    String msg;
	char[] chars;

    public Monitor(Socket servidor, JTextArea textArea){
        this.servidor = servidor;
        this.textArea = textArea;
    }

    @Override
    public void run(){
        try{  

            in = new BufferedReader(new InputStreamReader(servidor.getInputStream()));   //esta escuchando al servidor
           
            while((msg=in.readLine())!=null){
            	System.out.println(msg);
				char[] chars = new char[msg.length()];// crea una variable con la longitud de la cadena
			    chars = msg.toCharArray(); //convierte la varibale a un arreglo de caracteeres
				for(int i=0; i<chars.length; i++){
					int ascii = 0;
					ascii = chars[i]-1;//convierte a ascci el valor de la letra, luego al valor entero le resta 1
					chars[i] = (char) ascii;//regresa el valor en literal
				//	System.out.println(chars[i]);	
				}

				msg = new String(chars);

                textArea.setText(textArea.getText()+"\n"+msg);  
                
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}