package com.example.golfer;

public class Utilities {
	
	public static double clamp ( double value, double min, double max ) {
		double result = value;
		
		if ( result < min ) {
			result = min;
		}
		if ( result > max ) {
			result = max;
		}
		
		return result;
	}
	public static boolean areEqual(final double value0, final double value1, final double epsilon) {
		return Math.abs(value0 - value1) <= epsilon;
	}

	public static boolean inBetween(final double value, final double min, final double max) {
		return value >= min && value <= max;
	}
}
