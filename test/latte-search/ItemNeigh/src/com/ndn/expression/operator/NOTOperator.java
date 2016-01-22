package com.ndn.expression.operator;

import static com.ndn.expression.operator.OperatorFactory.NOT;
import static com.ndn.expression.operator.OperatorFactory.NOT_PRIORITY;

public class NOTOperator extends BaseOperator {
	public NOTOperator() {
		this.op = NOT;
		this.priority = NOT_PRIORITY;
		this.unary = true;
	}

	@Override
	public boolean impact(Object... args) {
		baseVerify(args);

		if (args[0] instanceof Boolean) {
			return !((Boolean) args[0]);
		} else {
			throw new IllegalArgumentException("args[0]: " + args[0] + " - args[1]" + args[1]);
		}
	}

}
