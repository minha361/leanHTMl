package com.adr.bigdata.dataimport.logic.impl.docgen.solrdoc;

import com.nhb.common.Loggable;
import com.nhb.common.data.PuObject;
import com.nhb.common.db.sql.beans.AbstractBean;

public interface PuObjectToBeanConverterInterface extends Loggable {
	public AbstractBean convert(PuObject puObject);
}
