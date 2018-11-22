/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author houda
 */
@Entity
public class MargeBloquante extends Marge implements Serializable {

    
    
    
    
    
    @OneToMany(mappedBy = "margeBloquante")
    private List<MargeItem> margeItems;

    public MargeBloquante() {
        super();
    }

    
    public List<MargeItem> getMargeItems() {
        if(margeItems==null){
            margeItems=new ArrayList<>();
        }
        
        return margeItems;
    }

    public void setMargeItems(List<MargeItem> margeItems) {
        this.margeItems = margeItems;
    }

}
