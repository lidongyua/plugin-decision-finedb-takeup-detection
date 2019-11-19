package com.fr.plugin.takeup.utils;

import com.fr.cluster.ClusterBridge;
import com.fr.cluster.core.ClusterNode;
import com.fr.decision.db.DecisionDBEnv;
import com.fr.stable.db.DBContext;
import com.fr.stable.db.session.DBSession;
import com.fr.third.org.hibernate.jdbc.AbstractWork;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lidongy
 * @version 10.0
 * Created by lidongy on 2019/11/18
 */
public class MachineCodeUtils {
    private static final String MACHINE_CODE_SOURCE = "ResourceModuleConfig.workRoots.";

    /**
     * 获取finedb里记录的机器码集合
     */
    public static List<String> getMachineCodeFromFineDB() throws Exception {

        List<String> machineCodeList = new ArrayList<String>();
        DBContext context = DecisionDBEnv.getDBContext();
        DBSession session = context.openSession();
        session.doWork(new AbstractWork() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;
                String sql = "select * from fine_conf_entity where id like '" + MACHINE_CODE_SOURCE + "%'";

                try {
                    preparedStatement = connection.prepareStatement(sql);
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        String result = resultSet.getString("id");
                        machineCodeList.add(result.substring(MACHINE_CODE_SOURCE.length()));
                    }
                } finally {
                    resultSet.close();
                    preparedStatement.close();
                }
            }
        });
        return machineCodeList;
    }

    /**
     * 从工程本身（包括集群情况）获取机器码
     */
    public static List<String> getMachineCodeFromProject() throws Exception {

        List<String> machineCodeList = new ArrayList<String>();
        for (ClusterNode clusterNode : ClusterBridge.getView().listNodes()) {
            machineCodeList.add(clusterNode.getID());
        }
        return machineCodeList;
    }

    /**
     * 获取不合法的机器码
     */
    public static List<String> getInvaildMachineCode() throws Exception {
        List<String> fineDBMachineCodeList = getMachineCodeFromFineDB();
        List<String> projectMachineCodeList = getMachineCodeFromProject();
        List<String> invaildMachineCode = new ArrayList<>();
        for (String fineDBMachineCode : fineDBMachineCodeList) {
            if (!projectMachineCodeList.contains(fineDBMachineCode)) {
                invaildMachineCode.add(fineDBMachineCode);
            }
        }
        return invaildMachineCode;
    }

    /**
     * 清理finedb，使存储的机器码只有当前工程的（集群下保存所有节点）
     */
    public static void clearInvaildMachineCodeFromFineDB() throws Exception {
        List<String> invaildMachineCode = getInvaildMachineCode();

        if (!invaildMachineCode.isEmpty()) {
            for (String machineCode : invaildMachineCode) {
                deleteMachineCodeFromFineDB(machineCode);
            }
        }
    }

    /**
     * 从finedb删除某个机器码
     */
    public static void deleteMachineCodeFromFineDB(String machineCode) throws Exception {
        DBContext context = DecisionDBEnv.getDBContext();
        DBSession session = context.openSession();
        session.doWork(new AbstractWork() {
            @Override
            public void execute(Connection connection) throws SQLException {
                PreparedStatement preparedStatement = null;
                String sql = "delete from fine_conf_entity where id = '" + MACHINE_CODE_SOURCE + machineCode + "'";
                try {
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.executeUpdate();
                } finally {
                    preparedStatement.close();
                }
            }
        });
    }
}
