package com.adr.bigdata.indexingrd.models;

import com.nhb.common.Loggable;
import com.nhb.common.db.sql.DBIAdapter;
import com.nhb.common.db.sql.daos.AbstractDAO;

public class AbstractModel implements Loggable {
	public static final int ONSITE_STATUS = 511;
	
	private DBIAdapter dbAdapter;

	protected DBIAdapter getDbAdapter() {
		return dbAdapter;
	}

	public void setDbAdapter(DBIAdapter dbAdapter) {
		this.dbAdapter = dbAdapter;
	}

	protected <T extends AbstractDAO> T openDAO(Class<T> daoClass) {
		assert daoClass != null;
		return this.dbAdapter.openDAO(daoClass);
	}

	public static byte[] longToBytes(long x) {
	    byte[] rs = new byte[8];
	    rs[0] = (byte) (x >> 56);
	    rs[1] = (byte) (x >> 48);
	    rs[2] = (byte) (x >> 40);
	    rs[3] = (byte) (x >> 32);
	    rs[4] = (byte) (x >> 24);
	    rs[5] = (byte) (x >> 16);
	    rs[6] = (byte) (x >> 8);
	    rs[7] = (byte) x;
		
	    return rs;
	}
	
	public static byte[] intToBytes(int x) {
		byte[] rs = new byte[4];
	    rs[0] = (byte) (x >> 24);
	    rs[1] = (byte) (x >> 16);
	    rs[2] = (byte) (x >> 8);
	    rs[3] = (byte) x;
		
	    return rs;
	}
	
	public static int bytesToInt(byte[] bytes) {
		int i = 0;
		i |= (bytes[0] & 0xFF) << 24;
		i |= (bytes[1] & 0xFF) << 16;
		i |= (bytes[2] & 0xFF) << 8;
		i |= bytes[3] & 0xFF;
		return i;
	}

	public static long bytesToLong(byte[] bytes) {
	    long l = 0;
	    l |= ((long) (bytes[0] & 0xFF)) << 56;
	    l |= ((long) (bytes[1] & 0xFF)) << 48;
	    l |= ((long) (bytes[2] & 0xFF)) << 40;
	    l |= ((long) (bytes[3] & 0xFF)) << 32;
	    l |= ((long) (bytes[4] & 0xFF)) << 24;
	    l |= ((long) (bytes[5] & 0xFF)) << 16;
	    l |= ((long) (bytes[6] & 0xFF)) << 8;
	    l |= (long) (bytes[7] & 0xFF);
	    return l;
	}
}
