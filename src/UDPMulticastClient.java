import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.Time;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.time.temporal.ChronoUnit.SECONDS;

public class UDPMulticastClient implements Runnable {
    HashMap<String, LocalTime> table = new HashMap<>();
    public static void StartClient(){
        Thread t = new Thread(new UDPMulticastClient());
        t.start();
    }
    public void UpdateTable(String msg){ //timeout = n seconds
        if (table.containsKey(msg)) table.replace(msg, LocalTime.now());
        else table.put(msg, LocalTime.now());
        Iterator it = table.entrySet().iterator();
        while(it.hasNext()){
            System.out.println("1");
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey());
            if (SECONDS.between((LocalTime)pair.getValue(), LocalTime.now()) >= 30) { //timeout
                System.out.println(pair.getKey() + "  " + pair.getValue() + "   " + LocalTime.now());
                //table.remove(pair.getKey());
                it.remove();
            }
        }
    }
    public void receiveUDPMessage(String ip, int port) throws IOException {
        byte[] buffer = new byte[1024];
        MulticastSocket socket = new MulticastSocket(4321);
        InetAddress group = InetAddress.getByName("230.0.0.0");
        socket.joinGroup(group);
        while (true) {
            System.out.println("Waiting for multicast message...");
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            System.out.println(packet.getSocketAddress().toString().split(":")[0]);
            String msg = new String(packet.getData(), packet.getOffset(), packet.getLength());
            System.out.println("[Multicast UDP message received] >> " + msg);
            UpdateTable(packet.getSocketAddress().toString().split(":")[0]);
            System.out.println("copies launched : " + table.size());
        }
        //socket.leaveGroup(group);
        //socket.close();
    }

    @Override
    public void run() {
        try {
            receiveUDPMessage("230.0.0.0", 4321);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
//works when first finishes last