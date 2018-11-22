/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Medecin;
import bean.NoteService;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author houda
 */
@Stateless
public class NoteServiceFacade extends AbstractFacade<NoteService> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NoteServiceFacade() {
        super(NoteService.class);
    }
   
     public List<NoteService> findNoteByMedecin(Medecin medecin ) {
        String requet = "SELECT n FROM NoteService n WHERE 1=1 ";
        if(medecin!=null){
            requet+=" AND n.medecin.id='" + medecin.getId()+"'";
        }
        return getEntityManager().createQuery(requet).getResultList();
    }

    public void deleteNotes(Medecin get) {
        List<NoteService> notes =em.createQuery("select n FROM NoteService n WHERE n.medecin.id='"+get.getId()+"'").getResultList();
        for (NoteService note : notes) {
            remove(note);
        }
    }
    
}
