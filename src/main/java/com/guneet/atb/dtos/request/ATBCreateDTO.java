package com.guneet.atb.dtos.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ATBCreateDTO {

    @Schema(example = "model1")
    private String taskParams;

    @Schema(hidden = true)
    private Integer complete;

    @Schema(example = "2")
    private Integer taskTypeId;

    @Schema(example = "1708349855")
    private Long runAfter;

    @NotNull(message = "Device cannot be null")
    @Schema(example = "{\n" +
            "        \"id\": \"1059562817\",\n" +
            "        \"type\": \"ENVOY\"\n" +
            "\n" +
            "    }")

    @Valid
    private DeviceDTO device;


    @JsonIgnore
    public String getDeviceId() {
        return this.device.getId();
    }

    @JsonIgnore
    public String getDeviceType() {
        return this.device.getType();
    }
    
}
