package com.fr.plugin.takeup;

import com.fr.scheduler.QuartzContext;
import com.fr.scheduler.ScheduleJobManager;
import com.fr.third.v2.org.quartz.JobKey;
import com.fr.third.v2.org.quartz.SimpleScheduleBuilder;
import com.fr.third.v2.org.quartz.TriggerBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lidongy
 * @version 10.0
 * Created by lidongy on 2019/11/18
 */
public class TakeupDetectionFactory {

    private static final int ONE_MINUTE = 60 * 1000;

    public static void initTakeupDetectionJob() throws Exception {
        JobKey jobKey = new JobKey("takeupDetectionTask", "takeupDetectionGroup");
        if (QuartzContext.getInstance().getScheduler().checkExists(jobKey)) {
            ScheduleJobManager.getInstance().removeJob(jobKey.getName(), jobKey.getGroup());
        }
        addTakeupDetectionJob(jobKey);
    }

    private static void addTakeupDetectionJob(JobKey jobKey) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 2, 0, 0);
        Map<String, Object> param = new HashMap<String, Object>();
        TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger();
        long frequency = 5L * ONE_MINUTE;
        triggerBuilder.forJob(jobKey.getName(), jobKey.getGroup())
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .startAt(new Date(cal.getTime().getTime() + frequency));
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule();
        schedule.withMisfireHandlingInstructionNextWithExistingCount();
        schedule.withIntervalInMinutes(5);
        schedule.repeatForever();
        triggerBuilder.withSchedule(schedule);
        ScheduleJobManager.getInstance().addJob(
                jobKey.getName(),
                jobKey.getGroup(),
                "takeup detection job",
                TakeupDetectionJob.class,
                triggerBuilder.build(),
                param);
    }

    public static void removeTakeupDetectionJob() throws Exception {
        JobKey jobKey = new JobKey("takeupDetectionTask", "takeupDetectionGroup");
        if (QuartzContext.getInstance().getScheduler().checkExists(jobKey)) {
            ScheduleJobManager.getInstance().removeJob(jobKey.getName(), jobKey.getGroup());
        }
    }
}
