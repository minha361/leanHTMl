package com.ndn.expression.tree;

public class Node {
	public Object data;
	public Node left;
	public Node right;

	public Node(Object data) {
		this.data = data;
	}

	public Node clone() {
		Node node = new Node(data);
		if (left != null) {
			if (left instanceof Node) {
				node.left = ((Node) left).clone();
			} else {
				node.left = new Node(new String(left.toString()));
			}
		}

		if (right != null) {
			if (right instanceof Node) {
				node.right = ((Node) right).clone();
			} else {
				node.right = new Node(new String(right.toString()));
			}
		}

		return node;
	}
}
