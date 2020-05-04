import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String hostname;
    private int port;
    private boolean isConnected = true;

    private Socket socket;


    public static void main(String[] args) {
        Client client = new Client("localhost", 500);
        client.connect();
    }

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void connect() {
        System.out.println("Connecting to server: " + this.hostname + " on port " + this.port);

        Scanner scanner = new Scanner(System.in);

        try {
            this.socket = new Socket(this.hostname, this.port);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());


            System.out.print("Enter a nickname: ");
            String nickName = scanner.nextLine();
            out.writeUTF(nickName);

            System.out.println("You are now connected as " + nickName);

            String input = "";

            //boolean isRunning = true;

            Thread readSocketThread = new Thread( () -> {
                receiveDataFromSocket(in);
            });

            readSocketThread.start();

            while (!input.equals("\\quit")) {
                //System.out.print("(" +nickName + "): ");
                input = scanner.nextLine();
                out.writeUTF(input);
                //System.out.print("Sended: " + input);
            }

            this.isConnected = false;

            try {
                System.out.println("You are now disconnected");
                readSocketThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void receiveDataFromSocket(DataInputStream in) {
        String received = "";
        while (this.isConnected) {
            try {
                received = in.readUTF();
                System.out.println(received);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void writeStringToSocket(Socket socket, String text) {
        try {
            socket.getOutputStream().write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
