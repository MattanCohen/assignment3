package bgu.spl.net.srv;




import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.Tools;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private final Connections<T> serverActiveConnections;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory,
            Connections<T> connections) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.serverActiveConnections =connections;
        this.sock = null;
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {

			System.out.println("TPC Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();

                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocolFactory.get(),
                        serverActiveConnections);
                try{

                    //add handler to connections:
                    //increment and get conId for new handler
                    int conId= Tools.incrementAndGetConId();

                    //add new handler to handlers
//        handlers.put(conId,handler);

                    //give handler's protocol connections and conId
                    handler.getProtocol().start(conId, (Connections<T>) serverActiveConnections);
                    handler.getProtocol().addHandler(handler);
                }
                finally {
                    execute(handler);
                }
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }


    /*

        final NonBlockingConnectionHandler<T> handler = new NonBlockingConnectionHandler<T>(
                readerFactory.get(),
                (BidiMessagingProtocol)protocolFactory.get(),
                clientChan,
                this);

        //add handler to connections:
        //increment and get conId for new handler
        int conId= Tools.incrementAndGetConId();

        //add new handler to handlers
//        handlers.put(conId,handler);

        //give handler's protocol connections and conId
        handler.getProtocol().start(conId, (Connections<T>) serverActiveConnections);
        handler.getProtocol().addHandler(handler);

        clientChan.register(selector, SelectionKey.OP_READ, handler);


        //                      IMPLEMENT!!!!!!!!!!!!!!!!!!!!!!!!!
//        handler.send();
    }
     */

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}
