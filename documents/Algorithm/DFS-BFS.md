# 그래프 탐색 알고리즘 : BFS와 DFS
[백준 1260](https://www.acmicpc.net/problem/1260)

## 그래프(graph)
* 컴퓨터 고학이나 이산수학에서 사용되는 그래프는 원으로 표현하는 정점(=**노드(node)**) 정점과 정점을 연결하는 **간선(=링크(link))**으로 이루어진 것이다.
* 예시 이미지
    ![alt text](/documents/img/algorithm/dfs-bfs/image.png)

* 사용 예시
    * 역을 정점으로, 인접해 있는 두 역을 간선으로 연결하면 **지하철 노선도**
    * 컴퓨터 네트워크에서 라우터를 정점으로 하고 링크로 연결된 두 개의 라우터를 간선으로 이으면 **네트워크의 접속 관계**를 나타냄

> 그래프 기본 지식
> * 폐로 : 시점과 종점이 같은 경로
>   * 예시 이미지
>       ![alt text](/documents/img/algorithm/dfs-bfs/image-3.png)
> * 트리 : 폐로가 없이 연결된 그래프
>   * 예시 이미지
>    ![alt text](/documents/img/algorithm/dfs-bfs/image-4.png)
### 가중 그래프
* 간선에 가중치를 부여한 그래프
* 간선에 부여된 값 : 간선의 가중치, 코스트(cost)
* 간선에 가중치가 없으면 두 개의 정점은 '연결돼 있거나', '연결돼 있지 않거나' 둘 중 하나이지만, 가중치가 있으면 '연결(관계)의 정도'를 표현할 수 있다.
* '**정도**'가 의미하는 건 그래프마다 다르다.
    * 통신 시간, 이동 시간, 금액 등
* 간선과 마찬가지로 정점에도 가중치를 부여할 수 있다.
* 예시 이미지
    ![alt text](/documents/img/algorithm/dfs-bfs/image-2.png)
### 방향성 그래프
* 가능한 이동방향을 나타내고 싶을 때 간선에 방향을 부여한 그래프(한 방향으로만 이동 가능한 간선이 있을 경우 사용)
* 간선에 방향이 없는 그래프는 '비방향성 그래프'라고 한다.
* 사용 예시
    * 웹페이지 링크 : 웹페이지 링크도 방향성이 있으므로.
* 예시 이미지
    ![alt text](/documents/img/algorithm/dfs-bfs/image-1.png)
## 그래프 탐색 알고리즘
* 특정 정점에서 출발해 간선을 통해 이동해 가며 대상 정점을 찾는 것.

### 너비 우선 탐색 (BFS, Breadth First Search)
* 정점을 탐색할 때 시작점에 가까운 정점들부터 탐색하는 방식
* 먼저 옆으로 탐색하면서 더 이상 남은게 없으면 아래로 이동해서 탐색
* 목표가 시작점에 가까이 있으면 탐색이 빨리 종료 된다.
* Queue 데이터 구조 이용
    * 특정 정점에서 이동 가능한 후보 정점은 '선입선출(FIFO)' 관리하기 때문이다.
    * 시작점에 가까운 정점이 빨리 후보가 됨
    * 예시 코드
        ```java
        public static void BFS(int start) {
            Queue<Integer> queue = new LinkedList<>();
            queue.add(start);
            checked[start] = true;

            while(!queue.isEmpty()) {
            start = queue.poll(); // 선입선출인 queue는 들어간 순서대로 탐색한 것이므로
            System.out.print(start + " "); // 나온 순서대로 출력해주면 됨

            for(int i = 1; i < arr.length; i++) {
                if(arr[start][i] == 1 && !checked[i]) { // 현재 탐색중인 정점에서 인접해있는 정점 모조리 queue에 넣기
                queue.add(i);
                checked[i] = true;
                }
            }
            }
        }
        ```
        * 변수 설명
            * checked[] : 탐색 완료 정점 체크하는 boolean형 배열
            * arr : 정점들의 연결 상태를 담은 배열

### DFS (Depth First Search) : 깊이 우선 탐색
* 정점을 탐색할 때 하나의 길을 끝까지 따라가며 더 이상 진행할 수 없는 곳에 다다르면 다음 후보가 되는 길을 따라간다.
* 먼저 깊이 내려가면서 탐색하다가 더 이상의 깊은 곳이 없으면 옆으로 이동해서 탐색
* Stack 데이터 구조 이용
    * 후보 정점은 '후입선출(LIFO)' 구조로 관리하기 때문이다.
    * 가장 최근에(늦게) 후보가 된 정점이 선택됨
    * 예시 코드
        ```java
        public static void DFS(int start) {
            checked[start] = true; // start는 탐색을 했다!

            System.out.print(start + " "); // 탐색 순서 출력(결과)

            for(int i = 1; i <= N; i++) {
            if(arr[start][i] == 1 && !checked[i]) { // stare와 인접해 있고, 탐색 전이면
                DFS(i); // 탐색하라.
            }
            }
        }
        ```
        * 변수 설명
            * checked[] : 탐색 완료 정점 체크하는 boolean형 배열
            * N : 정점의 개수
            * arr : 정점들의 연결 상태를 담은 배열

## BFS와 DFS의 시간복잡도
* 노드의 개수가 N이고 간선의 개수가 E일때, DFS와 BFS의 시간복잡도는 O(N+E)이다.





---
* 관련 추가 공부
    * 벨먼-포드 알고리즘
    * 다익스트라 알고리즘
    * A*

### Reference
이시다 모리테루, 미야자키 쇼이치, 『알고리즘 도감』, 제이펍, p80~p91
https://devuna.tistory.com/32
https://seonjun0906.tistory.com/entry/%EB%B0%B1%EC%A4%80-1260-DFS%EC%99%80-BFS