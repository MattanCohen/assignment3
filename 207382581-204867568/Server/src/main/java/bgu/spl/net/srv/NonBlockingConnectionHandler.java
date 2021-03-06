package bgu.spl.net.srv;

import bgu.spl.net.api.BIDI.Convertor;
import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.BIDI.BidiMessagingProtocol;
import bgu.spl.net.api.BIDI.ConnectionHandler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NonBlockingConnectionHandler<T> implements ConnectionHandler<T> {

    private static final int BUFFER_ALLOCATION_SIZE = 1 << 13; //8k
    private static final ConcurrentLinkedQueue<ByteBuffer> BUFFER_POOL = new ConcurrentLinkedQueue<>();

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();
    private final SocketChannel chan;
    private final Reactor reactor;

    private LinkedList<Integer> followList;
    private LinkedList<Integer> blockedList;

    public BidiMessagingProtocol<T> getProtocol() {
        return protocol;
    }

    public LinkedList<Integer> getBlockedList() {
        return blockedList;
    }

    public void addToBlockedList(int conId){
        blockedList.add(conId);
    }

    public LinkedList<Integer> getFollowList() {
        return followList;
    }

    public void addToFollowList(int conId){
        followList.add(conId);
    }


    public NonBlockingConnectionHandler(
            MessageEncoderDecoder<T> reader,
            BidiMessagingProtocol<T> protocol,
            SocketChannel chan,
            Reactor reactor) {
        this.chan = chan;
        this.encdec = reader;
        this.protocol = protocol;
        this.reactor = reactor;
    }

    public Runnable continueRead() {
        ByteBuffer buf = leaseBuffer();

        boolean success = false;
        try {
            success = chan.read(buf) != -1;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (success) {
            buf.flip();
            return () -> {
                try {
                    while (buf.hasRemaining()) {
                        T nextMessage = encdec.decodeNextByte(buf.get());
                        if (nextMessage != null) {
                            //since protocol is now bidi protocol
                            protocol.process(nextMessage);
//                            if (response != null) {
//                                writeQueue.add(ByteBuffer.wrap(encdec.encode(response)));
//                                reactor.updateInterestedOps(chan, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
//                            }

                        }
                    }
                } finally {
                    releaseBuffer(buf);
                }
            };
        } else {
            releaseBuffer(buf);
            close();
            return null;
        }

    }

    //

    public void close() {
        try {
            chan.close();
        } catch (IOException ex) {
            System.out.println("exception in close in NoneBlockingConnectionHandler "+ex.getMessage());
        }
    }

    public boolean isClosed() {
        return !chan.isOpen();
    }

    /**
     * @inv when calling continue write, there is a returning command
     *      in writeQueue that needs to be written (to the client's socket)
     * function continueWrite writes a pull notification (a notification that
     * can get pulled by the client at some time) and NOT PUSH NOTIFICATIONS.
     * So, when BGSProtocol wants to send the client push notification such as
     * ERROR or ACK, it will use send(msg) instead of continueWrite
     */
    public void continueWrite() {
        while (!writeQueue.isEmpty()) {
            try {
                ByteBuffer top = writeQueue.peek();
                chan.write(top);
                if (top.hasRemaining()) {
                    return;
                } else {
                    writeQueue.remove();
                }
            } catch (IOException ex) {
                System.out.println("exception in continue write in NoneBlockingConnectionHandler "+ex.getMessage());
                close();
            }
        }

        if (writeQueue.isEmpty()) {
            if (protocol.shouldTerminate()) close();
            else reactor.updateInterestedOps(chan, SelectionKey.OP_READ);
        }
    }

    private static ByteBuffer leaseBuffer() {
        ByteBuffer buff = BUFFER_POOL.poll();
        if (buff == null) {
            return ByteBuffer.allocateDirect(BUFFER_ALLOCATION_SIZE);
        }

        buff.clear();
        return buff;
    }

    private static void releaseBuffer(ByteBuffer buff) {
        BUFFER_POOL.add(buff);
    }


//     /**
//     * used to send push notification to a client (bypassing writeQueue)
//     * going to be used by the BGSProtocol
//     * @param msg ack/error
//     * @post socket's immediate output stream is msg
//     * */
//    @Override
//    public void send(T msg) {
//        writeQueue.add(ByteBuffer.wrap(encdec.encode(msg)));
//        reactor.updateInterestedOps(chan, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
//    }

    /**
     * used to send push notification to a client (bypassing writeQueue)
     * going to be used by the BGSProtocol
     * @param msg ack/error
     * @post socket's immediate output stream is msg
     */
    @Override
    public void send(T msg) {
        try (Socket sock = this.chan.socket()) {
            BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
            System.out.println("send message (string): "+msg);
            byte[] messageInBytes = encdec.encode(msg);
            messageInBytes = Convertor.reverese(messageInBytes);
            System.out.println("send message (bytes): "+ Arrays.toString(messageInBytes));
            System.out.println("------------------------------------------------------");
            out.write(messageInBytes);
            out.flush();
        } catch (IOException e) {
            System.out.println("exception in send in BlockingConnectionHandler "+e.getMessage());
            close();
        }
    }


}



//         hook the socket
//        try (Socket sock = this.chan.socket()) {
//            get socket's output stream
//            BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
//            write the encoded msg to the client
//            out.write(encdec.encode(msg));
//            flush it so the client receives it instantly
//            out.flush();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//    }
