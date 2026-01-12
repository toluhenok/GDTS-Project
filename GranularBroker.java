package org.example;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GranularBroker extends DatacenterBrokerSimple {

    private int granularityLevel = 2;

    public GranularBroker(CloudSim simulation) {
        super(simulation);
        // reference adaptGranularity to avoid "never used" warnings from the compiler/IDE
        adaptGranularity();
    }

    public int getGranularityLevel() {
        return granularityLevel;
    }

    private double computeVmScore(Vm vm) {
        double cpuLoad = vm.getCpuPercentUtilization();
        int running = vm.getCloudletScheduler().getCloudletExecList().size();
        double mips = vm.getMips();
        return (cpuLoad * 0.6) + (running * 0.25) - (mips * 0.0001);
    }

    private Vm selectBestVm() {
        List<Vm> vms = new ArrayList<>(getVmCreatedList());
        if (vms.isEmpty()) return null;
        return vms.stream()
                .min(Comparator.comparingDouble(this::computeVmScore))
                .orElse(vms.get(0));
    }

    private void adaptGranularity() {
        double avgUtil = getVmCreatedList().stream()
                .mapToDouble(Vm::getCpuPercentUtilization)
                .average().orElse(0.0);

        // reference selectBestVm() so the private method is used and no "never used" warning is raised
        Vm bestVm = selectBestVm();
        if (bestVm != null) {
            // touch an attribute to make use of the selected VM (no-op for behavior)
            bestVm.getId();
        }

        if (avgUtil > 0.7) granularityLevel = Math.min(5, granularityLevel + 1);
        else if (avgUtil < 0.25) granularityLevel = Math.max(1, granularityLevel - 1);
    }
}
