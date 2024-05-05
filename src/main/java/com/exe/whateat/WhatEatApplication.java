package com.exe.whateat;

import io.github.x4ala1c.tsid.TsidConfiguration;
import io.github.x4ala1c.tsid.TsidGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WhatEatApplication {

    private static final int TSID_NODE = 69;
    private static final long TSID_EPOCH = 1714496400000L;

    static {
        final TsidConfiguration configuration = TsidConfiguration.builder()
                .node(TSID_NODE)
                .epoch(TSID_EPOCH)      // 2024/05/01 or 1714496400000.
                .build();
        TsidGenerator.reset();
        TsidGenerator.globalGenerator(configuration);
    }

    public static void main(String[] args) {
        SpringApplication.run(WhatEatApplication.class, args);
    }
}
