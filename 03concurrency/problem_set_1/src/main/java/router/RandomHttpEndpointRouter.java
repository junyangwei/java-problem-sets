package router;

import java.util.List;
import java.util.Random;

/**
 * @author junyangwei
 * @date 2021-10-09
 */
public class RandomHttpEndpointRouter implements HttpEndpointRouter {
    @Override
    public String route(List<String> urls) {
        int size = urls.size();
        Random random = new Random(System.currentTimeMillis());
        return urls.get(random.nextInt(size));
    }
}
