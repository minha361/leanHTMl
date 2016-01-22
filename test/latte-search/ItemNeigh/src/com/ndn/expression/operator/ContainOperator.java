package com.ndn.expression.operator;

import static com.ndn.expression.operator.OperatorFactory.CONTAIN;
import static com.ndn.expression.operator.OperatorFactory.EQUAL_PRIORITY;

public class ContainOperator extends BaseOperator {
	public ContainOperator() {
		this.op = CONTAIN;
		this.priority = EQUAL_PRIORITY;
	}

	@Override
	public boolean impact(Object... args) {
		baseVerify(args);

		if (args[0] instanceof String) {
			return ((String) args[0]).toLowerCase().contains(args[1].toString().toLowerCase());
		} else {
			throw new IllegalArgumentException("args[0]: " + args[0] + " - args[1]" + args[1]);
		}
	}

}
