package com.guneet.atb.model.rds;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.guneet.atb.dtos.request.ATBCreateDTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Slf4j
@Builder
@Table("atb")
public class LegacyATB implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String params;
    

    public boolean isEmpty() {
        return this.id == null;
    }

    public LegacyATB(Map<String, Object> requestHeader, ATBCreateDTO atbCreateDTO){
        this.params = atbCreateDTO.getTaskParams();
    }

}
