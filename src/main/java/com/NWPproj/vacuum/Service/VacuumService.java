package com.NWPproj.vacuum.Service;

import com.NWPproj.vacuum.DAO.IErrorMessageRepository;
import com.NWPproj.vacuum.DAO.IVacuumRepository;
import com.NWPproj.vacuum.DAO.VacuumRepoNOJPA.IVacuumRepo;
import com.NWPproj.vacuum.Model.DataTypes;
import com.NWPproj.vacuum.Model.ErrorMessageDTO;
import com.NWPproj.vacuum.Model.Vacuum.VacuumDTO;
import com.NWPproj.vacuum.Model.Vacuum.VacuumSimple;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VacuumService implements IVacuumService{
    @Qualifier("IVacuumRepository")
    private final IVacuumRepository vacuumRepository;

    @Qualifier("IErrorMessageRepository")
    private final IErrorMessageRepository errorMessageRepository;

    @Qualifier("secondVacuumRepo")
    private final IVacuumRepo vacuumRepo;

    @Autowired
    private TaskScheduler taskScheduler;

    @Override
    public VacuumDTO NewVacuum(VacuumSimple request) {
        var vacuum = VacuumDTO.builder()
                .status(0)
                .active(true)
                .addedBy(request.getAddedBy())
                .name(request.getName())
                .dateAdded(LocalDateTime.now())
                .build();

        return vacuumRepository.save(vacuum);
    }

    @Transactional(readOnly = true)
    public List<VacuumDTO> searchVacuums(int userId, String name, List<DataTypes.StatusEnum> status, LocalDate dateFrom, LocalDate dateTo) {
        return vacuumRepository.search(name, status, dateFrom, dateTo); //dodati userID
    }

    public int startVacuum(int vacuumId) {
        VacuumDTO vacuum = vacuumRepository.findById(vacuumId)
                .orElseThrow(() -> new RuntimeException("Vacuum not found"));

        if (vacuum.getStatus() != DataTypes.StatusEnum.OFF.getValue()) {
            throw new RuntimeException("Vacuum must be in STOPPED state to start.");
        }

        int delayMillis = 15000 + new Random().nextInt(5000); // 15s + random deviation
        int remainingTimeSeconds = delayMillis / 1000;

        // Schedule the task
        taskScheduler.schedule(() -> {
            vacuum.setStatus(DataTypes.StatusEnum.ON.getValue());
            vacuumRepository.save(vacuum);
        }, new Date(System.currentTimeMillis() + delayMillis));

        return remainingTimeSeconds;
    }

    public int stopVacuum(int vacuumId) {
        VacuumDTO vacuum = vacuumRepository.findById(vacuumId)
                .orElseThrow(() -> new RuntimeException("Vacuum not found"));

        if (vacuum.getStatus() != DataTypes.StatusEnum.ON.getValue()) {
            throw new RuntimeException("Vacuum must be in RUNNING state to stop.");
        }

        int delayMillis = 15000 + new Random().nextInt(5000); // 15s + random deviation
        int remainingTimeSeconds = delayMillis / 1000;

        // Schedule the task
        taskScheduler.schedule(() -> {
            vacuum.setStatus(DataTypes.StatusEnum.OFF.getValue());
            vacuumRepository.save(vacuum);
        }, new Date(System.currentTimeMillis() + delayMillis));

        return remainingTimeSeconds;
    }

    public int dischargeVacuum(int vacuumId) {
        VacuumDTO vacuum = vacuumRepository.findById(vacuumId)
                .orElseThrow(() -> new RuntimeException("Vacuum not found"));

        if (vacuum.getStatus() != DataTypes.StatusEnum.OFF.getValue()) {
            throw new RuntimeException("Vacuum must be in STOPPED state to start.");
        }

        int delayMillis = 30000 + new Random().nextInt(5000); // 30s + random deviation
        int halfDelayMillis = delayMillis / 2;
        int remainingTimeSeconds = delayMillis / 1000;

        // Schedule the first part of the task
        taskScheduler.schedule(() -> {
            vacuum.setStatus(DataTypes.StatusEnum.DISCHARGING.getValue());
            vacuumRepository.save(vacuum);

            // Schedule the second part of the task
            taskScheduler.schedule(() -> {
                vacuum.setStatus(DataTypes.StatusEnum.OFF.getValue());
                vacuumRepository.save(vacuum);
            }, new Date(System.currentTimeMillis() + halfDelayMillis));
        }, new Date(System.currentTimeMillis() + halfDelayMillis));

        return remainingTimeSeconds;
    }

    public void scheduleOperation(int vacuumId, String operation, LocalDateTime scheduledTime) {
        Runnable task = () -> {
            try {
                switch (operation.toUpperCase()) {
                    case "START":
                        startVacuum(vacuumId);
                        break;
                    case "STOP":
                        stopVacuum(vacuumId);
                        break;
                    case "DISCHARGE":
                        dischargeVacuum(vacuumId);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown operation: " + operation);
                }
            } catch (Exception e) {
                ErrorMessageDTO errorMessage = ErrorMessageDTO.builder()
                        .vacuumId(vacuumId)
                        .operation(operation)
                        .errorMessage(e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build();
                errorMessageRepository.save(errorMessage);
            }
        };

        taskScheduler.schedule(task, Date.from(scheduledTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Transactional
    public void removeVacuum(int vacuumId) {
        VacuumDTO vacuum = vacuumRepository.findById(vacuumId)
                .orElseThrow(() -> new RuntimeException("Vacuum not found"));

        if (vacuum.getStatus() != DataTypes.StatusEnum.OFF.getValue()) {
            throw new IllegalStateException("Vacuum must be in STOPPED state to remove.");
        }

        vacuum.setActive(false);
        vacuumRepository.save(vacuum);
    }

}
