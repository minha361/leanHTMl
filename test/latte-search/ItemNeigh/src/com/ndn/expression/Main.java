package com.ndn.expression;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.ndn.expression.tree.Tree;
import com.ndn.expression.tree.ValuedTree;

public class Main {
	public static void main(String[] args) {
		String s = "category_path=323 AND product_item_name:iphone";
		
		Infix2PostfixConverter converter = new Infix2PostfixConverter();
		ExpressionTreeBuilder builder = new ExpressionTreeBuilder();
		
		Tree tree = builder.build(converter.convert(s), 1);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("category_path", Arrays.asList(324, "323", 322, 321, 0));
		map.put("product_item_name", "?i?n tho?i di ??ng Apple iPhone 6 64GB Vàng");
		System.out.println(map);
		ValuedTree appliedTree = tree.assignValue(map);
		System.out.println(appliedTree.result());
	}
}
