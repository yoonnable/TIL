package lambdaExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Main m = new Main();
		//사과 등록
		List<Apple> apples = m.initApples();
		System.out.println("<전체 등록된 사과 리스트>");
		System.out.println(apples);
		System.out.println("등록 누적 사과 갯수 : " + apples.size() + "개(확인 완료 시 엔터)");
		
		while(true) {
			//필터 조건 선택
			ApplePredicate p = m.selectFilter();
			if(p == null) {
				System.out.println("기능이 종료되었습니다. 이용해 주셔서 감사합니다.");
				break;
			}
			//결과 출력
			System.out.println(filterApples(apples, p) + "(확인 완료 시 엔터)");
		}
		
	}
	
	public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
		List<Apple> result = new ArrayList<Apple>();
		for(Apple a : inventory) {
			if(p.test(a)) {
				result.add(a);
			}
		}
		return result;
	}
	
	
	public ApplePredicate selectFilter() {
		Scanner sc = new Scanner(System.in);
		sc.nextLine();
		List<String> filters = Arrays.asList("초록색 사과", "150g 이상인 사과", "150g 이상인 빨간색 사과");
		ApplePredicate p = null;
		
		System.out.println("사과를 필터링 할 기준을 선택하세요.(종료: 보기에 없는 숫자 입력)");
		for(String f : filters) {
			System.out.println(filters.indexOf(f) + ". " + f);
		}
		System.out.print("번호 입력 : ");
		int no = sc.nextInt();
		for(String f : filters) {
			if(filters.indexOf(f) == no) {
				switch(f) {
					case "초록색 사과" : p = (Apple a) -> "green".equals(a.getColor()); break;
					case "150g 이상인 사과" : p = (Apple a) -> 150 <= a.getWeight(); break;
					case "150g 이상인 빨간색 사과" : p = (Apple a) -> "red".equals(a.getColor()) && 150 <= a.getWeight(); break;
				}
				System.out.println("<등록된 사과 중 " + f + ">");
			}
		}
		return p;
	}
	
	public List<Apple> initApples() {
		List<Apple> newApples = new ArrayList<Apple>();
		System.out.println("수확한 사과를 등록해주세요.");
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.print("수확한 사과 번호(없으면 0번을 입력해주세요.)*중복불가 : ");
			int no = sc.nextInt();
			for(int i = 0; newApples.size() > i; i++) {
				if(newApples.get(i).getNo() == no) {
					System.out.print("중복된 번호 입니다. 다시 입력해주세요.(없으면 0번을 입력해주세요.)*중복불가 : ");
					i = -1;
					no = sc.nextInt();
				}
			}
			if(no == 0) {
				break;
			}
			sc.nextLine();
			System.out.print("수확한 사과 색상 : ");
			String color = sc.nextLine();
			System.out.print("수확한 사과 무게(g) : ");
			int weight = sc.nextInt();
			Apple a = new Apple(no, color, weight);
			newApples.add(a);
			System.out.println("등록 완료!");
			
		}
		return newApples;
	}

}