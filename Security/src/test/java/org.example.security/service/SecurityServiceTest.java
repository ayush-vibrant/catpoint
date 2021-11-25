package org.example.security.service;

import org.example.image.service.ImageService;
import org.example.security.data.AlarmStatus;
import org.example.security.data.ArmingStatus;
import org.example.security.data.SecurityRepository;
import org.example.security.data.Sensor;
import org.example.security.data.SensorType;
import org.example.security.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {
    private SecurityService securityService;

    private Sensor sensor;

    private final String sensorName = UUID.randomUUID().toString();

    @Mock
    private SecurityRepository securityRepository;

    @Mock
    private ImageService imageService;

    private Set<Sensor> getAllSensors(int count, boolean status){
        Set<Sensor> sensors = new HashSet<>();
        for (int i = 0; i < count; i++) {
            Sensor sensor = new Sensor(UUID.randomUUID().toString(), SensorType.DOOR);
            sensor.setActive(status);

            sensors.add(sensor);
        }

        return sensors;
    }

    @BeforeEach
    void init() {
        securityService = new SecurityService(securityRepository, imageService);
        sensor = new Sensor(sensorName, SensorType.DOOR);
    }

    @Test
    void if_armedAlarmStatus_and_activatedSensor_then_put_system_into_pendingAlarmStatus() {
        Mockito.when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME); // TODO: Why am I not able to write when() instead of Mockito.when()
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);

        Mockito.verify(securityRepository, Mockito.times(1)).setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

    @Test
    void if_armedAlarm_activatedSensor_pendingAlarm_then_alarmStatus() {
        Mockito.when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);

        Mockito.verify(securityRepository, Mockito.times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void if_pendingAlarm_inactiveAllSensors_then_noAlarmStatus() {
        Mockito.when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, false);

        Mockito.verify(securityRepository, Mockito.times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }


}