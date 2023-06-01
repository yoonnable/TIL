package runAroundPattern;

import java.io.*;

public class FileProcessor {
    public String processfile(BufferedReaderProcessor brp) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader("runAroundPattern/data.txt"))) { //설정과 정리 과정
            return brp.process(br); //실제 작업 처리 코드
        }
    }
}
