package cn.wangz.atlas.model;

import java.util.Arrays;
import java.util.HashSet;

import cn.wangz.atlas.model.converter.EntityConverter;
import cn.wangz.atlas.model.entity.hive.HiveDb;
import cn.wangz.atlas.model.entity.hive.HiveProcess;
import cn.wangz.atlas.model.entity.hive.HiveTable;
import com.google.gson.Gson;

public class HiveProcessTests {

    public static void main(String[] args) {

        HiveDb hiveDb = new HiveDb();
        hiveDb.setQualifiedName("test@testCluster");
        hiveDb.setOwner("tester");

        HiveTable inputTable = new HiveTable();
        inputTable.setQualifiedName("test.test_table@testCluster");
        inputTable.setName("test_table");
        inputTable.setDb(hiveDb);
        inputTable.setOwner("tester");

        HiveTable outTable = new HiveTable();
        outTable.setQualifiedName("test.test_table_target@testCluster");
        outTable.setName("test_table_target");
        outTable.setDb(hiveDb);
        outTable.setOwner("tester");

        HiveProcess hiveProcess = new HiveProcess();
        hiveProcess.setQualifiedName("hiveQualifiedName@testCluster");
        hiveProcess.setClusterName("testCluster");
        hiveProcess.setOperationType("Query");
        hiveProcess.setInputs(new HashSet<>(Arrays.asList(inputTable)));
        hiveProcess.setOutputs(new HashSet<>(Arrays.asList(outTable)));

        System.out.println(new Gson().toJson(new EntityConverter(hiveProcess).convert()));
    }

}
