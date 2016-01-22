package com.ndn.expression.operator;

import static com.ndn.expression.operator.OperatorFactory.EQUAL;
import static com.ndn.expression.operator.OperatorFactory.EQUAL_PRIORITY;

import java.util.List;

public class EqualOperator extends BaseOperator {
	public EqualOperator() {
		this.op = EQUAL;
		this.priority = EQUAL_PRIORITY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean impact(Object... args) {
		baseVerify(args);
		if (args[0] instanceof List) {
			return ((List<Object>) args[0]).contains(args[1]);
		}
		return args[0].equals(args[1]) || args[0].toString().equals(args[1].toString());
	}

}
