package com.guneet.atb.dtos.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceDTO {
    @NotBlank(message = "Device ID cannot be null")
    private String id;

    @NotBlank(message = "Device type cannot be blank")
    private String type;

    @JsonIgnore
    private String swsVersion;
    @JsonIgnore
    private String serialNum;

    @Override
    public String toString() {
        return "DeviceDTO{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", swsVersion='" + swsVersion + '\'' +
                ", serialNum='" + serialNum + '\'' +
                '}';
    }

    @JsonIgnore
    public boolean isEnvoy() {
        return "ENVOY".equalsIgnoreCase(this.type);
    }
}

