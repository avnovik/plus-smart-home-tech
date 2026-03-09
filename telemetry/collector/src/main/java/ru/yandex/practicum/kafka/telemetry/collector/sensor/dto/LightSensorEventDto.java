package ru.yandex.practicum.kafka.telemetry.collector.sensor.dto;

/**
 * DTO события датчика освещённости.
 */
public class LightSensorEventDto extends SensorEventDto {

    private Integer linkQuality;

    private Integer luminosity;

    public Integer getLinkQuality() {
        return linkQuality;
    }

    public void setLinkQuality(Integer linkQuality) {
        this.linkQuality = linkQuality;
    }

    public Integer getLuminosity() {
        return luminosity;
    }

    public void setLuminosity(Integer luminosity) {
        this.luminosity = luminosity;
    }
}
