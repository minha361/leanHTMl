package com.adr.bigdata.search.product.function;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.ValueSource;
import org.apache.lucene.queries.function.docvalues.StrDocValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;

import com.adr.bigdata.indexing.db.sql.beans.AttributeValueMeasureUnitDisplayBean;
import com.adr.bigdata.search.model.ModelFactory;
import com.adr.bigdata.search.product.ProductFields;
import com.adr.bigdata.search.product.model.AttributeModel;
import com.nhb.common.Loggable;

import net.minidev.json.JSONValue;

public class VinAttributeQuery extends ValueSourceParser implements Loggable {
	public static final String ATT_TEMPLATE = "attr_.*_i";

	private AttributeModel attModel;

	@Override
	public ValueSource parse(FunctionQParser parser) throws SyntaxError {
		if (attModel == null) {
			ModelFactory mf = ModelFactory.getInstance(parser.getReq().getCore().getName());
			try {
				attModel = mf.getModel(AttributeModel.class, parser.getReq().getCore());
			} catch (Exception e) {
				getLogger().error("", e);
				return null;
			}
		}

		return new FieldCacheSource(ProductFields.PRODUCT_ID) {

			@SuppressWarnings("rawtypes")
			@Override
			public FunctionValues getValues(Map map, LeafReaderContext readerContext) throws IOException {
				Fields fields = readerContext.reader().fields();
				Map<String, NumericDocValues> atts = new HashMap<>();
				Map<String, BinaryDocValues> attNames = new HashMap<>();
				Map<String, Bits> attsExist = new HashMap<>();
				Map<String, Bits> attNamesExist = new HashMap<>();

				for (String field : fields) {
					if (field.matches(ATT_TEMPLATE)) {
						NumericDocValues attValueId = DocValues.getNumeric(readerContext.reader(), field);
						atts.put(field, attValueId);
						Bits attExist = DocValues.getDocsWithField(readerContext.reader(), field);
						attsExist.put(field, attExist);
						String attNameField = "attr_name_" + idAtt(field) + "_txt";
						BinaryDocValues attName = DocValues.getBinary(readerContext.reader(), attNameField);
						attNames.put(field, attName);
						Bits attNameExist = DocValues.getDocsWithField(readerContext.reader(), attNameField);
						attNamesExist.put(field, attNameExist);
					}
				}

				return new StrDocValues(this) {
					@Override
					public String strVal(int doc) {
						List<AttVO> attVOs = new ArrayList<>();
						Set<String> keyHazel = new HashSet<>();
						for (Entry<String, NumericDocValues> e : atts.entrySet()) {
							int attValueId = (int) e.getValue().get(doc);
							boolean attExist = attsExist.get(e.getKey()).get(doc);
							boolean attNameExist = false;
							if (attNamesExist.containsKey(e.getKey())) {
								attNameExist = attNamesExist.get(e.getKey()).get(doc);
							}
							if ((attValueId != 0 || attExist) && attNameExist && attNames.get(e.getKey()) != null) {
								int attId = idAtt(e.getKey());
								keyHazel.add(attId + "_" + attValueId);
								BytesRef name = attNames.get(e.getKey()).get(doc);
								String attName = (name != null) ? name.utf8ToString(): "nu";
								AttVO vo = new AttVO(attName, attId, attValueId);
								attVOs.add(vo);
							}
						}
						//get from hazelcast
						getLogger().debug("keyHazel: {}", keyHazel);
						Map<String, AttributeValueMeasureUnitDisplayBean> beans = attModel.getAttValues(keyHazel);
						getLogger().debug("result beans: {}", beans);
						if (beans != null) {
							for (AttVO vo : attVOs) {
								AttributeValueMeasureUnitDisplayBean bean = beans.get(vo.attId + "_" + vo.attValueId);
								if (bean != null) {
									vo.value = bean.getValue();
									vo.displayValue = transform(bean);
									vo.ratio = bean.getDisplayRatio();
									vo.displayUnit = bean.getDisplayUnitName();
								}
							}
						}

						return JSONValue.toJSONString(attVOs);
					}
				};
			}
		};
	}

	private static int idAtt(String field) {
		String tmp = field.substring(5);
		return Integer.parseInt(tmp.substring(0, tmp.length() - 2));
	}

	public class AttVO {
		public AttVO(String attName, int attId, int attValueId) {
			super();
			this.attName = attName;
			this.attId = attId;
			this.attValueId = attValueId;
		}

		public String displayValue;
		public String attName;
		public int attId;
		public int attValueId;
		public String value;
		public String displayUnit;
		public double ratio;
	}
	
	private static String transform(AttributeValueMeasureUnitDisplayBean displayunit) {
		try {
			Double doubleVal = Double.parseDouble(displayunit.getValue());
			double ratio = displayunit.getDisplayRatio();
			String unit = displayunit.getDisplayUnitName();
			int accuracy = 3;
			double displayVal = doubleVal;
			if (ratio > 0) {
				displayVal = doubleVal / ratio;
			}
			String strAccuracy = StringUtils.repeat("#", accuracy);
			DecimalFormat format = new DecimalFormat("0." + strAccuracy);
			String result = format.format(displayVal);
			return (result + " " + unit.trim());
		} catch (Exception e) {
//			e.printStackTrace();
			return "";
		}
	}
}
