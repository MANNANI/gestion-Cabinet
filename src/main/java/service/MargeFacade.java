/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Marge;
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
public class MargeFacade extends AbstractFacade<Marge> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MargeFacade() {
        super(Marge.class);
    }

    public void deleteMarges(Medecin get) {
List<Marge> marges=em.createQuery("select m FROM Marge m WHERE m.medecin.id='"+get.getId()+"'").getResultList();
        for (Marge marge : marges) {
            remove(marge);
        }
    }
    
}
