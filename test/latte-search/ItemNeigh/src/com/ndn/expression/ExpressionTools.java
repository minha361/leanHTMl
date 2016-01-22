package com.ndn.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ndn.expression.operator.BaseOperator;
import com.ndn.expression.tree.Node;
import com.ndn.expression.tree.Tree;
import com.ndn.expression.tree.ValuedTree;

public class ExpressionTools {
	private static Pattern operatorPattern = Pattern.compile("AND|OR|NOT|=|:");

	public static String format(String expression) {
		return expression.replaceAll("\\s+", " ").replaceAll("AND|OR|NOT|=|:|\\(|\\)", " $0 ").replaceAll("\\s+", " ")
				.trim();
	}

	public static boolean isOperator(String s) {
		return operatorPattern.matcher(s).find();
	}

	public static List<String> splitExclude(String str) {
		int start = 0;
		List<String> toks = new ArrayList<String>();
		boolean withinQuote = false;
		for (int end = 0; end < str.length(); end++) {
			char c = str.charAt(end);
			switch (c) {
			case ' ':
				if (!withinQuote) {
					toks.add(str.substring(start, end));
					start = end + 1;
				}
				break;
			case '\"':
				withinQuote = !withinQuote;
				break;
			}
		}
		if (start < str.length()) {
			toks.add(str.substring(start).replaceAll("\"", ""));
		}
		return toks;
	}

	public static Tree assignValue(Tree expression, Map<String, Object> values) {
		Tree newTree = expression.clone();
		assign(newTree.root, values);
		return newTree;
	}

	private static void assign(Node node, Map<String, Object> values) {
		if (node.data instanceof BaseOperator) {
			if (((BaseOperator) node.data).unary) {
				assign((Node) node.right, values);
			} else {
				assign((Node) node.left, values);
				assign((Node) node.right, values);
			}
		} else {
			String name = (String) node.data;
			if (values.containsKey(name)) {
				node.data = values.get(name);
				if (node.data instanceof String) {
					node.data = ((String) node.data).replaceAll("\\s+", " ").trim();
				}
			}
		}
	}

	public static boolean getResult(ValuedTree expression) {
		return getResult(expression.root);
	}

	private static boolean getResult(Node node) {
		if (node.data instanceof BaseOperator) {
			if (((BaseOperator) node.data).unary) {
				if (node.right.data instanceof BaseOperator) {
					boolean res = getResult(node.right);
					return ((BaseOperator) node.data).impact(res);
				} else {
					return ((BaseOperator) node.data).impact(node.right);
				}
			} else {
				if (node.left.data instanceof BaseOperator && node.right.data instanceof BaseOperator) {
					boolean res1 = getResult(node.left);
					boolean res2 = getResult(node.right);
					return ((BaseOperator) node.data).impact(res1, res2);
				} else {
					return ((BaseOperator) node.data).impact(node.left.data, node.right.data);
				}
			}
		} else {
			throw new RuntimeException("Iu biet tai sao");
		}
	}

	public static void main(String[] args) {
		System.out.println(splitExclude("a:\"a b c\""));
	}
}
