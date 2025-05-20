package com.hospital.energymgmt.repository;

import com.hospital.energymgmt.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByFloor(String floor);
    List<Room> findByDepartment(String department);
}
