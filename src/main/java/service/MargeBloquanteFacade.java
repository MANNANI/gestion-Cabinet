/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.MargeBloquante;
import bean.MargeItem;
import bean.Medecin;
import java.util.ArrayList;
import java.util.Date;
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
public class MargeBloquanteFacade extends AbstractFacade<MargeBloquante> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @EJB
    private MargeItemFacade margeItemFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MargeBloquanteFacade() {
        super(MargeBloquante.class);
    }

    public MargeBloquante creer(List<MargeItem> margeItems, MargeBloquante margeBloquante, Medecin emp) {
//       List<Medecin> med=em.createQuery("SELECT m FROM Medecin m WHERE m.id='"+emp.getId()+'"').getResultList();
//       if(!med.isEmpty()){
        System.out.println("haa le meddce" + emp.getId());
        margeBloquante.setMedecin(emp);
//       }
        margeBloquante.setId(generateId("MargeBloquante", "id"));
        for (MargeItem margeItem : margeItems) {
            margeItem.setMargeBloquante(margeBloquante);
            margeItemFacade.create(margeItem);
        }
        margeBloquante.setMargeItems(margeItems);
        create(margeBloquante);
        return margeBloquante;
    }

    public boolean testMargeBloquante(Date dateDebut, Date dateFin) {
        String requette = "SELECT mb FROM MargeBloquante mb WHERE 1=1 ";
        if (dateDebut != null && dateFin != null) {
            System.out.println("haniii f requette date ");
            requette += " AND mb.dateDebut <='" + util.DateUtil.getSqlDate(dateDebut) + "' AND mb.dateFin >= '" + util.DateUtil.getSqlDate(dateFin) + "'";
        }
        List<MargeBloquante> margeBloquantes = em.createQuery(requette).getResultList();
        if (margeBloquantes.isEmpty()) {
            System.out.println("liste emty");
            return false;
        } else {
            System.out.println("liste pleine");
            return true;
        }
    }

    public boolean testMarge(Date dateSelect, Medecin medecin) {
        /*ajouterrrr les marges d'un medecin*/

        if (dateSelect != null) {
            /*requette pour recuperer tt les marges dont la date selectionne est comprise entre*/
            String requette = "select mb from MargeBloquante mb where mb.medecin.id='" + medecin.getId() + "'";
            requette += " AND mb.dateDebut <='" + util.DateUtil.getSqlDate(dateSelect) + "' AND mb.dateFin >= '" + util.DateUtil.getSqlDate(dateSelect) + "'";
            List<MargeBloquante> margeBloquantes = em.createQuery(requette).getResultList();
            if (margeBloquantes.isEmpty()) {
                return false;
            } else {
                /*marge trouve*/
                for (MargeBloquante margeBloquante : margeBloquantes) {
                    Date dateLyouma = margeBloquante.getDateDebut();
                    /*recuperer la liste des margeItem de cette MargeBolquante*/
                    List<MargeItem> margeItems = em.createQuery("SELECT m FROM MargeItem m WHERE m.margeBloquante.id='" + margeBloquante.getId() + "'").getResultList();
                    for (MargeItem margeItem : margeItems) {
                        Date dateDeb = new Date(margeBloquante.getDateDebut().getTime());
                        dateLyouma = dateDeb;
                        /*teste le pas*/
                        int pas = 0;
                        Long config = null;
                        if (margeItem.getAnnee() > 0) {
                            pas = margeItem.getAnnee();
                            config = 12 * 30 * 24 * 60 * 60 * 1000L;
                        }
                        if (margeItem.getJour() > 0) {
                            pas = margeItem.getJour();
                            config = 24 * 60 * 60 * 1000L;

                        }
                        if (margeItem.getMois() > 0) {
                            pas = margeItem.getMois();
                            config = 30 * 24 * 60 * 60 * 1000L;

                        }

                        while (dateLyouma.getTime() <= margeBloquante.getDateFin().getTime()) {
//                         
                            /*trouver le pas*/
                            if (pas == 0) {
                                int res = testMargeItem(margeItem, dateLyouma, dateSelect);
                                if (res < 0) {
                                    return true;
                                } else {
                                    dateLyouma.setTime(margeBloquante.getDateFin().getTime() + 24 * 60 * 60 * 1000);
                                }
                            } else {
                                int res = testMargeItem(margeItem, dateLyouma, dateSelect);
                                if (res < 0) {
                                    return true;
                                } else {
                                    /*ajouter + pas jour */
                                    dateLyouma.setTime(dateLyouma.getTime() + pas * config);
                                    /*tester l'egalite avec la date selectionne */
//                                    int res = testMargeItem(margeItem, dateLyouma, dateSelect);
//                                    if (res < 0) {
//                                        return true;
//                                    }
                                }
                            }
                        }

                    }

                }
            }

        }
        /*la date selectionne est null*/
        return false;
    }

    public List<DefaultScheduleEvent> convertirMarge(Medecin medecin) {
        List<MargeBloquante> margeBloquantes = em.createQuery("SELECT m FROM MargeBloquante m WHERE m.medecin.id='" + medecin.getId() + "'").getResultList();
        if (margeBloquantes.isEmpty()) {
            return null;
        } else {
            List<DefaultScheduleEvent> events = new ArrayList<>();
            for (MargeBloquante margeBloquante : margeBloquantes) {
                Date dateLyouma = margeBloquante.getDateDebut();
                /*recuperer la liste des margeItem de cette MargeBolquante*/
                List<MargeItem> margeItems = em.createQuery("SELECT m FROM MargeItem m WHERE m.margeBloquante.id='" + margeBloquante.getId() + "'").getResultList();
                for (MargeItem margeItem : margeItems) {
                    Date dateDeb = new Date(margeBloquante.getDateDebut().getTime());
                    dateLyouma = dateDeb;
                    /*teste le pas*/
                    int pas = 0;
                    Long config = null;
                    if (margeItem.getAnnee() > 0) {
                        pas = margeItem.getAnnee();
                        config = 12 * 30 * 24 * 60 * 60 * 1000L;
                    }
                    if (margeItem.getJour() > 0) {
                        pas = margeItem.getJour();
                        config = 24 * 60 * 60 * 1000L;
                    }
                    if (margeItem.getMois() > 0) {
                        pas = margeItem.getMois();
                        config = 30 * 24 * 60 * 60 * 1000L;
                    }
                    while (dateLyouma.getTime() <= margeBloquante.getDateFin().getTime()) {
                        /*trouver le pas*/
                        if (pas == 0) {
                            events.add(convertirMargeItem(margeItem, dateLyouma));
                            dateLyouma.setTime(margeBloquante.getDateFin().getTime() + 24 * 60 * 60 * 1000);
                        } else {
                            events.add(convertirMargeItem(margeItem, dateLyouma));
                            /*ajouter + pas jour */
                            dateLyouma.setTime(dateLyouma.getTime() + pas * config);
                        }
                    }
                }
            }
            return events;
        }
    }

    public List<String> dateMarge(Medecin medecin) {
        List<String> margeDates = new ArrayList<>();
        List<MargeBloquante> margeBloquantes = em.createQuery("SELECT m FROM MargeBloquante m WHERE m.medecin.id='" + medecin.getId() + "'").getResultList();
        if (margeBloquantes.isEmpty()) {
            return null;
        } else {
            List<DefaultScheduleEvent> events = new ArrayList<>();
            for (MargeBloquante margeBloquante : margeBloquantes) {
                Date dateLyouma = margeBloquante.getDateDebut();
                /*recuperer la liste des margeItem de cette MargeBolquante*/
                List<MargeItem> margeItems = em.createQuery("SELECT m FROM MargeItem m WHERE m.margeBloquante.id='" + margeBloquante.getId() + "'").getResultList();
                for (MargeItem margeItem : margeItems) {
                    Date dateDeb = new Date(margeBloquante.getDateDebut().getTime());
                    dateLyouma = dateDeb;
                    /*teste le pas*/
                    int pas = 0;
                    Long config = null;
                    if (margeItem.getAnnee() > 0) {
                        pas = margeItem.getAnnee();
                        config = 12 * 30 * 24 * 60 * 60 * 1000L;
                    }
                    if (margeItem.getJour() > 0) {
                        pas = margeItem.getJour();
                        config = 24 * 60 * 60 * 1000L;
                    }
                    if (margeItem.getMois() > 0) {
                        pas = margeItem.getMois();
                        config = 30 * 24 * 60 * 60 * 1000L;
                    }
                    while (dateLyouma.getTime() <= margeBloquante.getDateFin().getTime()) {
                        /*trouver le pas*/
                        if (pas == 0) {
                            margeDates.add(util.DateUtil.convertdateToString(dateLyouma));
                            System.out.println("datelyoum ---->" + dateLyouma);
                            System.out.println("datelyoum convert---->" + util.DateUtil.convertToDate(dateLyouma));
//                            events.add(convertirMargeItem(margeItem, dateLyouma));
                            dateLyouma.setTime(margeBloquante.getDateFin().getTime() + 24 * 60 * 60 * 1000);
                        } else {
                            margeDates.add(util.DateUtil.convertdateToString(dateLyouma));
                            System.out.println("datelyoum ---->" + dateLyouma);
                            System.out.println("datelyoum convert---->" + util.DateUtil.convertToDate(dateLyouma));
//                            events.add(convertirMargeItem(margeItem, dateLyouma));
                            /*ajouter + pas jour */
                            dateLyouma.setTime(dateLyouma.getTime() + pas * config);
                        }
                    }
                }
            }
            return margeDates;
        }
    }

    public int testMargeItem(MargeItem margeItem, Date dateLyouma, Date dateSelected) {
        Date dateDebut = new Date(dateLyouma.getTime() + margeItem.getHeureDebut().getTime());
        Date dateFin = new Date(dateLyouma.getTime() + margeItem.getHeureFin().getTime());
        if (dateSelected.getTime() >= dateDebut.getTime() && dateSelected.getTime() <= dateFin.getTime()) {
            return -1;
            /*c'est dans la marge*/
        } else {
            return 1;
            /*dehors de la marge*/
        }
    }

    public DefaultScheduleEvent convertirMargeItem(MargeItem margeItem, Date dateLyouma) {
        Date dateDebut = new Date(dateLyouma.getTime() + margeItem.getHeureDebut().getTime());
        Date dateFin = new Date(dateLyouma.getTime() + margeItem.getHeureFin().getTime());
        DefaultScheduleEvent event = new DefaultScheduleEvent("margeBloquante", dateDebut, dateFin, "EventBloque");
        event.setEditable(false);
        return event;
    }

}
