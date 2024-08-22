package com.dji.sample;

import cn.hutool.cron.CronUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.dji.sample.*.dao")
@SpringBootApplication
@EnableScheduling
@ComponentScan("com.dji")
public class CloudApiSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudApiSampleApplication.class, args);

		// 开启定时任务管理，用于管理DRC心跳
		CronUtil.setMatchSecond(true);
		CronUtil.start(true);
	}

}
