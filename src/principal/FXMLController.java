/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package principal;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * FXML Controller class
 *
 * @author MASTER
 */
public class FXMLController implements Initializable {

    //private TextField textPalavra;
    //private TextArea textAreaPalavra;
    @FXML
    private Button btnConectar;
    ChatClient cliente;
    @FXML
    private Button btnEnviar;
    @FXML
    private Button btnDesconectar;
    @FXML
    private TextArea textAreaChat;
    @FXML
    private TextField textFieldMensagem;
    ClientSocket clientSocket;
    @FXML
    private TextField textUsuario;
    @FXML
    private Label lbSenha;
    @FXML
    private TextField textSenha;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cliente = new ChatClient();

    }

    // TODO
    @FXML
    private void conectar(ActionEvent event) throws IOException {

        try {
            clientSocket = cliente.start();
            new Thread(() -> clientMessageReturnLoop()).start();

            btnEnviar.setDisable(false);
            btnDesconectar.setDisable(false);
            btnConectar.setDisable(true);
        } catch (IOException ex) {
            System.out.println("Servidor Encontra-se Offline!");
        }

        /* {
            "operacao":"login",
             "params":{
                        "ra":"AAAAAAA",
                        "senha":"123456"
                        }
            }
         */
        JSONObject params = new JSONObject();
        params.put("ra", textUsuario.getText());
        params.put("senha", textSenha.getText());

        JSONObject obj = new JSONObject();
        obj.put("operacao", "login");
        obj.put("params", params);
        System.out.println(obj);

        cliente.messageLoop(obj.toJSONString());
        //cliente.messageLoop("{\"ra\":\"" + textUsuario.getText() + "\",\"senha\":\"" + textSenha.getText() + "\"}");
        // Envia uma mensagem no momento da conexão para identificar o Cliente

    }

    @FXML
    private void desconectarChat(ActionEvent event) {

        try {
            cliente.messageLoop("sair#$%");
            btnEnviar.setDisable(true);
            btnDesconectar.setDisable(true);
            btnConectar.setDisable(false);
        } catch (IOException ex) {
            System.out.println("Problemas ao Encerrar Conexão");
        }
    }

    @FXML
    private void enviarMensagem(ActionEvent event) throws IOException {

        if (textFieldMensagem.getText().equals("")) {
            System.out.println("Campo está vazio: Digite algo");
        } else {
            //textAreaChat.getStyleClass().add("textArea-textAreaChat-envio");
            textAreaChat.appendText("[ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " ]" + " EU: " + textFieldMensagem.getText() + "\n");
            cliente.messageLoop(textFieldMensagem.getText());
        }
    }

    public void clientMessageReturnLoop() {

        String msg;
        String operacao = " ";
        String status = " ";

        while ((msg = clientSocket.getMessage()) != null) {//pega um interrupção de conexão do servidor, Ex: Queda
            if (msg.equalsIgnoreCase("null")) { //Desconecta pela solicitação do usuário
                break;
            }
            System.out.println(msg);
            JSONObject json;
            JSONParser parser = new JSONParser();
            try {
                json = (JSONObject) parser.parse(msg);
                if ((operacao = (String) json.get("operacao")) == null) {
                    operacao = "";
                }
                if ((status = (String) json.get("status")) == null) {
                    status = "";
                }

            } catch (ParseException ex) {
                Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NullPointerException ev) {

            }
            if (operacao.equals("mensagem")) {
                
                textAreaChat.appendText(msg + "\n");
                try {
                    Thread.sleep(300);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, ex);
                }

                // textAreaChat.getStyleClass().add("textArea-textAreaChat-retorno");
                

            } else if (status.equals("200")) {
                System.out.println("Logado com Sucesso!");

            } else if (status.equals("401")) {
                System.out.println("Login ou Senha Incorretos!");

            }

        }

        try {
            clientSocket.closeInOut();//função fecha o Socket
            System.out.println("Desconectado do Servidor");
            btnEnviar.setDisable(true);
            btnDesconectar.setDisable(true);
            btnConectar.setDisable(false);

        } catch (IOException ex) {
            System.out.println("Problemas ao encerrar conexão");
        }

    }

}
