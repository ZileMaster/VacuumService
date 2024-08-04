package com.NWPproj.vacuum.DAO;

import com.NWPproj.vacuum.Model.DataTypes;
import com.NWPproj.vacuum.Model.Vacuum.VacuumDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IVacuumRepository extends JpaRepository<VacuumDTO, Integer> {
    @Query("SELECT v FROM VacuumDTO v WHERE v.active = true " + //AND v.addedBy.id = :userId  DODATI TO
            "AND (:name IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:status IS NULL OR v.status IN :status) " +
            "AND (:dateFrom IS NULL OR v.dateAdded >= :dateFrom) " +
            "AND (:dateTo IS NULL OR v.dateAdded <= :dateTo)")
    List<VacuumDTO> search(
            @Param("name") String name,
            @Param("status") List<DataTypes.StatusEnum> status,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo
            //@Param("userId") Long userId,
    );
}
