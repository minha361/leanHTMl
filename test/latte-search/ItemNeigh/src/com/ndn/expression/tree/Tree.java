package com.ndn.expression.tree;

import java.util.Map;

import com.ndn.expression.ExpressionTools;

public class Tree {
	public Node root;
	public int id;

	public Tree(Node data, int id) {
		root = data;
		this.id = id;
	}

	public Tree clone() {
		return new Tree(root.clone(), id);
	}

	public ValuedTree assignValue(Map<String, Object> map) {
		return new ValuedTree(ExpressionTools.assignValue(this, map).root);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tree other = (Tree) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
