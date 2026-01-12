package org.example;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

import java.util.ArrayList;
import java.util.List;

public class SimulationRunner {

    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("  GRANULAR COMPUTING – CLOUDSIM SIMULATION  ");
        System.out.println("============================================");

        CloudSim simulation = new CloudSim();

        // Create datacenters with sufficient hosts
        createDatacenter(simulation, 5, 1000);
        createDatacenter(simulation, 5, 2500);

        GranularBroker broker = new GranularBroker(simulation);

        // Create VMs
        List<Vm> vmList = createVms(6);
        broker.submitVmList(vmList);

        // Create cloudlets (tasks)
        List<Cloudlet> granules = GranuleGenerator.generateManyTasks(0, 2, 4000, 2, 1);
        broker.submitCloudletList(granules);

        // Set termination time to avoid infinite simulation
        simulation.terminateAt(5000);
        
        simulation.start();

        System.out.println("\n=========== Simulation Results ===========");

        broker.getCloudletFinishedList().forEach(cl ->
                System.out.printf(
                        "Cloudlet %d finished on VM %d at time %.2f%n",
                        cl.getId(), cl.getVm().getId(), cl.getFinishTime()
                )
        );

        double makespan = broker.getCloudletFinishedList().stream()
                .mapToDouble(Cloudlet::getFinishTime)
                .max().orElse(0.0);

        System.out.printf("Makespan: %.2f%n", makespan);
        System.out.printf("Total Cloudlets Finished: %d%n", broker.getCloudletFinishedList().size());
    }

    private static DatacenterSimple createDatacenter(CloudSim sim, int hosts, long mipsPerPe) {
        List<Host> hostList = new ArrayList<>();
        for (int i = 0; i < hosts; i++) {
            List<Pe> peList = new ArrayList<>();
            peList.add(new PeSimple(mipsPerPe));
            Host host = new HostSimple(4096L, 10000L, 100000L, peList);
            hostList.add(host);
        }
        return new DatacenterSimple(sim, hostList, new VmAllocationPolicySimple());
    }

    private static List<Vm> createVms(int count) {
        List<Vm> vms = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Vm vm = new VmSimple(1000 + i * 250, 1)
                    .setRam(1024)
                    .setBw(1000)
                    .setSize(10000);
            vms.add(vm);
        }
        return vms;
    }
}
