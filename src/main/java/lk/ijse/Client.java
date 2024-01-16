package lk.ijse;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;
    private Socket socket;

    public Client(Socket socket , String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush(); // clientUsername has entered the chat

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine(); //input field
                //System.out.print("me : ");
                bufferedWriter.write(username + " : " + messageToSend); //client send message
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void listenMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFormGroupChat;

                while (socket.isConnected()){
                    try{
                        messageFormGroupChat = bufferedReader.readLine();
                        System.out.println(messageFormGroupChat);
                    }catch (IOException e){
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }

            if (bufferedWriter != null){
                bufferedWriter.close();
            }

            if (socket != null){
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username for the group chat : ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost" , 1234);
        Client client = new Client(socket,username);
        client.listenMessage();
        client.sendMessage();
    }
}
