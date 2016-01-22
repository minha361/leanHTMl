package com.adr.bigdata.search.product.fe.logic;

import com.adr.bigdata.search.model.ModelFactory;

public abstract class ModeledLogic extends AbstractLogic {
	protected ModelFactory modelFactory;

	public ModeledLogic(ModelFactory modelFactory) {
		super();
		this.modelFactory = modelFactory;
	}

}
