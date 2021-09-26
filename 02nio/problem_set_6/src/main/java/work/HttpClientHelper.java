package work;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 使用第三方库 HttpClient 封装 GET 和 POST 请求
 * - 参考：https://github.com/renfufei/JAVA-000/blob/main/Week_02/homework02/src/main/java/com/renfufei/homework02/HttpClientHelper.java
 * @author junyangwei
 * @date 2021-09-26
 */
public class HttpClientHelper {

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    /**
     * GET 请求
     * @param url 请求url
     * @return String
     * @throws IOException
     */
    public static String getAsString(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * Post 请求
     * @param url 请求链接
     * @param json 请求体
     * @param headers 请求头部信息
     * @return String
     * @throws IOException
     */
    public static String postAsJSON(String url, String json, Map<String, String> headers) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        Set<String> keySet = headers.keySet();
        for (String name : keySet) {
            httpPost.setHeader(name, headers.get(name));
        }
        final String JSON_TYPE = "application/json;charset=UTF-8";
        httpPost.setHeader(HTTP.CONTENT_TYPE, JSON_TYPE);
        StringEntity entity = new StringEntity(json, "UTF-8");

        httpPost.setEntity(entity);

        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            System.out.println(response.getStatusLine());
            HttpEntity httpEntity = response.getEntity();
            return EntityUtils.toString(httpEntity, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return null;
    }

    /**
     * 单元测试
     */
    public static void main(String[] args) throws Exception {
        String getUrl = "https://example.org/index.html";
        String getResult = HttpClientHelper.getAsString(getUrl);
        System.out.println("Get request url: " + getUrl + " ; response: \n" + getResult);

        String postUrl = "https://example.org/test";
        Map<String, String> headers = new HashMap<>();
        String postResult = HttpClientHelper.postAsJSON(postUrl, "field1=value1&field2=value2", headers);
        System.out.println("POST request url: " + postUrl + " ; response: \n" + postResult);
    }
}
