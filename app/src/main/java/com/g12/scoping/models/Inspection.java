package com.g12.scoping.models;

import io.realm.RealmObject;

public class Inspection extends RealmObject {
    public static final int FILTER = 0, EXCHANGER = 1, AIR_RECEIVER = 2, VESSEL = 3, TANK = 4, PIPE = 5;
    
    private long uniqueId;
    private int equipmentType;
    private String name;
    private String dateCreated;
    private String createdBy;
    private String dateModified;
    private String modifiedBy;
    
    public Inspection(){ }
    
    public Inspection(int equipmentType, String name, String dateCreated, String createdBy){
        this.uniqueId = System.currentTimeMillis();
        this.equipmentType = equipmentType;
        this.name = name;
        this.dateCreated = dateCreated;
        this.createdBy = createdBy;
        //First modification == Creation
        this.dateModified = dateCreated;
        this.modifiedBy = createdBy;
    }
    
    public int getEquipmentType(){
        return equipmentType;
    }
    
    public long getUniqueId(){ return uniqueId;}
    
    public String getName(){
        return name;
    }
    
    public String getDateCreated(){
        return dateCreated;
    }
    
    public String getCreatedBy(){
        return createdBy;
    }
    
    public String getDateModified(){
        return dateModified;
    }
    
    public String getModifiedBy(){
        return modifiedBy;
    }
    
    
    public void setDateModified(String dateModified){
        this.dateModified = dateModified;
    }
    
    public void setModifiedBy(String modifiedBy){
        this.modifiedBy = modifiedBy;
    }
    
    
    public static int parseEquipmentType(String type) throws Exception{
        switch(type){
            case "Filter":
                return Inspection.FILTER;
            case "Exchanger":
                return Inspection.EXCHANGER;
            case "Air Receiver":
                return Inspection.AIR_RECEIVER;
            case "Vessel":
                return Inspection.VESSEL;
            case "Tank":
                return Inspection.TANK;
            case "Pipe":
                return Inspection.PIPE;
            default:
                throw new Exception("Wrong Equipment Type");
        }
    }
    
    public static String parseEquipmentType(int type) throws Exception{
        switch(type){
            case FILTER:
                return "Filter";
            case EXCHANGER:
                return "Exchanger";
            case AIR_RECEIVER:
                return "Air Receiver";
            case VESSEL:
                return "Vessel";
            case TANK:
                return "Tank";
            case PIPE:
                return "Pipe";
            default:
                throw new Exception("Wrong Equipment Type");
        }
    }
    
}
