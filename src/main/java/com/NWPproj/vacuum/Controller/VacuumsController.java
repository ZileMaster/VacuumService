package com.NWPproj.vacuum.Controller;


import com.NWPproj.vacuum.Model.DataTypes;
import com.NWPproj.vacuum.Model.OperationResponse;
import com.NWPproj.vacuum.Model.Vacuum.VacuumDTO;
import com.NWPproj.vacuum.Model.Vacuum.VacuumSimple;
import com.NWPproj.vacuum.Service.IVacuumService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vacuum")
@RequiredArgsConstructor
public class VacuumsController {

    private final IVacuumService vacuumService;

    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> testAPI() {
        Map<String, String> response = new HashMap<>();
        response.put("response", "Pong");

        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<VacuumDTO> AddVacuum(@RequestBody VacuumSimple request)
    {
        VacuumDTO vacuum = vacuumService.NewVacuum(request);

        if(vacuum == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new VacuumDTO());
        }

        return ResponseEntity.ok(vacuum);
    }

    @GetMapping("/search")
    public ResponseEntity<List<VacuumDTO>> searchVacuums(
            @RequestParam(value = "userId", defaultValue = "-1") int userId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "status", required = false) String status, // Koristi String i kasnije pretvori
            @RequestParam(value = "dateFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(value = "dateTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo
    ) {
        List<DataTypes.StatusEnum> statusEnumList = status != null ?
                Arrays.stream(status.split(","))
                        .map(DataTypes.StatusEnum::valueOf)
                        .collect(Collectors.toList()) :
                null;

        List<VacuumDTO> vacuums = vacuumService.searchVacuums(userId, name, statusEnumList, dateFrom, dateTo);
        return ResponseEntity.ok(vacuums);
    }

    @PostMapping("/start")
    public ResponseEntity<OperationResponse> startVacuum(@RequestParam("vacuumId") int vacuumId) {
        int remainingTime = vacuumService.startVacuum(vacuumId);
        return ResponseEntity.ok(new OperationResponse("Start operation initiated.", remainingTime));
    }

    @PostMapping("/stop")
    public ResponseEntity<OperationResponse> stopVacuum(@RequestParam("vacuumId") int vacuumId) {
        int remainingTime = vacuumService.stopVacuum(vacuumId);
        return ResponseEntity.ok(new OperationResponse("Stop operation initiated.", remainingTime));
    }

    @PostMapping("/discharge")
    public ResponseEntity<OperationResponse> dischargeVacuum(@RequestParam("vacuumId") int vacuumId) {
        int remainingTime = vacuumService.dischargeVacuum(vacuumId);
        return ResponseEntity.ok(new OperationResponse("Discharge operation initiated.", remainingTime));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeVacuum(@RequestParam("vacuumId") int vacuumId) {
        try {
            vacuumService.removeVacuum(vacuumId);
            return ResponseEntity.ok("Vacuum removed successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred.");
        }
    }

    @PostMapping("/schedule")
    public ResponseEntity<String> scheduleOperation(
            @RequestParam("vacuumId") int vacuumId,
            @RequestParam("operation") String operation,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {
        vacuumService.scheduleOperation(vacuumId, operation, time);
        return ResponseEntity.ok("Operation scheduled successfully.");
    }
}
