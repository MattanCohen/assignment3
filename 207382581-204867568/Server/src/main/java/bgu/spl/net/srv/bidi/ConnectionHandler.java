package bgu.spl.net.srv.bidi;

import java.io.Closeable;
import java.io.IOException;

public interface ConnectionHandler<T> extends Closeable{

    //should be used by send and broadcast in the conncetions implementation. meaning for each message sent
    void send(T msg) ;

}
