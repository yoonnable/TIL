package behaviorParameterization;

public class AppleGreenColorPredicate implements ApplePredicate {

	@Override
	public boolean test(Apple a) {
		return "green".equals(a.getColor());
	}

}
