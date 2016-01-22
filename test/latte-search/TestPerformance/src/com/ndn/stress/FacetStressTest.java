package com.ndn.stress;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Strings;
import com.ndn.stress.utils.FileSystemUtils;
import com.ndn.stress.utils.Initializer;

public class FacetStressTest {
	// http://10.220.75.133:8983/solr/product/getfilter?catid=
	// http://10.220.75.78:8983/solr/product/getfilter?category=

	private static AtomicInteger numSuccess = new AtomicInteger(0);
	private static AtomicInteger numFail = new AtomicInteger(0);
	private static AtomicLong totalTimeSuccess = new AtomicLong();
	private static CountDownLatch latch;

	public static void main(String[] args) throws IOException, InterruptedException {
		int NUM = Integer.parseInt(args[0]);
		int MAX = Integer.parseInt(args[1]);
		String prefix = args[2];

		ExecutorService executor = Executors.newFixedThreadPool(MAX);
		List<String> params = getCats();
		long start = System.currentTimeMillis();
		latch = new CountDownLatch(NUM * params.size());
		for (int i = 0; i < NUM; i++) {
			for (String param : params) {
				String link = prefix + URLEncoder.encode(param, "UTF-8");
				executor.submit(new CallRunnable(link));
			}
			//			Collections.shuffle(param);
		}
		if (latch.getCount() != 0) {
			latch.await();
		}
		long totalTime = System.currentTimeMillis() - start;
		double avg = (double) totalTimeSuccess.get() / numSuccess.get();

		System.out.println("link: " + prefix);
		System.out.println("ccu: " + MAX);
		System.out.println("totalTimeSuccess: " + totalTimeSuccess.get());
		System.out.println("totalTime: " + totalTime);
		System.out.println("numSuccess: " + numSuccess.get());
		System.out.println("numFail: " + numFail.get());
		System.out.println("avg: " + avg);

		executor.shutdown();
	}

	static class CallRunnable implements Runnable {
		private String link;

		public CallRunnable(String link) {
			this.link = link;
		}

		@Override
		public void run() {
			try {
				long start = System.currentTimeMillis();
				callHttp(link);
				totalTimeSuccess.addAndGet(System.currentTimeMillis() - start);
				numSuccess.incrementAndGet();
			} catch (Exception e) {
				numFail.incrementAndGet();
			} finally {
				latch.countDown();
			}
		}
	}

	static void callHttp(String link) throws Exception {
		try {
			URL url = new URL(link);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.getContent();
		} catch (Exception e) {
			System.err.println("error link: " + link + " --- " + e.getMessage());
			throw e;
		}
	}

	static List<String> getCats() throws IOException {
		Initializer.bootstrap(FacetStressTest.class);
		String fileCats = FileSystemUtils.createAbsolutePathFrom("conf", "cats.dat");
		List<String> cats = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(fileCats));
		String line;
		while ((line = br.readLine()) != null) {
			String cat = line.trim();
			if (!cat.isEmpty()) {
				cats.add(cat);
			}
		}
		br.close();
		return cats;
	}

	static Set<String> getParams() throws IOException {
		Initializer.bootstrap(FacetStressTest.class);
		String fileCats = FileSystemUtils.createAbsolutePathFrom("conf", "cats.dat");
		String fileDist = FileSystemUtils.createAbsolutePathFrom("conf", "district.dat");
		Set<String> params = new HashSet<String>();

		List<String> cats = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(fileCats));
		String line;
		while ((line = br.readLine()) != null) {
			String cat = line.trim();
			if (!Strings.isNullOrEmpty(cat)) {
				cats.add(cat);
			}
		}

		br.close();

		List<String> dist = new ArrayList<String>();
		br = new BufferedReader(new FileReader(fileDist));
		while ((line = br.readLine()) != null) {
			String distStr = line.trim();
			if (!Strings.isNullOrEmpty(distStr)) {
				dist.add(distStr);
			}
		}
		br.close();

		for (String distId : dist) {
			String[] splitedDist = distId.split(",");
			if (splitedDist.length < 2) {
				continue;
			}
			for (String cat : cats) {
				String param = "keyword=" + cat + "&cityid=" + splitedDist[1] + "&districtid=" + splitedDist[0];
				params.add(param);
			}
		}

		BufferedWriter bw = new BufferedWriter(new FileWriter("conf/out_param.dat"));
		for (String p : params) {
			bw.write(p + "\r\n");
		}
		bw.close();
		return params;
	}
}
