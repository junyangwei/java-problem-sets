package work;


/**
 * 发送 HTTP 请求练习
 * @author junyangwei
 * @date 2021-09-26
 */
public class HttpPractice {
    public static void main(String[] args) {
        String url = "http://localhost:8801";
        try {
            String result = HttpClientHelper.getAsString(url);
            System.out.println("Get url :" + url + " ; response :" +  result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
