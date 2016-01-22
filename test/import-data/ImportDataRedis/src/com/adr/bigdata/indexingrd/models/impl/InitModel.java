package com.adr.bigdata.indexingrd.models.impl;

import com.adr.bigdata.indexingrd.models.AbstractModel;
import com.mario.consumer.api.MarioApi;

public class InitModel extends AbstractModel {

	public void init(MarioApi api) throws Exception {
		System.out.println("checkpoint 1... brand");
		

		System.out.println("checkpoint 2... category");
		

		System.out.println("checkpoint 3... merchant");
		

		System.out.println("checkpoint 4.............. category attribute filter");
		
		System.out.println("checkpoint 5.............. display unit for (attributeId + value)");
		
		
		System.out.println("checkpoint 6.............. keyword => url");
		
	}
}
