/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.MargeItem;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author houda
 */
@Stateless
public class MargeItemFacade extends AbstractFacade<MargeItem> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MargeItemFacade() {
        super(MargeItem.class);
    }
    
}
