package com.ndn.expression;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import com.ndn.expression.operator.BaseOperator;
import com.ndn.expression.operator.OperatorFactory;

public class Infix2PostfixConverter {
	public Queue<Object> convert(String infix) {
		infix = ExpressionTools.format(infix);
		List<String> es = ExpressionTools.splitExclude(infix);
		Stack<Object> stack = new Stack<Object>();
		Queue<Object> queue = new ArrayDeque<Object>();

		for (int i = 0; i < es.size(); i++) {
			String cur = es.get(i);
			if (ExpressionTools.isOperator(cur)) {
				BaseOperator curOp = OperatorFactory.create(cur);
				if (!stack.empty()) {
					Object top = stack.peek();
					if (top instanceof BaseOperator) {
						if (((BaseOperator) top).priority >= curOp.priority) {
							queue.add(stack.pop());
						}
					}
				}

				stack.push(curOp);
			} else if (cur.equals("(")) {
				stack.push(cur);
			} else if (cur.equals(")")) {
				while (true) {
					Object top = stack.pop();
					if (top instanceof String && ((String) top).equals("(")) {
						break;
					}

					queue.add(top);
				}
			} else {
				queue.add(cur);
			}
		}
		while (!stack.empty()) {
			if (stack.peek().toString().equals("(") || stack.peek().toString().equals(")")) {
				throw new RuntimeException("Sai cu phap");
			}
			queue.add(stack.pop());
		}

		return queue;
	}

	public static void main(String[] args) {
		Infix2PostfixConverter converter = new Infix2PostfixConverter();
		System.out.println(converter.convert("A AND (B OR C)"));
	}
}
