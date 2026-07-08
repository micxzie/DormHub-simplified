package com.micxzie.dormhub.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "room")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false, unique = true, length = 10)
    private String roomNumber;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyRate;

    @Column(nullable = false, length = 15)
    private String status = "VACANT";

    // --- Constructors ---

    public Room(){
        // JPA doesn't require constructor
    }

    // --- Getters and Setters ---

    public Long getRoomId(){
        return roomId;
    }

    public void setRoomId(Long roomId){
        this.roomId = roomId;
    }

    public String getRoomNumber(){
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber){
        this.roomNumber = roomNumber;
    }

    public int getCapacity(){
        return capacity;
    }

    public void setCapacity(int capacity){
        this.capacity = capacity;
    }

    public BigDecimal getMonthlyRate(){
        return monthlyRate; 
    }

    public void setMonthlyRate(BigDecimal monthlyRate){
        this.monthlyRate = monthlyRate;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }
}
