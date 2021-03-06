/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mario.consumer.test.db.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.adr.bigdata.indexing.db.sql.beans.CategoryBean;

/**
 *
 * @author ndn
 */
public class CategoryMapper implements ResultSetMapper<CategoryBean> {

	@Override
	public CategoryBean map(int i, ResultSet rs, StatementContext sc)
			throws SQLException {
		CategoryBean bean = new CategoryBean();
		bean.setId(rs.getInt("CategoryId"));
		bean.setParentId(rs.getInt("CategoryParentId"));
		bean.setName(rs.getString("CategoryName"));
		return bean;
	}

}
