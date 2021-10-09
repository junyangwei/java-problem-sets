package router;

import java.util.List;

/**
 * @author junyangwei
 * @date 2021-10-09
 */
public interface HttpEndpointRouter {

    String route(List<String> endpoints);

    // Load Balance
    // Random
    // RoundRibbon
    // Weight
    // - server01,20
    // - server02,30
    // - server03,50
}
