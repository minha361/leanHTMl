package com.adr.bigdata.indexing.logic.impl;

import static com.adr.bigdata.indexing.models.impl.CommisionModel.C_CATGORY_ID;
import static com.adr.bigdata.indexing.models.impl.CommisionModel.C_COMMISION_FEE;
import static com.adr.bigdata.indexing.models.impl.CommisionModel.C_CUSTOM_COMMISION_FEE;
import static com.adr.bigdata.indexing.models.impl.CommisionModel.C_IS_NOT_APPLY_COMMISION;
import static com.adr.bigdata.indexing.models.impl.CommisionModel.C_MERCHANT_ID;
import static com.adr.bigdata.indexing.models.impl.CommisionModel.C_UPDATE_TIME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adr.bigdata.indexing.db.sql.beans.CommisionBean;
import com.adr.bigdata.indexing.logic.BaseLogicProcessor;
import com.adr.bigdata.indexing.models.impl.CommisionModel;
import com.adr.bigdata.indexing.vos.CommisionVO;
import com.nhb.common.data.PuObject;

public class CommisionUpdatedLogicProcessor extends BaseLogicProcessor {
	private CommisionModel model = null;

	@Override
	public void execute(PuObject data) throws Exception {
		long start = System.currentTimeMillis();
		if (model == null) {
			model = getModel(CommisionModel.class);
		}

		Integer merchantId = data.getInteger(C_MERCHANT_ID);
		Collection<PuObject> customCommisionFee = data.getPuObjectArray(C_CUSTOM_COMMISION_FEE);
		if (merchantId == null || customCommisionFee == null) {
			getLogger().error("merchantId or customCommisionFee is null");
			return;
		} else {
			Map<String, CommisionVO> mapData = new HashMap<String, CommisionVO>();
			customCommisionFee.forEach(commision -> {
				Integer catId = commision.getInteger(C_CATGORY_ID);
				Integer isNotApplyCommision = commision.getInteger(C_IS_NOT_APPLY_COMMISION);
				Integer commisionFee = commision.getInteger(C_COMMISION_FEE);
				Long updateTime = commision.getLong(C_UPDATE_TIME);
				if (catId == null || isNotApplyCommision == null || commisionFee == null || updateTime == null) {
					getLogger().error("catId or isNotApplyCommision or commisionFee or updateTime is null");
					return;
				} else {
					CommisionVO commsionVO = new CommisionVO(merchantId, catId, isNotApplyCommision, commisionFee,
							updateTime);
					mapData.put(merchantId + "_" + catId, commsionVO);
				}
			});
			Map<String, Long> lastUpdateTime = model.getLastUpdatedTime(mapData.keySet());
			List<Integer> processedCat = new ArrayList<Integer>();
			mapData.forEach((mcCat, commision) -> {
				if (lastUpdateTime.containsKey(mcCat) && lastUpdateTime.get(mcCat) >= commision.getUpdateTime()) {
					getLogger("(merchant, category): " + mcCat + " has been updated by a newer");
				} else {
					processedCat.add(commision.getCategoryId());
				}
			});
			Collection<CommisionBean> wpimIds = model.getWPIMIds(merchantId, processedCat);
			Map<Integer, CommisionVO> processedData = new HashMap<Integer, CommisionVO>();
			Map<Integer, Long> catIdUpdateTimes = new HashMap<Integer, Long>();
			wpimIds.forEach(wpimId -> {
				if (mapData.containsKey(merchantId + "_" + wpimId.getCatId())) {
					CommisionVO commisionVO = mapData.get(merchantId + "_" + wpimId.getCatId());
					processedData.put(wpimId.getWpimId(), commisionVO);
					catIdUpdateTimes.put(wpimId.getCatId(), commisionVO.getUpdateTime());
				}
			});
			model.updateToSolr(processedData);
			model.updateToCache(merchantId, catIdUpdateTimes);
			getProfillingLogger().debug("time proccess 1 commison: " + (System.currentTimeMillis() - start));
		}
	}

}
