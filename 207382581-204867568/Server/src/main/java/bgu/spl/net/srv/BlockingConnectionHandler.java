package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.BIDI.BidiMessagingProtocol;
import bgu.spl.net.api.BIDI.Connections;
import bgu.spl.net.api.BIDI.ConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private final Connections<T> serverActiveConnections;


    public BidiMessagingProtocol<T> getProtocol() {
        return protocol;
    }

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol, Connections connections) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        serverActiveConnections = connections;
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            String client = "Client "+Thread.currentThread().getId();
            System.out.println(client+" started reading");
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                System.out.println(client+" has read a line");
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }
            System.out.println(client+" finished reading");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() {
        connected = false;
        try {

            sock.close();
        } catch (IOException e) {
            System.out.println("expection in close in BlockingConnectionHandler "+e.getMessage());
        }
    }

    /**
     * used to send push notification to a client (bypassing writeQueue)
     * going to be used by the BGSProtocol
     * @param msg ack/error
     * @post socket's immediate output stream is msg
     */
    @Override
    public void send(T msg) {
        try {
            out.write(encdec.encode(msg));
            out.flush();
        } catch (IOException e) {
            System.out.println("exception in send in BlockingConnectionHandler "+e.getMessage());
            close();
        }
    }
}


//        try (Socket sock = this.sock) {
//            out = new BufferedOutputStream(sock.getOutputStream());
//            //write the encoded msg to the cliend
//            out.write(encdec.encode(msg));
//            //flush it so the client receives it instantly
//            out.flush();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
