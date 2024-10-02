# BufferedReader로 여러 줄 입력 받기

```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BufferedReaderMultiInput {

  public static void main(String[] args) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String number;
    while (!(number = br.readLine()).equals("0")) {
      System.out.println(number);
    }
  }

}
```