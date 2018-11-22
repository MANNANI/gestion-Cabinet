/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author houda
 */
@Entity
public class AdminSecretaire extends Employe implements Serializable {

   
    @ManyToOne
    private Admin admin;

     public AdminSecretaire() {
        super();
    }
    
    

    

    public Admin getAdmin() {
        if(admin==null){
            admin=new Admin();
        }
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
    
    

  
    
}
