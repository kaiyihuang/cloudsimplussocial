package org.cloudbus.cloudsim.brokers;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.SocialCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.hosts.SocialHost;
import org.cloudbus.cloudsim.user.User;
import org.cloudbus.cloudsim.vms.Vm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * A implementation of {@link DatacenterBroker} that uses a Best Fit
 * mapping between submitted cloudlets and Vm's, trying to place a Cloudlet
 * at the best suitable Vm which can be found (according to the required Cloudlet's PEs).
 * The Broker then places the submitted Vm's at the first Datacenter found.
 * If there isn't capacity in that one, it will try the other ones.
 *
 * @author Humaira Abdul Salam
 * @since CloudSim Plus 4.3.8
 */
public class DatacenterBrokerBestFitSocial extends DatacenterBrokerSimple {

    /**
     * Creates a DatacenterBroker object.
     *
     * @param simulation The CloudSim instance that represents the simulation the Entity is related to
     */
    public DatacenterBrokerBestFitSocial(final CloudSim simulation) {
        super(simulation);
    }

    /**
     * Selects the VM with the lowest number of PEs that is able to run a given Cloudlet.
     * In case the algorithm can't find such a VM, it uses the
     * default DatacenterBroker VM mapper as a fallback.
     *
     * @param cloudlet the Cloudlet to find a VM to run it
     * @return the VM selected for the Cloudlet or {@link Vm#NULL} if no suitable VM was found
     */
    @Override
    public Vm defaultVmMapper(final Cloudlet cloudlet) {
        if (cloudlet.isBoundToVm()) {
            return cloudlet.getVm();
        }

        ArrayList<Vm> temp_vm_list = new ArrayList<Vm>();

        for(Vm vm_choice: getVmCreatedList())
        {
            HashMap<User, Integer> myadjacency_list = ((SocialCloudlet)cloudlet).owner.adjacency_map;
            if( myadjacency_list.get( ((SocialHost)(vm_choice.getHost())).owner) == ((SocialCloudlet)cloudlet).securityLevel ) {
                temp_vm_list.add(vm_choice);
            }
        }
        for(Vm vm_choice: getVmCreatedList())
        {
            HashMap<User, Integer> myadjacency_list = ((SocialCloudlet)cloudlet).owner.adjacency_map;
            if( myadjacency_list.get( ((SocialHost)(vm_choice.getHost())).owner) < ((SocialCloudlet)cloudlet).securityLevel && myadjacency_list.get( ((SocialHost)(vm_choice.getHost())).owner) != 0) {
                temp_vm_list.add(vm_choice);
            }
        }
        for(Vm vm_choice: getVmCreatedList())
        {
            HashMap<User, Integer> myadjacency_list = ((SocialCloudlet)cloudlet).owner.adjacency_map;
            if( myadjacency_list.get( ((SocialHost)(vm_choice.getHost())).owner) == 0) {
                temp_vm_list.add(vm_choice);
            }
        }
        //Collections.reverse(temp_vm_list);

        System.out.println(temp_vm_list);

        final Vm mappedVm = temp_vm_list
            .stream()
            .filter(vm -> vm.getExpectedFreePesNumber() >= cloudlet.getNumberOfPes())
            .min(Comparator.comparingLong(Vm::getExpectedFreePesNumber))
            .orElse(Vm.NULL);


        //System.out.println(mappedVm.getCloudletScheduler().getCloudletList());

        if (mappedVm == Vm.NULL) {
            LOGGER.warn("{}: {}: {} (PEs: {}) couldn't be mapped to any suitable VM.",
                getSimulation().clockStr(), getName(), cloudlet, cloudlet.getNumberOfPes());
        } else {
            LOGGER.trace("{}: {}: {} (PEs: {}) mapped to {} (available PEs: {}, tot PEs: {})",
                getSimulation().clockStr(), getName(), cloudlet, cloudlet.getNumberOfPes(), mappedVm,
                mappedVm.getExpectedFreePesNumber(), mappedVm.getFreePesNumber());
        }

        return mappedVm;
    }
}
