package com.adr.bigdata.search.sim;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;

public class VinSimilarity extends DefaultSimilarity {
	@Override
	public float lengthNorm(FieldInvertState arg0) {
		return 1.0f;
	}

	@Override
	public float tf(float freq) {
		return (float) Math.log1p(freq);
	}
}
