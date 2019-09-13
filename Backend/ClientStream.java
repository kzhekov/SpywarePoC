package Backend;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class ClientStream {
    private AudioFormat format;
    private String serverName;
    private int port;
    private boolean inVoice;

    private ClientStream() {
        format = SpywareServer.getAudioFormat();

        Scanner reader = new Scanner(System.in);  // Reading from System.in
        System.out.println("Enter an address: ");
        serverName = reader.nextLine();
        reader.close();
        port = 993;
        inVoice = true;
        runListener();
    }

    public static void main(String[] args) {
        new ClientStream();
    }

    private void runListener() {
        try {
            System.out.println("Connecting to server:" + serverName + " Port:" + port);
            Socket clientSocket = new Socket(InetAddress.getByName(serverName), port);
            System.out.println("Connected! Server Address: " + clientSocket.getRemoteSocketAddress());
            DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(speakerInfo);
            speaker.open(format);
            speaker.start();
            while (inVoice) {
                InputStream is = clientSocket.getInputStream();
                byte[] data = new byte[1024];
                is.read(data);
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
                AudioInputStream ais = new AudioInputStream(inputStream, format, data.length);
                int bytesRead = ais.read(data);
                if (bytesRead != -1) {
                    speaker.write(data, 0, bytesRead);
                }
                ais.close();
                inputStream.close();
            }
            System.out.println("Stopped listening to incoming audio.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

