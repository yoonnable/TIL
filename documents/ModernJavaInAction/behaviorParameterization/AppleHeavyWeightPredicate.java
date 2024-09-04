package behaviorParameterization;

public class AppleHeavyWeightPredicate implements ApplePredicate {

	@Override
	public boolean test(Apple a) {
		return 150 <= a.getWeight();
	}

}
