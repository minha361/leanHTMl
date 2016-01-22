package com.ndn.expression.operator;

public class OperatorFactory {
	public static final int AND_PRIORITY = 100;
	public static final int NOT_PRIORITY = 101;
	public static final int EQUAL_PRIORITY = 102;

	public static final String AND = "AND";
	public static final String OR = "OR";
	public static final String NOT = "NOT";
	public static final String EQUAL = "=";
	public static final String CONTAIN = ":";

	public static BaseOperator create(String op) {
		switch (op) {
		case "AND":
			return new ANDOperator();
		case "OR":
			return new OROperator();
		case "NOT":
			return new NOTOperator();
		case "=":
			return new EqualOperator();
		case ":":
			return new ContainOperator();

		default:
			throw new RuntimeException("Do not support this opterator");
		}
	}
}
