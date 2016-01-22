package com.adr.extractlog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.log4j.Logger;

import com.adr.extractlog.bean.LogBean;
import com.adr.log.manager.LogManager;


public class ExtractLog {
	protected static Logger logger = Logger.getLogger(ExtractLog.class);
	public static void main(String[] args) {
		try {
			LogManager logManager = LogManager.getInstance();
			long now = System.currentTimeMillis();
			LogGetter getLog = new LogGetter();
			int logPageNumber = 0;
			while (getLog.isHasMore()) {
				List<LogBean> logBeans = getLog.getLogs(logPageNumber);
				logManager.addLogs(logBeans);
				logPageNumber++;
//				break;
			}
			System.out.println("total getLogs's time = " + (System.currentTimeMillis() - now));
			now = System.currentTimeMillis();
			logManager.loadProductItemInfor();
			System.out.println("total getProductItemTags's time = " + (System.currentTimeMillis() - now));
			now = System.currentTimeMillis();
			logManager.exportAllLog();
			System.out.println("exportAllLog's time spend = " + (System.currentTimeMillis() - now));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(getStackTrace(e));
			
		}
	}
	public static String getStackTrace(final Throwable throwable) {
	     final StringWriter sw = new StringWriter();
	     final PrintWriter pw = new PrintWriter(sw, true);
	     throwable.printStackTrace(pw);
	     return sw.getBuffer().toString();
	}
}
