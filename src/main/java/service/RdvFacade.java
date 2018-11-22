/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.MargeNonBloquante;
import bean.Medecin;
import bean.Patient;
import bean.Rdv;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.ScheduleEvent;
import util.DateUtil;

/**
 *
 * @author houda
 */
@Stateless
public class RdvFacade extends AbstractFacade<Rdv> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @EJB
    private MargeBloquanteFacade margeBloquanteFacade;
    @EJB
    private MargeNonBloquanteFacade margeNonBloquanteFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RdvFacade() {
        super(Rdv.class);
    }
    //convertir les rdv into event 

    public List<DefaultScheduleEvent> convertir(Medecin medecin) {
        /*telecharger les rdv de ce medecin*/
        List<Rdv> rdvs = em.createQuery("SELECT r FROM Rdv r WHERE r.medecin.id='" + medecin.getId() + "'").getResultList();
        List<DefaultScheduleEvent> l1 = new ArrayList<>();
        for (Rdv rdv : rdvs) {
            /* date de fin du event = date de debut + pas */
            DefaultScheduleEvent event = new DefaultScheduleEvent(rdv.getPatient().getNom(), rdv.getDateDebut(), new Date(rdv.getDateDebut().getTime() + medecin.getConfiguration().getPas() * 60 * 1000), "colorRdv");
            event.setId("Rdv" + rdv.getId());
            event.setDescription("" + rdv.getId());
//            event.setDescription("rddvvv");
            l1.add(event);
        }
        List<DefaultScheduleEvent> l2 = margeBloquanteFacade.convertirMarge(medecin);
        if (l2 != null) {
            l1.addAll(l2);
        }
       List<DefaultScheduleEvent> l3 =margeNonBloquanteFacade.convertirMarge(medecin);
        if (l3 != null) {
            l1.addAll(l3);
        }
        return l1;
    }

    public Rdv findByEvent(ScheduleEvent event, Medecin medecin) {
        if (event.getId() != null) {
            String requette = "SELECT r FROM Rdv r WHERE r.id='" + event.getDescription() + "' and r.medecin.id='" + medecin.getId() + "'";

//            if (event.getStartDate() != null) {
//                requette += util.DateUtil.addConstraintDate("r", "dateDebut", "=", DateUtil.getSqlDate(event.getStartDate()));
//            }
//            if (!event.getTitle().equals("")) {
//                requette += util.DateUtil.addConstraint("r.patient", "nom", "=", event.getTitle());
//            }
            List<Rdv> l1 = em.createQuery(requette).getResultList();
            if (!l1.isEmpty()) {
                return l1.get(0);
            }
        }
        return null;
    }

    public DefaultScheduleEvent getPram(DefaultScheduleEvent event, Rdv rdvBd,Rdv rdvView) {
        if (event != null && rdvBd != null && rdvView!=null) {
            rdvBd.setDateDebut(event.getStartDate());
            rdvBd.setMedecin(rdvView.getMedecin());
            rdvBd.setPatient(rdvView.getPatient());
            edit(rdvBd);
          return event;
        }
       return null;
    }

    public List<DefaultScheduleEvent> findRdvByPatient(Patient patient, Medecin medecin) {
        if (patient != null) {
            List<Rdv> rdvs = em.createQuery("SELECT r FROM Rdv r WHERE r.patient.id='" + patient.getId() + "'").getResultList();
            List<DefaultScheduleEvent> l1 = new ArrayList<>();
            for (Rdv rdv : rdvs) {
                /* date de fin du event = date de debut + pas */
                DefaultScheduleEvent event = new DefaultScheduleEvent(rdv.getPatient().getNom(), rdv.getDateDebut(), new Date(rdv.getDateDebut().getTime() + medecin.getConfiguration().getPas() * 60 * 1000), "colorRdv");
                event.setEditable(false);
                l1.add(event);
            }
            List<DefaultScheduleEvent> l2 = margeBloquanteFacade.convertirMarge(medecin);
            if (l2 != null) {
                l1.addAll(l2);
            }
            return l1;

        }
        return null;

    }
    public List<Patient> findPatientByMedecin(Medecin medecin){
        if(medecin!=null){
            System.out.println("ha la liste +"+em.createQuery("SELECT DISTINCT r.patient FROM Rdv r WHERE r.medecin.id='"+medecin.getId()+"'").getResultList());
            return em.createQuery("SELECT DISTINCT r.patient FROM Rdv r WHERE r.medecin.id='"+medecin.getId()+"'").getResultList();
        }
        return null;
      
    }
    public List<Rdv> findRdvByMedecin(Medecin medecin){
         if(medecin!=null){
            return em.createQuery("SELECT r FROM Rdv r WHERE r.medecin.id='"+medecin.getId()+"'").getResultList();
        }
        return null;
    }

   public void deleteRdvs(Medecin get) {
List<Rdv> rdvs= findRdvByMedecin(get);
       for (Rdv rdv : rdvs) {
           remove(rdv);
       }
   
   
   }

    public List<DefaultScheduleEvent> listDateRdv(Medecin medecin, Date dateDeRdv) {
        System.out.println("insidelistDateRdv");
        
        List<Rdv> rdvs = em.createQuery("SELECT r FROM Rdv r WHERE r.medecin.id='" + medecin.getId() + "'").getResultList();
        List<DefaultScheduleEvent> l1 = new ArrayList<>();
        Date dateConfigDebut = new Date();
        dateConfigDebut.setTime(medecin.getConfiguration().getHeureDebut().getTime());
        Date dateConfigFin = new Date(medecin.getConfiguration().getHeureFin().getTime());
        List<String> dates = new ArrayList<>();
        int res ;
        boolean isMarge;
        Date dateRdvFin = new Date(dateConfigFin.getTime());
//            System.out.println("util.DateUtil.convertdateToString(dateDeRdv)-->" + util.DateUtil.convertdateToString(dateDeRdv));
//            System.out.println("util.DateUtil.convertdateToString(rdv.getDateDebut())-->" + util.DateUtil.convertdateToString(rdv.getDateDebut()));
        System.out.println("dateConfigDebut-->" + dateConfigDebut);
        System.out.println("medecin.getConfiguration().getHeureFin()-->" + medecin.getConfiguration());
        while (dateConfigDebut.getTime() < dateConfigFin.getTime()) {
            dateRdvFin.setTime(dateConfigDebut.getTime() + medecin.getConfiguration().getPas() * 60 * 1000);
            res = dateIsRdv(DateUtil.convertHeure(dateConfigDebut), rdvs, dateDeRdv);
//            System.out.println("res === " + res + " \t dateRdvFin" + dateRdvFin + " \t dateConfigFin  " + dateConfigFin);
            isMarge = margeBloquanteFacade.testMarge(dateConfigDebut, medecin);
            if (res >= 0 && !isMarge) {
                if (dateRdvFin.getTime() <= dateConfigFin.getTime()) {
                    DefaultScheduleEvent event = new DefaultScheduleEvent("xxMessage", new Date(dateConfigDebut.getTime()), new Date(dateRdvFin.getTime()), "rdvClass");
                    l1.add(event);
                    System.out.println(" dateConfigDebut-->" + dateConfigDebut);

                } else {
                    return l1;
                }
            }
            dateConfigDebut.setTime(dateConfigDebut.getTime() + medecin.getConfiguration().getPas() * 60 * 1000);
        }
        return l1;
    }

    public List<DefaultScheduleEvent> patientRdv(Patient patient, Medecin medecin) {
        String requet = "";
        Date date = new Date();
        if (patient != null) {
            requet = "select r from Rdv r where r.patient.id='" + patient.getId() + "'";
        }
        System.out.println("rdvFacade.patientRdv..requet--->"+requet);
        List<Rdv> rdvs = getEntityManager().createQuery(requet).getResultList();
        List<DefaultScheduleEvent> l1 = new ArrayList<>();
        for (Rdv rdv : rdvs) {
            /* date de fin du event = date de debut + pas */
            System.out.println("patientRdv.rdv" + rdv.getDateDebut());
            System.out.println("patientRdv.medecin.getConfiguration().getPas()---->" + medecin.getConfiguration().getPas());
            DefaultScheduleEvent event = new DefaultScheduleEvent("", rdv.getDateDebut(), new Date(rdv.getDateDebut().getTime() + medecin.getConfiguration().getPas() * 60 * 1000));
            event.setId("Rdv" + rdv.getId());
            event.setDescription("" + rdv.getId());
            event.setDescription("rdv Patient");
            
            if(rdv.getDateDebut().getTime() >= date.getTime()){
                event.setStyleClass("rdvNoPasse");
                event.setTitle(" rdv not passed with \n   Dr."+rdv.getMedecin().getNom());
            }else{
                event.setStyleClass("rdvPasse");
                event.setTitle(" rdv Passed with \n  Dr."+rdv.getMedecin().getNom());
            }
            l1.add(event);
        }
        return l1;
    }

    public int dateIsRdv(String date, List<Rdv> rdvs, Date dateDeRdv) {
        String dateRdv = DateUtil.convertdateToString(dateDeRdv);
        String str = "";
        for (Rdv rdv : rdvs) {
            str = DateUtil.convertdateToString(rdv.getDateDebut());
            if (date.equals(DateUtil.convertHeure(rdv.getDateDebut())) && dateRdv.equals(str)) {
                return -1;
            }
        }
        return 1;
    }

    public Date timeEquals(Date dateSrc, Date dateDes) {
        dateDes.setHours(dateSrc.getHours());
        dateDes.setMinutes(dateSrc.getMinutes());
        dateDes.setSeconds(dateSrc.getSeconds());
        return dateDes;
    }


    public List<Rdv> rdvParPatient(Patient patient) {
        String requet = "SELECT r FROM Rdv r WHERE 1=1 ";
        requet += util.DateUtil.addConstraint("r", "patient.id", "=", patient.getId());
        requet+=util.DateUtil.addConstraintDate("r", "dateDebut", ">=", new Date());
        System.out.println("requet"+requet);
        return getEntityManager().createQuery(requet).getResultList();
    }
    /**
     * Demande
     * @param medecin
     * @return 
     */
    
    public List<Rdv> RdvsByMedecin(Medecin medecin) {
        String requet = "SELECT r FROM Rdv r WHERE 1=1 ";
        if (medecin != null) {
            requet += util.DateUtil.addConstraint("r", "medecin.id", "=", medecin.getId());
            requet += util.DateUtil.addConstraint("r", "confirmer", "=", 0);
        }
        return getEntityManager().createQuery(requet).getResultList();
    }
    
    
    public Long nbrDemande() {
       
            List<Long> l= em.createQuery("SELECT count(r.id) FROM Rdv r WHERE r.confirmer = 1").getResultList();
            System.out.println("nombre des compte blocker "+l.get(0));
           return l.get(0);
//            System.out.println("haa apres le cast => "+res);
//                 return  3;
    }

}
