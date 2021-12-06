package org.example.security.service;

import org.example.image.service.ImageService;
import org.example.security.data.AlarmStatus;
import org.example.security.data.ArmingStatus;
import org.example.security.data.SecurityRepository;
import org.example.security.data.Sensor;
import org.example.security.data.SensorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;

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
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

    @Test
    void if_armedAlarm_activatedSensor_pendingAlarm_then_alarmStatus() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void if_pendingAlarm_inactiveAllSensors_then_noAlarmStatus() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void if_activeAlarm_and_activatedSensor_or_deactivatedSensor_then_activeAlarm(boolean sensorStatus) {
        lenient().when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        lenient().when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
        securityService.changeSensorActivationStatus(sensor, sensorStatus);

        verify(securityRepository, never()).setAlarmStatus(any(AlarmStatus.class));
    }

    @Test
    void if_alreadyActiveSensor_activated_pendingAlarmState_then_alarmStatus() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, times(1)).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void if_alreadyDeactivatedSensor_deactivated_then_noAlarmStatus() {
        securityService.changeSensorActivationStatus(sensor, false);
        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository, never()).setAlarmStatus(any(AlarmStatus.class));
    }
}