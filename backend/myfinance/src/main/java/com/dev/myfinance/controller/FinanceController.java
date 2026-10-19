package com.dev.myfinance.controller;

import com.dev.myfinance.dto.request.FinanceRequest;
import com.dev.myfinance.dto.response.FinancePageResponse;
import com.dev.myfinance.service.FinanceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/finance")
public class FinanceController {

    private final FinanceService service;

    public FinanceController(FinanceService service) {
        this.service = service;
    }

    @PostMapping("/insert")
    public ResponseEntity<Void> createFinance(@RequestBody @Valid FinanceRequest request) {
        Long id = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .replacePath("/finance/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }


    @GetMapping("/getAll")
    public ResponseEntity<FinancePageResponse> getAllFinances(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        FinancePageResponse response = service.getAllFinances(
                category,
                startDate,
                endDate,
                tagIds,
                page,
                size);
        return ResponseEntity.ok(response);
    }

}
