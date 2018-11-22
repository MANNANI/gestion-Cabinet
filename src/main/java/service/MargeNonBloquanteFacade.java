/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.MargeNonBloquante;
import bean.Medecin;
import bean.Vacance;
import com.sun.org.apache.bcel.internal.generic.D2F;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.primefaces.model.DefaultScheduleEvent;

/**
 *
 * @author houda
 */
@Stateless
public class MargeNonBloquanteFacade extends AbstractFacade<MargeNonBloquante> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @EJB
    private VacanceFacade vacanceFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MargeNonBloquanteFacade() {
        super(MargeNonBloquante.class);
    }

    public void creer(List<Vacance> vacances, Employe medecin) {

        System.out.println("ha le med " + medecin);
        List<Medecin> med = em.createQuery("SELECT m FROM Medecin m WHERE m.id='" + medecin.getId() + '"').getResultList();
        if (!med.isEmpty()) {
            List<Vacance> listes = vacances;
            System.out.println("liste " + listes);
            for (int i = 0; i < listes.size(); i++) {
                System.out.println("ha le  " + i + "element " + listes.get(i).getClass() + "ha la liste " + listes);
                Vacance vacance = new Vacance();
                vacance = listes.get(i);
                System.out.println("ha vancance" + vacance);
                System.out.println("ha ");
                MargeNonBloquante m = new MargeNonBloquante();
                m.setDateDebut(vacance.getDateDebut());
                m.setDateFin(vacance.getDateFin());
                m.setMedecin(med.get(0));
                create(m);

            }

        }
    }
    public  List<DefaultScheduleEvent> convertirMarge(Medecin medecin) {
        List<MargeNonBloquante> margeNonBloquantes = em.createQuery("SELECT m FROM MargeNonBloquante m WHERE m.medecin.id='" + medecin.getId() + "'").getResultList();
        if (margeNonBloquantes.isEmpty()) {
            return null;
        } else {
           List<DefaultScheduleEvent> events = new ArrayList<>();
            for (MargeNonBloquante margeNonBloquante : margeNonBloquantes) {
                DefaultScheduleEvent event=new DefaultScheduleEvent("MargeNonBloquante",margeNonBloquante.getDateDebut(),margeNonBloquante.getDateFin(),"eventNonBloque");
           event.setEditable(false);
                events.add(event);
            }
            return events;
        }
}
}