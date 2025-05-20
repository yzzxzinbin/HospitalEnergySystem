package com.hospital.energymgmt.controller;

import com.hospital.energymgmt.dto.RoomDto;
import com.hospital.energymgmt.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomDto roomDto) {
        try {
            RoomDto createdRoom = roomService.createRoom(roomDto);
            return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Or a more specific error response
        }
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        List<RoomDto> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-room-number/{roomNumber}")
    public ResponseEntity<RoomDto> getRoomByRoomNumber(@PathVariable String roomNumber) {
        return roomService.getRoomByRoomNumber(roomNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-floor/{floor}")
    public ResponseEntity<List<RoomDto>> getRoomsByFloor(@PathVariable String floor) {
        List<RoomDto> rooms = roomService.getRoomsByFloor(floor);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/by-department/{department}")
    public ResponseEntity<List<RoomDto>> getRoomsByDepartment(@PathVariable String department) {
        List<RoomDto> rooms = roomService.getRoomsByDepartment(department);
        return ResponseEntity.ok(rooms);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @RequestBody RoomDto roomDto) {
        try {
            RoomDto updatedRoom = roomService.updateRoom(id, roomDto);
            return ResponseEntity.ok(updatedRoom);
        } catch (IllegalArgumentException e) {
            // Could be due to room not found or room number conflict
            return ResponseEntity.badRequest().build(); // Or a more specific error response
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
