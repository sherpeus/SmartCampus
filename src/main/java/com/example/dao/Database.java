/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.dao;

import java.util.ArrayList;
import java.util.List;
import com.example.model.Room;
import com.example.model.Sensor;
import com.example.model.SensorReading;
import java.util.HashMap;

/**
 *
 * @author lenovo
 */
public class Database {
    public static final List<Room> ROOMS = new ArrayList<>();
    public static final List<Sensor> SENSORS = new ArrayList<>();
    public static final HashMap<Sensor,ArrayList<SensorReading>> SENSOR_READINGS = new HashMap<>();
    public static final HashMap<Room,ArrayList<Sensor>> ROOM_SENSOR=new HashMap<>();
    
}
