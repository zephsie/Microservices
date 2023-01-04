package com.zephsie.report.feign;

import com.fasterxml.jackson.annotation.JsonView;
import com.zephsie.report.dtos.JournalDTO;
import com.zephsie.report.utils.views.EntityView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.UUID;

@FeignClient(name = "fitness-server")
public interface JournalService {

    @GetMapping(value = "/api/journal/between", produces = "application/json")
    @JsonView(EntityView.WithMappings.class)
    Collection<JournalDTO> read(@RequestParam(value = "dt_supply_start") long dtSupplyStart,
                                @RequestParam(value = "dt_supply_end") long dtSupplyEnd,
                                @RequestHeader("USER_ID") UUID userId);
}