package com.hospital.energymgmt.service;

import com.hospital.energymgmt.dto.RoomDto;
import com.hospital.energymgmt.model.Room;
import com.hospital.energymgmt.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // 将 Room 实体转换为 RoomDto
    private RoomDto convertToDto(Room room) {
        if (room == null) {
            return null;
        }
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setName(room.getName());
        dto.setFloor(room.getFloor());
        dto.setDepartment(room.getDepartment());
        dto.setDescription(room.getDescription());
        return dto;
    }

    // 将 RoomDto 转换为 Room 实体
    private Room convertToEntity(RoomDto roomDto) {
        if (roomDto == null) {
            return null;
        }
        Room room = new Room();
        room.setId(roomDto.getId()); // 通常在创建时id为null，更新时需要
        room.setRoomNumber(roomDto.getRoomNumber());
        room.setName(roomDto.getName());
        room.setFloor(roomDto.getFloor());
        room.setDepartment(roomDto.getDepartment());
        room.setDescription(roomDto.getDescription());
        return room;
    }

    @Transactional
    public RoomDto createRoom(RoomDto roomDto) {
        // 检查房间号是否已存在
        roomRepository.findByRoomNumber(roomDto.getRoomNumber()).ifPresent(r -> {
            throw new IllegalArgumentException("Room number already exists: " + r.getRoomNumber());
        });
        Room room = convertToEntity(roomDto);
        Room savedRoom = roomRepository.save(room);
        return convertToDto(savedRoom);
    }

    @Transactional(readOnly = true)
    public Page<RoomDto> getAllRooms(Pageable pageable) {
        Page<Room> roomPage = roomRepository.findAll(pageable);
        return roomPage.map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<RoomDto> getRoomById(Long id) {
        return roomRepository.findById(id).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<RoomDto> getRoomByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber).map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<RoomDto> getRoomsByFloor(String floor) {
        return roomRepository.findByFloor(floor).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoomDto> getRoomsByDepartment(String department) {
        return roomRepository.findByDepartment(department).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomDto updateRoom(Long id, RoomDto roomDto) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));

        // 检查更新的房间号是否与现有其他房间冲突
        if (roomDto.getRoomNumber() != null && !roomDto.getRoomNumber().equals(existingRoom.getRoomNumber())) {
            roomRepository.findByRoomNumber(roomDto.getRoomNumber()).ifPresent(r -> {
                if (!r.getId().equals(id)) { // 确保不是当前正在更新的房间本身
                    throw new IllegalArgumentException("Room number already exists: " + r.getRoomNumber());
                }
            });
            existingRoom.setRoomNumber(roomDto.getRoomNumber());
        }

        if (roomDto.getName() != null) {
            existingRoom.setName(roomDto.getName());
        }
        if (roomDto.getFloor() != null) {
            existingRoom.setFloor(roomDto.getFloor());
        }
        if (roomDto.getDepartment() != null) {
            existingRoom.setDepartment(roomDto.getDepartment());
        }
        if (roomDto.getDescription() != null) {
            existingRoom.setDescription(roomDto.getDescription());
        }

        Room updatedRoom = roomRepository.save(existingRoom);
        return convertToDto(updatedRoom);
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new IllegalArgumentException("Room not found with id: " + id);
        }
        // 在删除房间之前，可能需要处理与该房间关联的设备 (例如，将设备的roomId设为null)
        // 这个逻辑可以根据具体需求添加，例如通过DeviceService来处理
        roomRepository.deleteById(id);
    }
}
