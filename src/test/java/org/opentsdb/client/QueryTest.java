package org.opentsdb.client;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import org.junit.Test;
import org.opentsdb.client.request.QueryBuilder;
import org.opentsdb.client.request.SubQueries;
import org.opentsdb.client.response.SimpleHttpResponse;
import org.opentsdb.client.util.Aggregator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by shifeng on 2016/5/19. MyProject
 */
public class QueryTest {

	@Test
	public void queryTest() throws IOException {

		HttpClientImpl client = new HttpClientImpl("http://localhost:4399");

		QueryBuilder builder = QueryBuilder.getInstance();
		SubQueries subQueries = new SubQueries();

		String sum = Aggregator.sum.toString();
		subQueries.addMetric("metric1").addTag("tag1", "tab1value").addAggregator(sum).addDownsample("1s-" + sum);
		long now = new Date().getTime() / 1000;
		builder.getQuery().addStart(126358720).addEnd(now).addSubQuery(subQueries);
		System.out.println(builder.build());

		try {
			SimpleHttpResponse response = client.pushQueries(builder, ExpectResponse.STATUS_CODE);
			String content = response.getContent();
			int statusCode = response.getStatusCode();
			if (statusCode == 200) {
				JSONArray jsonArray = JSON.parseArray(content);
				for (Object object : jsonArray) {
					JSONObject json = (JSONObject) JSON.toJSON(object);
					String dps = json.getString("dps");
					Map<String, String> map = JSON.parseObject(dps, Map.class);
					for (Map.Entry entry : map.entrySet()) {
						System.out.println("Time:" + entry.getKey() + ",Value:" + entry.getValue());
						Double.parseDouble(String.valueOf(entry.getValue()));
					}
				}
			}
			// System.out.println(jsonArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
