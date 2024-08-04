package com.NWPproj.vacuum.DAO.VacuumRepoNOJPA;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository("secondVacuumRepo")
@RequiredArgsConstructor
public class VacuumRepo implements IVacuumRepo{

    private final EntityManager entityManager;
}
