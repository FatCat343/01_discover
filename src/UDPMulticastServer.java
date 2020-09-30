import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static java.lang.Thread.sleep;

public class UDPMulticastServer {

    public static void sendUDPMessage(String message, String ipAddress, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress group = InetAddress.getByName(ipAddress);
        byte[] msg = {1};
        DatagramPacket packet = new DatagramPacket(msg, 1 , group, port);
        socket.send(packet);
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        UDPMulticastClient.StartClient();
        while (true) {
            sendUDPMessage("This is a multicast messge", "230.0.0.0", 4321);
            try {
                sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
