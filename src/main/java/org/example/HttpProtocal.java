package org.example;

import java.io.InputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.function.Function;

public interface HttpProtocal {

    public Request getRequest(Session session);
    public void sendResponse(Session session, Response response);


}
