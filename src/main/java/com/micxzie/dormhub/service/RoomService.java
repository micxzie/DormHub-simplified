package com.micxzie.dormhub.service;

import com.micxzie.dormhub.model.Room;
import com.micxzie.dormhub.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {
    
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms(){
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id){
        return roomRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    public Room createRoom(Room room){
        // Business rule: no two rooms can share a room number
        // this is my attempt to do the business rule that I think should be necessary
        if(roomRepository.findByRoomNumber(room.getRoomNumber()).isPresent()){
            throw new RuntimeException("Room number is already in use: " + room.getRoomNumber());
        }
        if(room.getCapacity() < 1 && room.getCapacity() <= 5){
            throw new RuntimeException("Capacity must atleast be 1 and not greater than 5");
        }
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room updatedRoom){
        if(updatedRoom.getCapacity() < 1){
        throw new RuntimeException("Capacity must be at least 1");
        }

        Room existingRoom = getRoomById(id);
        existingRoom.setRoomNumber(updatedRoom.getRoomNumber());
        existingRoom.setCapacity(updatedRoom.getCapacity());
        existingRoom.setMonthlyRate(updatedRoom.getMonthlyRate());
        existingRoom.setStatus(updatedRoom.getStatus());

        return roomRepository.save(existingRoom);
    }

    public void deleteRoom(Long id){
        Room existingRoom = getRoomById(id);
        roomRepository.delete(existingRoom);
    }
}
