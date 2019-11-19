package com.fr.plugin.takeup;

import com.fr.decision.system.bean.message.MessageUrlType;
import com.fr.decision.webservice.v10.message.MessageService;
import com.fr.log.FineLoggerFactory;
import com.fr.plugin.context.PluginContext;
import com.fr.plugin.observer.inner.AbstractPluginLifecycleMonitor;
import com.fr.plugin.takeup.utils.MachineCodeUtils;
import com.fr.plugin.takeup.utils.TakeupDetectionMessageUtils;
import com.fr.plugin.transform.FunctionRecorder;
import com.fr.stable.StringUtils;

import java.util.List;

/**
 * @author lidongy
 * @version 10.0
 * Created by lidongy on 2019/11/18
 */
@FunctionRecorder
public class TakeupDetectionLifecycleMonitor extends AbstractPluginLifecycleMonitor {
    public TakeupDetectionLifecycleMonitor() {
    }

    @Override
    public void afterRun(PluginContext pluginContext) {
    }

    @Override
    public void beforeStop(PluginContext pluginContext) {
    }

    @Override
    public void afterInstall(PluginContext pluginContext) {
        try {
            List<String> machineCodeList = MachineCodeUtils.getInvaildMachineCode();
            if (!machineCodeList.isEmpty()) {
                String message = TakeupDetectionMessageUtils.buildTakeupMessage(machineCodeList);
                FineLoggerFactory.getLogger().warn(message);
                MessageService.getInstance().sendMessage2SupperRole(message, StringUtils.EMPTY, MessageUrlType.NONE);
                MachineCodeUtils.clearInvaildMachineCodeFromFineDB();
            }
            TakeupDetectionFactory.initTakeupDetectionJob();
        } catch (Exception e) {
            FineLoggerFactory.getLogger().error(e.getMessage());
        }
    }

    @Override
    public void beforeUninstall(PluginContext pluginContext) {
        try {
            TakeupDetectionFactory.removeTakeupDetectionJob();
        } catch (Exception e) {
            FineLoggerFactory.getLogger().error(e.getMessage());
        }
    }

    @Override
    public void afterUpdate(PluginContext pluginContext) {
    }

    @Override
    public int currentAPILevel() {
        return CURRENT_LEVEL;
    }
}
