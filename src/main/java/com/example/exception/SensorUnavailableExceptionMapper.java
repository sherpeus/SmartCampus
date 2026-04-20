/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.exception;
import com.example.model.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
/**
 *
 * @author lenovo
 */
@Provider
public class SensorUnavailableExceptionMapper  implements ExceptionMapper<SensorUnavailableException> {
    
@Override
public Response toResponse(SensorUnavailableException exception) {
    
        ErrorMessage errorMessage = new ErrorMessage("The intended sensor is currrently under Maintenance and won't be accepting new readings in the mean time",403);
        return Response.status(403).entity(errorMessage).build();
}
    
}
/*

{
    "id":"TEMP-001",
    "timestamp":123,
    "value": 10

}

{
    "id":"TEMP-001",
    "type":"Temperature",
    "status": "ACTIVE",
    "currentValue":111,
    "roomId":11

}
{
    "id":"11",
    "name":"fk",
    "capacity": 45

}
*/