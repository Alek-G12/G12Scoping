package com.g12.scoping.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Inspection extends RealmObject {
    
    @PrimaryKey
    private String id;
    private String name;
    private String equipmentType;
    private Date dateCreated;
    private String createdBy;
    private Date dateModified;
    private String modifiedBy;
    private RealmList<Section> sections;
    
    public Inspection(){}
    
    public Inspection(String id, String equipmentType, String name, Date dateCreated,
                      String createdBy){
        this.id = id;
        this.equipmentType = equipmentType;
        this.name = name;
        this.dateCreated = dateCreated;
        this.createdBy = createdBy;
        //First modification == Creation
        this.dateModified = dateCreated;
        this.modifiedBy = createdBy;
    }
    
    public String getEquipmentType(){
        return equipmentType;
    }
    
    public String getName(){
        return name;
    }
    
    public Date getDateCreated(){
        return dateCreated;
    }
    
    public String getCreatedBy(){
        return createdBy;
    }
    
    public Date getDateModified(){
        return dateModified;
    }
    
    public void setDateModified(Date dateModified){
        this.dateModified = dateModified;
    }
    
    public String getModifiedBy(){
        return modifiedBy;
    }
    
    public void setModifiedBy(String modifiedBy){
        this.modifiedBy = modifiedBy;
    }
    
    public RealmList<Section> getSections(){
        return sections;
    }
    
    public void setSections(RealmList<Section> sections){
        this.sections = sections;
    }
}
