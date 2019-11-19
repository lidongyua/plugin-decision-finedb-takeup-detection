package com.fr.plugin.takeup;

import com.fr.cluster.core.ClusterNode;
import com.fr.decision.system.bean.message.MessageUrlType;
import com.fr.decision.webservice.v10.message.MessageService;
import com.fr.log.FineLoggerFactory;
import com.fr.plugin.takeup.utils.MachineCodeUtils;
import com.fr.plugin.takeup.utils.TakeupDetectionMessageUtils;
import com.fr.scheduler.job.FineScheduleJob;
import com.fr.stable.StringUtils;
import com.fr.third.v2.org.quartz.JobExecutionContext;

import java.util.List;

/**
 * @author lidongy
 * @version 10.0
 * Created by lidongy on 2019/11/18
 */
public class TakeupDetectionJob extends FineScheduleJob {
    @Override
    public void run(JobExecutionContext context, ClusterNode node) throws Exception {
        List<String> machineCodeList = MachineCodeUtils.getInvaildMachineCode();
        if (!machineCodeList.isEmpty()) {
            String message = TakeupDetectionMessageUtils.buildTakeupMessage(machineCodeList);
            FineLoggerFactory.getLogger().warn(message);
            MessageService.getInstance().sendMessage2SupperRole(message, StringUtils.EMPTY, MessageUrlType.NONE);
        }
    }
}
