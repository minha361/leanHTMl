package test;

import java.util.ArrayList;
import java.util.List;

import com.nhb.common.data.PuObject;

public class TestProductItem extends BaseUnitTest {
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();

		List<PuObject> pis = new ArrayList<PuObject>();

		PuObject pi;

		pi = PuObject
				.fromJSON("{\"image\":\"[]\",\"productItemName\":\"tst 18_7\",\"brandName\":\"aAvinaa\",\"productId\":173295,\"categoryPath\":[597,596,595,591],\"productItemId\":215313,\"freshFoodType\":0,\"weight\":0.33,\"updateTime\":1437195168823,\"brandStatus\":1,\"categoryName\":\"Nước mặn\",\"productItemStatus\":3,\"productItemPolicy\":1,\"productItemType\":4,\"solrFeProductAttribute\":[{\"attributeId\":135,\"attributeStatus\":1,\"attributeValueId\":1717,\"attributeValue\":\"100\",\"attributeName\":\"Khối lượng\"}],\"createTime\":\"2015-07-18 11:52:48\",\"categoryStatus\":1,\"brandId\":2459,\"warehouseProductItemMapping\":[{\"merchantProductItemStatus\":3,\"merchantStatus\":1,\"quantity\":100,\"originalPrice\":1000.0,\"safetyStock\":0,\"sellPrice\":1000.0,\"priceStatus\":1,\"isVisible\":463,\"merchantSKU\":\"g76t6b8\",\"provinceId\":4,\"merchantName\":\"acc not share\",\"warehouseProductItemMappingId\":1585350,\"merchantId\":2282,\"warehouseId\":1483,\"warehouseStatus\":1,\"vatStatus\":1, \"isNotApplyCommision\":1, \"commisionFee\":13}],\"barcode\":\"242e7318-ff63-47df-bc00-ee342f53e98f\",\"categoryId\":597}");
//		System.out.println(pi.toJSON());
		pis.add(pi);
		PuObject pris = new PuObject("list", pis);
		getLogicProcessor(10).execute(pris);

		System.out.println("total time: " + (System.currentTimeMillis() - start));
	}
}
