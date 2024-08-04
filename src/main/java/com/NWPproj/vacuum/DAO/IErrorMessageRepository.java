package com.NWPproj.vacuum.DAO;

import com.NWPproj.vacuum.Model.ErrorMessageDTO;
import com.NWPproj.vacuum.Model.Vacuum.VacuumDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IErrorMessageRepository extends JpaRepository<ErrorMessageDTO, Integer> {
}
