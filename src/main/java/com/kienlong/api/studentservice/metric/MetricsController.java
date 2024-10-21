package com.kienlong.api.studentservice.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricsController {

//    private final Counter myCounter;
//
//    public MetricsController(MeterRegistry meterRegistry) {
//        this.myCounter = meterRegistry.counter("custom_metric_counter");
//    }
//
//    @GetMapping("/count")
//    public String count() {
//        myCounter.increment();
//        return "Counter increased!";
//    }
}
