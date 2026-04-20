/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;
import com.example.model.Sensor;
import com.example.model.SensorReading;
import com.example.dao.Database;
import com.example.dao.GenericDAO;
import com.example.exception.LinkedResourceNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.example.exception.SensorUnavailableException;
/**
 *
 * @author lenovo
 */
public class SensorReadingResource {
    
    private String sensorId;
    //public static final HashMap<Sensor,ArrayList<SensorReading>> SENSOR_READINGS = new HashMap<>();
    private HashMap<Sensor,ArrayList<SensorReading>> SensorReadings=Database.SENSOR_READINGS;
    private GenericDAO<Sensor> sensorDAO=new GenericDAO<>(Database.SENSORS);

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        Sensor current=sensorDAO.getById(this.sensorId);
        ArrayList<SensorReading> sensorData=SensorReadings.getOrDefault(current, new ArrayList<SensorReading>());
        return Response.ok(sensorData).build();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading newReading) {

        //newReading.setId(this.sensorId);
        
        
        
        Sensor current=sensorDAO.getById(this.sensorId);
        if(current==null){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if(current.getStatus().equalsIgnoreCase("MAINTENANCE")){
           
            throw new SensorUnavailableException("The intended sensor is currrently under Maintenance and won't be accepting new readings in the mean time");
            
        }
        current.setCurrentValue(newReading.getValue());
        //private HashMap<Sensor,ArrayList<SensorReading>> SensorReadings=Database.SENSOR_READINGS;
        SensorReadings.get(current).add(newReading);
        /*ArrayList<SensorReading> cur=SensorReadings.get(current);
        for(SensorReading c:cur){
            if(c.getId().equals(newReading.getId())){
                return Response.status(Response.Status.CONFLICT)
                       .entity("The sensor reading id is already in use!")
                       .build();
            }
        }*/
        return Response.status(Response.Status.CREATED)
                       .entity("Reading logged for sensor " + this.sensorId)
                       .build();
    
    }
   
}
