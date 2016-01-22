package com.ndn.expression.tree;

import com.ndn.expression.ExpressionTools;

public class ValuedTree {
	public Node root;

	public ValuedTree(Node data) {
		root = data;
	}

	public ValuedTree clone() {
		return new ValuedTree(root.clone());
	}
	
	public boolean result() {
		return ExpressionTools.getResult(this);
	}
}
