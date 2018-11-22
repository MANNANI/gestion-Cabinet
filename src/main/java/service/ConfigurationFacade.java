/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Configuration;
import bean.Medecin;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author houda
 */
@Stateless
public class ConfigurationFacade extends AbstractFacade<Configuration> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigurationFacade() {
        super(Configuration.class);
    }
    
    public Configuration findByMedecin(Medecin medecin){
        List<Configuration> configurations=em.createQuery("SELECT m.configuration FROM Medecin m WHERE m.id='"+medecin.getId()+"'").getResultList();
        return configurations.get(0);
    }
    
}
