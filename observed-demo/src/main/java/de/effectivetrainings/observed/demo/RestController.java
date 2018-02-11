package de.effectivetrainings.observed.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping(value = "/{host}/{port}")
    public void request(@RequestParam(value = "slowness", required = false) boolean slowness, @PathVariable("host") String host, @PathVariable("port") Integer port) {
        if (host != null && port != null) {

            final URI target = UriComponentsBuilder
                    .newInstance()
                    .scheme("http")
                    .host(host)
                    .port(port)
                    .path("rest")
                    .queryParam("slowness", slowness )
                    .build()
                    .toUri();
            final String result = restTemplate.getForObject(target, String.class);
            System.out.println(result);
        }
    }

    @GetMapping("/rest")
    public String rest(@RequestParam("slowness") boolean slowness) throws Exception {
        if (slowness) {
            Thread.sleep(5000);
        }
        return "simple rest call";
    }
}
