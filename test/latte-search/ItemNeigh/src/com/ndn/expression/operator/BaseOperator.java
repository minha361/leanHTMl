package com.ndn.expression.operator;

public abstract class BaseOperator {

	public String op;
	public int priority;
	public boolean unary = false;

	public abstract boolean impact(Object... args);

	private boolean verify(Object... args) {
		if (unary) {
			return args.length == 1;
		} else {
			return args.length == 2;
		}
	}

	protected void baseVerify(Object... args) {
		if (!verify(args)) {
			throw new IllegalArgumentException("args: " + args + " unary: " + unary);
		}
	}
	
	@Override
	public String toString() {
		return this.op;
	}
}
