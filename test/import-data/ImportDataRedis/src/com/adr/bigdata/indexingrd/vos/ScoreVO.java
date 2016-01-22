/**
 * 
 */
package com.adr.bigdata.indexingrd.vos;

/**
 * @author ndn
 *
 */
public class ScoreVO {
	private int wpimId;
	private Double score = null;
	private String jsonScore = null;
	private String districtsJson = null;
	private long updateTime;

	public int getWpimId() {
		return wpimId;
	}

	public void setWpimId(int wpimId) {
		this.wpimId = wpimId;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getJsonScore() {
		return jsonScore;
	}

	public void setJsonScore(String jsonScore) {
		this.jsonScore = jsonScore;
	}

	public String getDistrictsJson() {
		return districtsJson;
	}

	public void setDistrictsJson(String districtsJson) {
		this.districtsJson = districtsJson;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "ScoreVO [wpimId=" + wpimId + ", score=" + score + ", jsonScore=" + jsonScore + ", districtsJson="
				+ districtsJson + ", updateTime=" + updateTime + "]";
	}

}
