package runAroundPattern;

import java.io.BufferedReader;
import java.io.IOException;

public class BufferedReaderOneLineReadProcessorImpl implements BufferedReaderProcessor {

    @Override
    public String process(BufferedReader br) throws IOException {
        return br.readLine();
    }
    
}
