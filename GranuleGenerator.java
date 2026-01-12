package org.example;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;

import java.util.ArrayList;
import java.util.List;

public class GranuleGenerator {

    public static List<Cloudlet> generateGranules(int taskId, long taskLength, int granularityLevel, int pes) {
        List<Cloudlet> granules = new ArrayList<>();
        if (granularityLevel < 1) granularityLevel = 1;

        long baseSize = taskLength / granularityLevel;
        long remainder = taskLength % granularityLevel;

        for (int i = 0; i < granularityLevel; i++) {
            long size = baseSize + (i == 0 ? remainder : 0);
            Cloudlet granule = new CloudletSimple(taskId * 1000 + i, size, pes);
            granules.add(granule);
        }
        return granules;
    }

    public static List<Cloudlet> generateManyTasks(int startTaskId,
                                                   int numberOfTasks,
                                                   long taskBaseLength,
                                                   int granularityLevel,
                                                   int pes) {
        List<Cloudlet> all = new ArrayList<>();
        for (int t = 0; t < numberOfTasks; t++) {
            List<Cloudlet> g = generateGranules(startTaskId + t, taskBaseLength + (t * 500), granularityLevel, pes);
            all.addAll(g);
        }
        return all;
    }
}
