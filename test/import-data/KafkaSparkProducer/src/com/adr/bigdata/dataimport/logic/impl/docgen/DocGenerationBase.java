package com.adr.bigdata.dataimport.logic.impl.docgen;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import net.minidev.json.parser.ParseException;

import org.apache.solr.common.SolrInputDocument;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import com.adr.bigdata.dataimport.system.ExternalSources;
import scala.Tuple2;

import com.adr.bigdata.indexing.db.sql.models.CachedModel;
import com.nhb.common.data.PuObject;

public class DocGenerationBase extends CachedModel implements Serializable,
		DocGenerationJob {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private byte[] byteArray = null;

	private PuObject puObject = null;
	private boolean debugOption;
	private boolean checkingUpdateTime = true;

	public PuObject getPuObject() {
		return puObject;
	}

	public void setPuObject(PuObject puObject) {
		this.puObject = puObject;
	}

	public byte[] getByteArray() {
		return byteArray;
	}

	@Override
	public JavaRDD<SolrInputDocument> genDocuments(JavaSparkContext conf)
			throws ParseException, Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setByteArray(byte[] ba) {
		byteArray = ba;
	}

	@Override
	public void byteArrayToPuObject() throws IOException {
		if (byteArray != null) {
			puObject = new PuObject().fromMessagePack(byteArray);
			if (puObject == null) {
				getLogger().error("Error parsing from message pack ...");
				System.out.println("Error parsing from message pack ...");
			}
		}
	}

	@Override
	public JavaRDD<SolrInputDocument> genDocuments(
			JavaRDD<Tuple2<String, String>> msg) {
		JavaRDD<List<SolrInputDocument>> docs = msg
				.map(new Function<Tuple2<String, String>, List<SolrInputDocument>>() {

					private static final long serialVersionUID = 1L;

					@Override
					public List<SolrInputDocument> call(
							Tuple2<String, String> arg0) throws Exception {
						// TODO Auto-generated method stub
						return null;
					}

				});
		return null;
	}

	@Override
	public void setExternalSources(final ExternalSources es) {
		this.setCacheWrapper(es.getCacheWrapper());
		this.setDbAdapter(es.getDbiAdapter());
	}

	public boolean isDebugOption() {
		return this.debugOption;
	}

	public void setDebugOption(final boolean debugOption) {
		this.debugOption = debugOption;
	}

	@Override
	public void setDebug(boolean option) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCheckingUpdateTime(boolean checking) {
		// TODO Auto-generated method stub
		this.checkingUpdateTime = checking;
	}

	public boolean getCheckingUpdateTime() {
		return this.checkingUpdateTime;
	}
}
