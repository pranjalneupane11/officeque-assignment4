package edu.ysu.officeque.repository;

import edu.ysu.officeque.domain.OfficeHourSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeHourSessionRepository extends JpaRepository<OfficeHourSession, Long> { }
