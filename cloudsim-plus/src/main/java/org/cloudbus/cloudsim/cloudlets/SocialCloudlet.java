package org.cloudbus.cloudsim.cloudlets;

import org.cloudbus.cloudsim.user.User;
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModel;

//Written by Kaiyi :)
public class SocialCloudlet extends CloudletSimple {


	public int socialId;
	public User owner;
	public int securityLevel;

	public SocialCloudlet(final long length, final int pesNumber, final UtilizationModel utilizationModel,
                          final int social_id,
                          final int security_level,
                          User owned) {
        super(length, pesNumber, utilizationModel);
		this.securityLevel = security_level;
		this.socialId = social_id;
		this.owner = owned;
	}

}
