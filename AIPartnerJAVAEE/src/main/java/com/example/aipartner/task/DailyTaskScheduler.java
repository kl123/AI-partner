package com.example.aipartner.task;

import com.example.aipartner.service.IndividualStudyPlanningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DailyTaskScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyTaskScheduler.class);

    @Autowired
    private IndividualStudyPlanningService individualStudyPlanningService;

    // 每天凌晨 00:00:00 执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void executeDailyTask() {
        try {
            log.info("定时任务开始执行：{}", new java.util.Date());

            // TODO: 替换为实际业务逻辑
            // 示例：清理过期数据、发送每日报告等
            performBusinessLogic();

            log.info("定时任务执行完成");
        } catch (Exception e) {
            log.error("定时任务执行失败", e);
        }
    }

    private void performBusinessLogic() {
        // 示例逻辑：打印当前时间
        individualStudyPlanningService.updateRisk();
        System.out.println("当前时间：" + new java.util.Date());
    }
}
