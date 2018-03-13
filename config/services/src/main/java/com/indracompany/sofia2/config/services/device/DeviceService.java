package com.indracompany.sofia2.config.services.device;

import java.util.Date;
import java.util.List;

import com.indracompany.sofia2.config.model.Device;

public interface DeviceService {

	public List<Device> getByClientPlatformId(String identification);

	public List<Device>  getByClientPlatformIdAndIdentification(String clientPlatformId, String identification);

	public void createDevice(Device device);

	public void updateDevice(Device device);

	public List<Device> getAll();

	int updateDeviceStatusAndDisableWhenUpdatedAtLessThanDate(boolean status, boolean disabled, Date date);
}
