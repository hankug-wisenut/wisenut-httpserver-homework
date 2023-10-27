package org.example;

import java.io.InputStream;
import java.util.function.Function;

public interface HttpProtocal {

    public void run(InputStream in, Controller controller);


}
