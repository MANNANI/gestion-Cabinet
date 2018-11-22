/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.Medecin;
import bean.Patient;
import bean.Residence;
import bean.Specialite;
import bean.UserConnexion;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author houda
 */
@Stateless
public class MedecinFacade extends AbstractFacade<Medecin> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @EJB
    private ConfigurationFacade configurationFacade;
    @EJB
    private NoteServiceFacade noteServiceFacade;
    @EJB
    private RdvFacade rdvFacade;
    @EJB
    private MedecinSecretaireFacade medecinSecretaireFacade;
    @EJB
    private MargeFacade margeFacade;
    @EJB
    private UserConnexionFacade userConnexionFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MedecinFacade() {
        super(Medecin.class);
    }

    public List<Medecin> recherche(Specialite specialite, Residence residence, String nom, String prenom, String tel, String email) {
        String requette = "SELECT m FROM Medecin m WHERE 1=1";
        if (specialite != null) {
            requette += util.DateUtil.addConstraint("m", "specialite.id", "=", specialite.getId());
        }
        if (residence != null) {
            requette += util.DateUtil.addConstraint("m", "residence.id", "=", residence.getId());
        }

        if (!nom.equals("")) {
            requette += util.DateUtil.addConstraint("m", "nom", "=", nom);
        }
        if (!prenom.equals("")) {
            requette += util.DateUtil.addConstraint("m", "prenom", "=", prenom);
        }
        if (!tel.equals("")) {
            requette += util.DateUtil.addConstraint("m", "tel", "=", tel);
        }
        if (!email.equals("")) {
            requette += util.DateUtil.addConstraint("m", "email", "=", email);
        }
        List<Medecin> listes = em.createQuery(requette).getResultList();
        if (listes.isEmpty()) {
            return findAll();
        } else {
            return listes;
        }
    }

    public void removeEmp(Employe employe) {
        List<Medecin> med = em.createQuery("select a from Medecin a where a.id='" + employe.getId() + "'").getResultList();
        // configuration
        configurationFacade.remove(med.get(0).getConfiguration());
        //note 
        noteServiceFacade.deleteNotes(med.get(0));
//MedSec
        medecinSecretaireFacade.deleteSec(med.get(0));
//Rdv
        rdvFacade.deleteRdvs(med.get(0));
//marge
        margeFacade.deleteMarges(med.get(0));
//compte user
        UserConnexion user = userConnexionFacade.find(med.get(0).getUserConnexion().getLogin());
        med.get(0).setUserConnexion(null);
        userConnexionFacade.remove(user);
// med
        remove(med.get(0));
    }

    
     public List<Patient> patientByMedecin(Medecin medecin){
        
        String requet="SELECT r.patient FROM Rdv r WHERE r.medecin.id='"+medecin.getId()+"'";
        return getEntityManager().createQuery(requet).getResultList();
    }
    
}
