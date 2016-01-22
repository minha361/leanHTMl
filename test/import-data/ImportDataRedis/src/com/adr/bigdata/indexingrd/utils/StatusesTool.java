package com.adr.bigdata.indexingrd.utils;

import java.util.ArrayList;
import java.util.List;

public class StatusesTool {
	public static List<Integer> getListBits(int n) {
		List<Integer> result = new ArrayList<Integer>();
		int i = 0;
		int a = 1 << i;
		while (a <= n) {
			if ((n & a) == a) {
				result.add(a);
			}
			i++;
			a = 1 << i;
		}
		return result;
	}
}
