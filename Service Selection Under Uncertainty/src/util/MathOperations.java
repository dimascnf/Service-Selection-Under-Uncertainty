package util;

public class MathOperations {
	
	public static double binomial(int n, int k) {
		return factorial(n) / factorial(k)*factorial(n-k);
	}

	private static int factorial(int n) {
		
		int factorial = 1;
		
		for (int i = 2; i<=n; i++) {
			factorial = factorial * i;
		}
		
		return factorial;
	}

}
