package com.ndn.expression;

import java.util.Queue;
import java.util.Stack;

import com.ndn.expression.operator.BaseOperator;
import com.ndn.expression.operator.ContainOperator;
import com.ndn.expression.operator.EqualOperator;
import com.ndn.expression.tree.Node;
import com.ndn.expression.tree.Tree;

public class ExpressionTreeBuilder {
	public Tree build(Queue<Object> postfix, int id) {
		Stack<Object> stack = new Stack<Object>();

		while (!postfix.isEmpty()) {
			Object o = postfix.poll();

			if (!(o instanceof BaseOperator)) {
				stack.push(o);
			} else {
				BaseOperator op = (BaseOperator) o;
				Node node = new Node(op);
				if (stack.peek() instanceof Node) {
					node.right = (Node) stack.pop();
				} else {
					node.right = new Node(stack.pop());
				}
				if (!op.unary) {
					if (stack.peek() instanceof Node) {
						node.left = (Node) stack.pop();
					} else {
						node.left = new Node(stack.pop());
					}

					if (op instanceof EqualOperator || op instanceof ContainOperator) {
						if ((node.left.data instanceof BaseOperator) || (node.right.data instanceof BaseOperator)) {
							throw new RuntimeException("Sai cu phap");
						}
					}
				}

				stack.push(node);
			}
		}

		return new Tree((Node) stack.pop(), id);
	}
}
