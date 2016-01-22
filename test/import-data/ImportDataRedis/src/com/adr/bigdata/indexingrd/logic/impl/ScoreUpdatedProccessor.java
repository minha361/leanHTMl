/**
 * 
 */
package com.adr.bigdata.indexingrd.logic.impl;

import static com.adr.bigdata.indexingrd.models.impl.ScoreModel.S_DISTRICTS_JSON;
import static com.adr.bigdata.indexingrd.models.impl.ScoreModel.S_JSON_SCORE;
import static com.adr.bigdata.indexingrd.models.impl.ScoreModel.S_SCORE;
import static com.adr.bigdata.indexingrd.models.impl.ScoreModel.S_UPDATE_TIME;
import static com.adr.bigdata.indexingrd.models.impl.ScoreModel.S_WHPIMID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.adr.bigdata.indexingrd.APIFields;
import com.adr.bigdata.indexingrd.logic.BaseLogicProcessor;
import com.adr.bigdata.indexingrd.models.impl.ScoreModel;
import com.adr.bigdata.indexingrd.vos.ScoreVO;
import com.nhb.common.data.PuObject;

/**
 * @author ndn
 *
 */
public class ScoreUpdatedProccessor extends BaseLogicProcessor {
	private ScoreModel scoreModel;

	@Override
	public void execute(PuObject data) throws Exception {
		long start = System.currentTimeMillis();
		if (scoreModel == null) {
			scoreModel = getModel(ScoreModel.class);
		}

		Collection<PuObject> list = data.getPuObjectArray(APIFields.LIST);

		// get list wpimIds to get cache
		Set<Integer> wpimIds = new HashSet<Integer>();
		List<ScoreVO> scoreVOs = new ArrayList<ScoreVO>();
		for (PuObject e : list) {
			Integer wpimId = e.getInteger(S_WHPIMID);
			Double score = e.getDouble(S_SCORE);
			String jsonScore = e.getString(S_JSON_SCORE);
			String sDistrictsJson = e.getString(S_DISTRICTS_JSON);
			Long updateTime = e.getLong(S_UPDATE_TIME);

			if (wpimId == null || updateTime == null) {
				getLogger().error("warehouseProductItemMappingId or updateTime is null: {}, {}", wpimId, updateTime);
				continue;
			}

			ScoreVO vo = new ScoreVO();
			vo.setWpimId(wpimId);
			vo.setUpdateTime(updateTime);
			vo.setScore(score);
			vo.setJsonScore(jsonScore);
			vo.setDistrictsJson(sDistrictsJson);

			if (vo.getScore() == null && vo.getJsonScore() == null && vo.getDistrictsJson() == null) {
				getLogger().error("all Fields: score, jsonScore, districtsJson are null or invalid");
				continue;
			}

			wpimIds.add(wpimId);
			scoreVOs.add(vo);
		}

		// get cache
		Map<String, String> lastUpdateTimes = scoreModel.getLastUpdateTimes(wpimIds);

		// check cache
		List<ScoreVO> updatedScoreVOs = new ArrayList<ScoreVO>();
		for (ScoreVO vo : scoreVOs) {
			Long lastUpdateTime = Long.valueOf(lastUpdateTimes.get(vo.getWpimId()));
			if (lastUpdateTime != null && lastUpdateTime >= vo.getUpdateTime()) {
				getLogger().info("score of warehouseProductItemMapping {} has been updated by a newer", vo.getWpimId());
			} else {
				updatedScoreVOs.add(vo);
			}
		}

		scoreModel.updateToRedis(updatedScoreVOs);
		scoreModel.updateTimeToCache(updatedScoreVOs);
		scoreModel.sendLastUpdatedWhpIdsInteger(wpimIds);

		getProfillingLogger().debug("time execute {} score is {} ms", scoreVOs.size(), (System.currentTimeMillis() - start));
	}

}
