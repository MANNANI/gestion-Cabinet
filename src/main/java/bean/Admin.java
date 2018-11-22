/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.io.Serializable;
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
public class Admin extends Employe implements Serializable {

    @OneToMany(mappedBy = "admin")
    private List<AdminSecretaire> adminSecretaires;

    
    public Admin() {
        super();
    }

    public List<AdminSecretaire> getAdminSecretaires() {
        return adminSecretaires;
    }

    public void setAdminSecretaires(List<AdminSecretaire> adminSecretaires) {
        this.adminSecretaires = adminSecretaires;
    }
    
    
   
}
