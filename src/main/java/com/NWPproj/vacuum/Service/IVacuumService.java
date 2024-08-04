package com.NWPproj.vacuum.Service;

import com.NWPproj.vacuum.Model.DataTypes;
import com.NWPproj.vacuum.Model.Vacuum.VacuumDTO;
import com.NWPproj.vacuum.Model.Vacuum.VacuumSimple;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IVacuumService {
    VacuumDTO NewVacuum(VacuumSimple request);

    List<VacuumDTO> searchVacuums(int userId, String name, List<DataTypes.StatusEnum> status, LocalDate dateFrom, LocalDate dateTo);

    int startVacuum(int vacuumId);

    int stopVacuum(int vacuumId);

    int dischargeVacuum(int vacuumId);

    void removeVacuum(int vacuumId);

    void scheduleOperation(int vacuumId, String operation, LocalDateTime time);
}
