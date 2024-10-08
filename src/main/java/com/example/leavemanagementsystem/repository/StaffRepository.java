package com.example.leavemanagementsystem.repository;

import com.example.leavemanagementsystem.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff,Long> {
    Optional<Staff> findByStaffId(String staffId);

    Optional<Staff> findByEmail(String email);

    @Query("SELECT s FROM Staff s WHERE s.department = :department AND s.lineManagerId = :lineManagerId")
    List<Staff> findByDepartmentAndLineManagerId(
            @Param("department") String department,
            @Param("lineManagerId") Long lineManagerId
    );
}
