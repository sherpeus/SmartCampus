/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;
import com.example.dao.GenericDAO;
import com.example.dao.Database;
import com.example.exception.LinkedResourceNotFoundException;
import com.example.model.Room;
import com.example.model.Sensor;
import com.example.model.SensorReading;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
/**
 *
 * @author lenovo
 */
@Path("/sensors")
public class SensorResource {
    
    private GenericDAO<Room> roomDAO = new GenericDAO<>(Database.ROOMS);
    private HashMap<Room,ArrayList<Sensor>>roomSensor=Database.ROOM_SENSOR;
    private GenericDAO<Sensor> sensorDAO=new GenericDAO<>(Database.SENSORS);
    private HashMap<Sensor,ArrayList<SensorReading>> SensorReadings=Database.SENSOR_READINGS;
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewSensor(Sensor sensor) {
    
    
        String roomId = sensor.getRoomId(); 


        Room room = roomDAO.getById(roomId);
        if (room == null) {
            throw new LinkedResourceNotFoundException("\"No such room with id = : " + roomId+" exists\"");
        }


        if (sensorDAO.getById(sensor.getId()) != null) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"Sensor ID already exists.\"}").build();
        }


        sensorDAO.add(sensor);
        SensorReadings.putIfAbsent(sensor, new ArrayList<SensorReading>());
        List cur=room.getSensorIds();
        cur.add(sensor.getId());
        room.setSensorIds(cur);
        if (roomSensor.containsKey(room)) {
            roomSensor.get(room).add(sensor);
        }

        return Response.status(Response.Status.CREATED).entity(sensor).build();
        
            }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(sensorDAO.getAll());
        if (type != null && !type.isEmpty()) {
            List<Sensor> filteredSensors = new ArrayList<>();
            for (Sensor s : allSensors) {
                if (s.getType().equalsIgnoreCase(type)) {
                    filteredSensors.add(s);
                }
            }
            return Response.ok(filteredSensors).build();
        }

        return Response.ok(allSensors).build();
    }
    
    @Path("{sensorId}/readings")
    public SensorReadingResource getReadingResource(@PathParam("sensorId") String id) {
        return new SensorReadingResource(id); 
    }
}