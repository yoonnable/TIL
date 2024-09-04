package behaviorParameterization;

public class AppleRedAndHeavyPredicate implements ApplePredicate {

	@Override
	public boolean test(Apple a) {
		return "red".equals(a.getColor()) && 150 <= a.getWeight();
	}

}
