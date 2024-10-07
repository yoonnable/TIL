# Sieve of Eratosthenes 에라토스테네스의 체
* 이 알고리즘은 소수가 아닌 수를 제거해나가는 방식으로 작동한다.
* 특정 수의 배수를 모두 제거함으로써, 해당 수가 소수가 아닌 것을 판별한다.
* 𝑁 이 소수인지 확인하려면, 그 수의 약수는 항상 제곱근 이하에 있기 때문에, 𝑁의 제곱근까지만 확인하면 된다. *더 큰 수로 나누어떨어지는 경우는 무조건 이미 이전에 제거가 되어있다.*
* 예시
    * 소수를 찾기 위해 16까지 탐색한다고 가정.
    1. 2는 소수이다. 따라서 2의 배수들을 모두 제거함 (4, 6, 8, 10, 12, 14, 16).
    2. 그 다음 3을 확인한다. 3은 소수이다. 따라서 3의 배수들을 제거함 (9, 12, 15).
    3. 그 다음 4는 이미 제거된 수(2의 배수로, 1번 과정에서)이므로 건너뛴다.
    4. 여기서 중요한 점은 5를 기준으로 5보다 작은 수의 배수들이 이미 모두 제거되었고, **제곱근을 기준으로 더 큰 수에서 소수 판별을 할 필요가 없다**는 점이다.

> 따라서, 소수 판별을 할 때, 그 수의 제곱근보다 큰 수를 고려할 필요가 없다.

## 에라토스테네스의 체 알고리즘의 시간복잡도 : O(N log log N)
* prime.length 보다 작은 수들 중 소수를 구하는 코드이다.
* 값이 false인 인덱스가 소수가 된다.
```Java
public static void getPrime(boolean[] prime) {
    prime[0] = prime[1] = true; // index인 0,1은 소수 아니니까 true로 변경


    // Math.sqrt(a) : a의 제곱근을 double형으로 리턴한다. ex) Math.sqrt(16) = 4.0
    for(int i = 2; i <= Math.sqrt(prime.length); i++) {

        if(prime[i]) continue;

        for(int j = i * i; j < prime.length; j += i) {
            prime[j] = true;
        }
    }
}
```
### 에라토스테네스의 체 알고리즘의 시간 복잡도 분석
1. **외부 for문**

    ```java
    for(int i = 2; i <= Math.sqrt(prime.length); i++) {
    ```
    * 이 루프는 대략 $\sqrt{𝑁}$ 번 반복된다. 그러나 이 반복 자체는 전체 시간 복잡도에 큰 영향을 미치지 않는다. 왜냐하면 <u>내부에서 수행되는 작업</u>들이 주요한 시간 복잡도를 결정하기 때문이다.

2. **내부 for문**
    ```java
    for(int j = i * i; j < prime.length; j += i) {
        prime[j] = true;
    }
    ```
    * 이 루프에서 중요한 점은, **𝑖가 커질수록**, 즉 작은 소수부터 처리함에 따라 **𝑖의 배수들을 true로 설정하는 작업의 빈도가 줄어든다는 것**이다. 각 소수 𝑖에 대해 𝑁/𝑖번의 연산이 발생하게 된다는 것을 알 수 있다.

    * 소수에 해당하는 𝑖가 증가할수록, 처리해야 하는 배수들이 점점 줄어들게 되므로, 실제로 모든 소수 𝑖에 대해 수행되는 연산의 총합은 𝑁 log log 𝑁에 수렴하게 된다.

### 직관적 분석
* 작은 소수일수록 많은 배수들을 처리해야 하고, 큰 소수일수록 처리할 배수들이 적다.
* 이러한 연산을 합산하면, 총 연산 횟수는 **O(N log log N)**이 된다.

### 시간 복잡도 정리
* 외부 루프에서 $\sqrt{𝑁}$까지만 탐색하지만, 이 부분은 전체 시간 복잡도에 큰 영향을 미치지 않음.
* 내부 루프에서 각 소수에 대해 약 
𝑁/𝑖번의 연산이 수행되며, 이를 모두 합하면 𝑂(𝑁𝑙𝑜𝑔𝑙𝑜𝑔𝑁)이 된다.