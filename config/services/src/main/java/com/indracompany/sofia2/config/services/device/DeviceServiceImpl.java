package com.indracompany.sofia2.config.services.device;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.indracompany.sofia2.config.model.Device;
import com.indracompany.sofia2.config.repository.ClientPlatformRepository;
import com.indracompany.sofia2.config.repository.DeviceRepository;

@Service
public class DeviceServiceImpl implements DeviceService {

	@Autowired
	DeviceRepository deviceRepository;
	@Autowired
	ClientPlatformRepository clientPlatformRepository;

	@Override
	public List<Device> getAll() {
		return deviceRepository.findAll();
	}
	@Override
	public List<Device> getByClientPlatformId(String clientPlatformId) {

		final List<Device> devices = deviceRepository.findByClientPlatform(clientPlatformId);

		return devices;
	}


	@Override
	public void createDevice(Device device) {
		deviceRepository.save(device);
	}
	@Override
	public void updateDevice(Device device) {
		deviceRepository.save(device);
	}
	@Override
	@Transactional
	public int updateDeviceStatusAndDisableWhenUpdatedAtLessThanDate(boolean status, boolean disabled, Date date) {
		return deviceRepository.updateDeviceStatusByUpdatedAt(status, disabled, date);
	}
	@Override
	public List<Device> getByClientPlatformIdAndIdentification(String clientPlatformId, String identification) {
		return deviceRepository.findByClientPlatformAndIdentification(clientPlatformId, identification);
	}



}
