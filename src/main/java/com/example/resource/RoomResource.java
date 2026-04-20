/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.resource;
import com.example.dao.GenericDAO;
import com.example.dao.Database;
import com.example.model.Sensor;
import com.example.model.Room;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import com.example.exception.*;
/**
 *
 * @author lenovo
 */
@Path("/rooms")
public class RoomResource {
    //PART 2 GET
    private GenericDAO<Room> roomDAO = new GenericDAO<>(Database.ROOMS);
    private HashMap<Room,ArrayList<Sensor>>roomSensor=Database.ROOM_SENSOR;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        List<Room> rooms = roomDAO.getAll();
        Map<String, Object> roomCollection = new HashMap<>();
        roomCollection.put("rooms",rooms);
        return Response.ok(roomCollection).build();
    }
    
    //part 2 POST
    @Context
    private UriInfo uriInfo;
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addNewRoom(Room room){
        if (roomDAO.getById(room.getId()) != null) {
            return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"Room ID already exists.\"}").build();
        }
        roomDAO.add(room);
        roomSensor.putIfAbsent(room, new ArrayList<Sensor>());
        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location).build();
    }
    
    //PART 2 GET BY ID
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId){
        Room room=roomDAO.getById(roomId);
        if(room==null){
            return Response.status(409).entity("{\"error\": \"Room not found\"}").build();
        }
        return Response.ok(room).build();
    }
    
    // PART 2 DELETE
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoomById(@PathParam("roomId") String roomId){
        Room room=roomDAO.getById(roomId);
        if(room==null){
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\": \"Room not found\"}").build();
        }
        boolean hasSensors=!roomSensor.get(room).isEmpty();
        if(hasSensors){
            //throw an error here
            throw new RoomNotEmptyException("\"Active hardware detected in room: \"" + roomId);
            //System.out.println("Error should hv been returned :D");
            //return Response.status(Response.Status.CONFLICT).entity("{\"error\": \"Cannot delete room: It contains active sensors.\"}").build();
        }
        roomDAO.delete(roomId);
        roomSensor.remove(room);
        return Response.noContent().build();
    }
}
