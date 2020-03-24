package SelfAdaptation.ppo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketImpl;
import java.net.SocketImplFactory;

public class DingNetConnection {

    private RandomAccessFile in;
    private RandomAccessFile out;

    DingNetConnection() {
        try {

            this.in = new RandomAccessFile("/Users/tobiasbraun/dingnet_in", "r");
            this.out = new RandomAccessFile("/Users/tobiasbraun/dingnet_out", "rw");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public DingNetMessage receiveMessage() {
        DingNetMessage message = null;
        try {
            String line = null;
            while (line == null || line == "" || line == "\n") {
                Thread.sleep(5);
                line = this.in.readLine();
            }
            message = DingNetMessage.fromMessage(line);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return message;
    }

    public void sendMessage(DingNetMessage message) {
        try {
            this.out.write(message.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (this.in != null) {
            this.in.close();
        }
        if (this.out != null) {
            this.out.close();
        }
        super.finalize();
    }
}
