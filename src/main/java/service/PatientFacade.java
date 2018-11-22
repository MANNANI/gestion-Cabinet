/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Medecin;
import bean.Patient;
import bean.Residence;
import bean.Specialite;
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
public class PatientFacade extends AbstractFacade<Patient> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @EJB
    private MedecinFacade medecinFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Patient> listEtat(String etat) {
        if (etat.equals("Bloquer")) {
            return em.createQuery("SELECT p FROM Patient p WHERE p.userConnexion.blocked=1").getResultList();
        } else if (etat.equals("NonBloquer")) {
            return em.createQuery("SELECT p FROM Patient p WHERE p.userConnexion.blocked=0").getResultList();
        } else {
            return null;
        }
    }

    public PatientFacade() {
        super(Patient.class);
    }
//    public List<Patient> recherche(Patient patient){
//        String requette="SELECT p FROM Patient p WHERE 1=1";
//      if(patient!=null){
//          requette+=util.DateUtil.addConstraint("p","patient.id","=",patient.getId());
//      }
//      List<Patient> patients=em.createQuery(requette).getResultList();
//      
//      
//      return patients;
//       
//    }

    public Patient recherchePat(Patient patient) {
        String requette = "SELECT p FROM Patient p WHERE 1=1";
        if (!patient.getNom().equals("")) {
            requette += util.DateUtil.addConstraint("p", "nom", "=", patient.getNom());
        }
        if (!patient.getPrenom().equals("")) {
            requette += util.DateUtil.addConstraint("p", "prenom", "=", patient.getPrenom());
        }
        List<Patient> patients = em.createQuery(requette).getResultList();

        return patients.get(0);

    }

    public List<Medecin> rechercheMedecin(Specialite specialite, Residence residence, String nom, String prenom, String tel, String email) {
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
            return medecinFacade.findAll();
        } else {
            return listes;
        }
    }

    public List<Medecin> rechercheMedecinPerReSp(Specialite specialite, Residence residence) {
        String requette = "SELECT m FROM Medecin m WHERE 1=1";
        if (specialite != null) {
            requette += util.DateUtil.addConstraint("m", "specialite.id", "=", specialite.getId());
        }
        if (residence != null) {
            requette += util.DateUtil.addConstraint("m", "residence.id", "=", residence.getId());
        }

        List<Medecin> listes = em.createQuery(requette).getResultList();

        return listes;
    }
}
