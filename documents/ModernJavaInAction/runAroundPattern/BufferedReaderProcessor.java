package runAroundPattern;

import java.io.BufferedReader;
import java.io.IOException;

@FunctionalInterface //함수형 인터페이스 생성
public interface BufferedReaderProcessor {
    String process(BufferedReader br) throws IOException; // 함수형 인터페이스 조건인 단 하나의 추상 메서드 정의
    
    // 참고만 하라구~
    default void defaultMethod() {
        System.out.println("함수형 인터페이스에서 디폴트 메서드는 있어도 되고 없어도 되고 여러개 있어도 상관 없음~!");
        System.out.println("접근제어자 default와 혼동 하면 안됨!!!!!");
        System.out.println("디폴트 메서드의 의미의 default를 명시하면 인터페이스 내부에서도 코드작성 가능~!");
    }
}