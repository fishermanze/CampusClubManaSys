package com.campusclub.activity.config;

import com.campusclub.activity.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Configuration
@EnableScheduling
@Component
public class ScheduledTaskConfig {

    private static final Logger logger = Logger.getLogger(ScheduledTaskConfig.class.getName());

    @Autowired
    private ActivityService activityService;

    /**
     * 每小时执行一次，更新活动状态
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时整点执行
    public void updateActivityStatusTask() {
        logger.info("执行活动状态自动更新任务，时间：" + LocalDateTime.now());
        try {
            activityService.updateActivityStatusAutomatically();
            logger.info("活动状态自动更新任务执行完成");
        } catch (Exception e) {
            logger.severe("活动状态自动更新任务执行失败：" + e.getMessage());
        }
    }

    /**
     * 每分钟执行一次，用于实时性要求较高的活动状态更新
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行
    public void updateActivityStatusTaskRealtime() {
        logger.fine("执行实时活动状态更新任务，时间：" + LocalDateTime.now());
        try {
            activityService.updateActivityStatusAutomatically();
        } catch (Exception e) {
            logger.warning("实时活动状态更新任务执行失败：" + e.getMessage());
        }
    }
}