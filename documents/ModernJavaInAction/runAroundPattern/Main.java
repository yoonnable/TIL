package runAroundPattern;

import java.io.BufferedReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        FileProcessor fp = new FileProcessor();

        /*
         * 람다 사용하여 파라미터 넘기기
         * 1 줄이면 끝!!
         */
        System.out.println("1. 람다 사용하여 파라미터 넘기기");
        System.out.println(fp.processfile((BufferedReader br) -> br.readLine()));//1줄읽기
        System.out.println(fp.processfile((BufferedReader br) -> br.readLine() + br.readLine()));//2줄 읽기
        System.out.println("*************************************************************");
        
        /*
         * 위 람다 사용한 것과 같은 기능으로
         * 인터페이스를 상속받은 클래스에 구현체를 만들어 파라미터 넘기기
         *  
         *  - 람다 사용하는 것 보다 코드 줄이 길고,
         *  - 매번 다른 동작을 시킬 때마다 구현체 class를 생성해야 하는 번거로움이 있다.
         */
        System.out.println("2. 인터페이스를 상속받은 클래스에 구현체를 만들어 파라미터 넘기기");
        BufferedReaderProcessor oneLineRead = new BufferedReaderOneLineReadProcessorImpl();
        BufferedReaderProcessor twoLineRead = new BufferedReaderTwoLineReadProcessorImpl();
        System.out.println(fp.processfile(oneLineRead)); //1줄 읽기
        System.out.println(fp.processfile(twoLineRead)); //2줄 읽기
        System.out.println("*************************************************************");

        /*
         * 위 구현체 class를 직접 하나하나 생성해서 변수에 할당하고 넘겨주는 것을
         * 람다로 만들어 변수에 할당하여 그나마(?) 좀 더 간편하게 하기
         * <- 이걸로 '람다 사용하여 파라미터 넘기기'가 더 직관적으로 이해 가는 듯..
         */
        System.out.println("3. 1과 2의 중간 단계(?)");
        BufferedReaderProcessor oneLineReadLambda = (BufferedReader br) -> br.readLine();
        BufferedReaderProcessor twoLineReadLambda = (BufferedReader br) -> br.readLine() + br.readLine();
        System.out.println(fp.processfile(oneLineReadLambda)); //1줄 읽기
        System.out.println(fp.processfile(twoLineReadLambda)); //2줄 읽기
        System.out.println("*************************************************************");


        /*
         * 아! 그러면 람다식은 함수형 인터페이스의 추상메서드의 구현부를 만듦과 동시에
         * 그 구현부가 정의된, 오버라이드 된 메서드를 갖고있는, 즉 함수형 인터페이스를 상속받은
         * 클래스를 함수형 인터페이스 형으로 만드는 거네~~~
         *
         * 
         * 
         * 근데,, 추가로 2번 보면 생성자를 바로 인수로 넣어도 되긴 하네
         * 메인 코드는 줄어들지 몰라도
         * 어쨌든 결국 class는 만들어야 하니께
         * 모든 작업을 통틀었을땐 람다를 사용해 파라미터로 넘기는게 간결함에 있어선 제일 최선일듯!!
         */
        System.out.println("4. 인터페이스를 상속받은 클래스에 구현체 생성자를 바로 파라미터 넘기기");
        System.out.println(fp.processfile(new BufferedReaderOneLineReadProcessorImpl())); //1줄 읽기
        System.out.println(fp.processfile(new BufferedReaderTwoLineReadProcessorImpl())); //2줄 읽기
        System.out.println("*************************************************************");


    }
}
