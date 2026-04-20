/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.model;

/**
 *
 * @author lenovo
 */
public class ErrorMessage {
    
    private String errorMessage;
    private int errorCode;
    

    public ErrorMessage() {}

    public ErrorMessage(String errorMessage, int errorCode) {
        
    this.errorMessage = errorMessage;
    this.errorCode = errorCode;
   

    }

    // Getters and Setters
    public String getErrorMessage() { 
        return errorMessage;
    
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage =errorMessage;
    }

    public int getErrorCode() { 
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

   
    
}
