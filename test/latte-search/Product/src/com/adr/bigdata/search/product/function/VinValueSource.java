package com.adr.bigdata.search.product.function;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.index.DocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.index.SortedDocValues;
import org.apache.lucene.queries.function.FunctionValues;
import org.apache.lucene.queries.function.docvalues.FloatDocValues;
import org.apache.lucene.queries.function.valuesource.FieldCacheSource;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.mutable.MutableValue;
import org.apache.lucene.util.mutable.MutableValueFloat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VinValueSource extends FieldCacheSource {
	public VinValueSource() {
		super(SELL_PRICE);
	}

	public static final String IS_NOT_APPLY_COMMISION = "is_not_apply_commision";
	public static final String COMMISION_FEE = "commision_fee";
	public static final String START_TIME_DISCOUNT = "start_time_discount";
	public static final String FINISH_TIME_DISCOUNT = "finish_time_discount";
	public static final String IS_PROMOTION = "is_promotion";
	public static final String IS_PROMOTION_MAPPING = "is_promotion_mapping";
	public static final String PROMOTION_PRICE = "promotion_price";
	public static final String SELL_PRICE = "sell_price";

	public final Logger log = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("rawtypes")
	@Override
	public FunctionValues getValues(Map context, LeafReaderContext readerContext) throws IOException {
		NumericDocValues commisionBool = DocValues.getNumeric(readerContext.reader(), IS_NOT_APPLY_COMMISION);
		NumericDocValues commisionFeeNum = DocValues.getNumeric(readerContext.reader(), COMMISION_FEE);
		NumericDocValues startNum = DocValues.getNumeric(readerContext.reader(), START_TIME_DISCOUNT);
		NumericDocValues finishNum = DocValues.getNumeric(readerContext.reader(), FINISH_TIME_DISCOUNT);

		SortedDocValues isPromoBool = DocValues.getSorted(readerContext.reader(), IS_PROMOTION);
		int isPromoTrueOrder = trueOrder(isPromoBool);
		SortedDocValues isPromoMappingBool = DocValues.getSorted(readerContext.reader(), IS_PROMOTION_MAPPING);
		int isPromoMappingTrueOrder = trueOrder(isPromoMappingBool);

		NumericDocValues promoPriceNum = DocValues.getNumeric(readerContext.reader(), PROMOTION_PRICE);
		NumericDocValues sellPriceNum = DocValues.getNumeric(readerContext.reader(), SELL_PRICE);

		return new FloatDocValues(this) {

			@Override
			public float floatVal(int doc) {
				boolean isNotApplyCommision = commisionBool.get(doc) == 1;
				float commisionFee = Float.intBitsToFloat((int) commisionFeeNum.get(doc));
				long startPromo = startNum.get(doc);
				long finishPromo = finishNum.get(doc);
				boolean isPromo = isPromoBool.getOrd(doc) == isPromoTrueOrder;
				boolean isPromoMapping = isPromoMappingBool.getOrd(doc) == isPromoMappingTrueOrder;
				float promoPrice = Float.intBitsToFloat((int) promoPriceNum.get(doc));
				float sellPrice = Float.intBitsToFloat((int) sellPriceNum.get(doc));

				log.debug("isNotApplyCommision: {}", isNotApplyCommision);
				log.debug("commisionFee: {}", commisionFee);
				log.debug("startPromo: {}", startPromo);
				log.debug("finishPromo: {}", finishPromo);
				log.debug("isPromo: {}", isPromo);
				log.debug("isPromoMapping: {}", isPromoMapping);
				log.debug("promoPrice: {}", promoPrice);
				log.debug("sellPrice: {}", sellPrice);

				long now = System.currentTimeMillis();
				float priceWithPromo = (isPromo && isPromoMapping && startPromo < now && finishPromo > now) ? promoPrice
						: sellPrice;
				float finalPrice = isNotApplyCommision ? (priceWithPromo * (100 - commisionFee) / 100) : priceWithPromo;
				return finalPrice;
			}

			@Override
			public boolean exists(int doc) {
				return true;
			}

			@Override
			public ValueFiller getValueFiller() {
				return new ValueFiller() {
					private final MutableValueFloat mval = new MutableValueFloat();

					@Override
					public MutableValue getValue() {
						return mval;
					}

					@Override
					public void fillValue(int doc) {
						mval.value = floatVal(doc);
						mval.exists = true;
					}
				};
			}
		};
	}

	/**
	 * Copy from solr 5.3.1
	 */
	private static final int trueOrder(SortedDocValues sindex) {
		int nord = sindex.getValueCount();
		int tord = -2;
		for (int i = 0; i < nord; i++) {
			final BytesRef br = sindex.lookupOrd(i);
			if (br.length == 1 && br.bytes[br.offset] == 'T') {
				tord = i;
				break;
			}
		}
		return tord;
	}
}
