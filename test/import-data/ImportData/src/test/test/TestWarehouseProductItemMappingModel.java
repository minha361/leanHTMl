package test;

import java.util.ArrayList;
import java.util.List;

import com.adr.bigdata.indexing.models.impl.WarehouseProductItemMappingModel;
import com.nhb.common.data.PuObject;

public class TestWarehouseProductItemMappingModel extends BaseUnitTest {
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		List<PuObject> whpims = new ArrayList<PuObject>();

		PuObject whpim1 = new PuObject();
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_ID, 1);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_WH_ID, 1);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_PRODUCT_ITEM_ID, 1);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_ID, 1);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_QUANTITY, 1);
		whpim1.setString(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_SKU, "1321423423");
		whpim1.setDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_SELL_PRICE, 423423.342343);
		whpim1.setDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_ORIGINAL_PRICE, 87676.3232);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_PRODUCT_ITEM_STATUS, 1);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_SAFETY_STOCK, 3);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_IS_VISIBLE, 521);
		whpim1.setLong(WarehouseProductItemMappingModel.WH_PI_MAPPING_UPDATE_TIME, 3L);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_PRICE_STATUS, 1);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_VAT_STATUS, 0);

		whpims.add(whpim1);

		whpim1 = new PuObject();
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_ID, 2791);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_WH_ID, 44);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_PRODUCT_ITEM_ID, 1321);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_ID, 176);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_QUANTITY, 24);
		whpim1.setString(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_SKU, "xxxxxxx---132fds1423423");
		whpim1.setDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_SELL_PRICE, 42313.342343);
		whpim1.setDouble(WarehouseProductItemMappingModel.WH_PI_MAPPING_ORIGINAL_PRICE, 1111676.3232);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_MERCHANT_PRODUCT_ITEM_STATUS, 0);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_SAFETY_STOCK, 5);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_IS_VISIBLE, 521);
		whpim1.setLong(WarehouseProductItemMappingModel.WH_PI_MAPPING_UPDATE_TIME, 3L);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_PRICE_STATUS, 0);
		whpim1.setInteger(WarehouseProductItemMappingModel.WH_PI_MAPPING_VAT_STATUS, 1);
		
		whpims.add(whpim1);

		PuObject puWhpims = new PuObject("list", whpims);
		getLogicProcessor(9).execute(puWhpims);

		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
