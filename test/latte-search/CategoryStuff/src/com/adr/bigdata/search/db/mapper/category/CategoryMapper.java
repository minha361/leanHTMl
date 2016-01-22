package com.adr.bigdata.search.db.mapper.category;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.search.db.bean.category.CategoryBean;

public class CategoryMapper implements ResultSetMapper<CategoryBean> {

	@Override
	/**
	 * Lưu ý với category path đã bỏ đi số 0 ở đầu khi lấy về từ db
	 */
	public CategoryBean map(int i, ResultSet r, StatementContext arg2)
			throws SQLException {
		CategoryBean bean = new CategoryBean();
		bean.setId(r.getInt("Id"));
		String sPath = r.getString("Path");
		String[] ssPath = sPath.split(",");
		List<Integer> path = new ArrayList<Integer>();
		int length1 = ssPath.length - 1;
		for (int j = 0; j < length1; j++)
			path.add(Integer.parseInt(ssPath[j]));
		bean.setPath(path);
		bean.setStatus(r.getInt("Status"));
		bean.setParentId(r.getInt("ParentCategoryId"));
		bean.setName(r.getString("Name"));
		bean.setLeaf(r.getBoolean("IsLeaf"));
		bean.setUpdateTime(r.getTimestamp("UpdatedDate").getTime());
		return bean;
	}

}
