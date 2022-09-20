package principal;

import java.io.IOException;
import java.net.Socket;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/**
 *
 * @author MASTER
 */
public class ChatClient {

    /**
     * @param args the command line arguments
     */
    // servidor remoto ssh.chauchuty.cf
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private ClientSocket clientSocket;

    public ChatClient() {

    }

    public ClientSocket start() throws IOException {

        clientSocket = new ClientSocket(
                new Socket(SERVER_ADDRESS, 8089));
        System.out.println(clientSocket.getMessage());
       

        //Mesmo abrindo Thread aqui em cima o uso de memória aumenta, o gasto
        // não está relacionado a abertura de várias Threads
        // new Thread(() -> clientMessageReturnLoop(clientSocket)).start(); 
        /*THREAD ESTÁ OPERANDO NO CONTROLLER, O MÉTODO clientMessageReturnLoop 
             NÃO ESTÁ SENDO USADO NESSA CLASSE
           
         */
        // messageLoop();
        return clientSocket;
    }

    public void messageLoop(String msg) throws IOException {

        clientSocket.sendMsg(msg);

    }

    /* public void clientMessageReturnLoop(ClientSocket clientSocket) {

        String msg;

        while ((msg = clientSocket.getMessage()) != null) {//pega um interrupção de conexão do servidor, Ex: Queda
            if (msg.equalsIgnoreCase("Desconectado!"))  { //Desconecta pela solicitação do usuário
                break;
            }
            System.out.println(msg);
           
        }
        
        try {
            clientSocket.closeInOut();//função fecha o Socket
            System.out.println("Desconectado do Servidor");
            
          
        } catch (IOException ex) {
            System.out.println("Problemas ao encerrar conexão");
        }

    }
     */
}
