/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Medecin;
import bean.Patient;
import bean.Rdv;
import controller.util.JsfUtil;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import service.MargeBloquanteFacade;
import service.RdvFacade;

/**
 *
 * @author ACER
 */
@Named(value = "schedulePatient")
@SessionScoped
public class schedulePatient implements Serializable {
    
    private ScheduleModel eventModel;
    
    private DefaultScheduleEvent event = new DefaultScheduleEvent();
    private List<DefaultScheduleEvent> events;
    private Medecin medecin;
    private Rdv selected;
    private Date dateChoisi;
    @EJB
    private RdvFacade rdvFacade;
    @EJB
    private MargeBloquanteFacade margeBloquanteFacade;
    private List<String> dateDeMarge;
    private String text = "";
    
    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        medecin = (Medecin) util.Session.getAttribut("MedecinPatient");
        System.out.println("dans ScheduleView.init(à): medecinChoisis est " + medecin);
        if (medecin != null) {
            addEvents();
        } else {
            medecin = new Medecin();
        }
        
    }

    //telecharger les rdv de la base de donnee
    public void addEvents() {
        eventModel.clear();
        events = null;
        System.out.println("medecinChoisis = " + medecin);
        
        Patient patient = (Patient) util.Session.getAttribut("UserPatient");
        System.out.println("util.Session.getAttribut(UserPatient); " + patient);
        if (patient == null) {
            patient = (Patient) util.Session.getAttribut("PatientRech");
        }
        
        System.out.println("util.Session.getAttribut(PatientRech); " + patient);
        events = rdvFacade.patientRdv(patient, medecin);
        System.out.println("events.size = " + events.size());
        for (DefaultScheduleEvent event1 : events) {
            System.out.println("event1.dateFin---->" + event1.getEndDate());
            System.out.println("event1.dateDebut---->" + event1.getStartDate() + "\n\n");
            eventModel.addEvent(event1);
            
        }
    }
    
    public String dateMargeString() {
        String str = "";
        dateDeMarge = margeBloquanteFacade.dateMarge(medecin);
        if (dateDeMarge != null) {
            for (int i = 0; i < getDateDeMarge().size(); i++) {
                
                str += dateDeMarge.get(i);
                str += ",";
                
            }
        }
        return str;
    }
    
    public ScheduleModel getEventModel() {
        if (eventModel == null) {
            eventModel = new DefaultScheduleModel();
        }
        return eventModel;
    }
    
    public DefaultScheduleEvent getEvent() {
        return event;
    }
    
    public void setEvent(DefaultScheduleEvent event) {
        this.event = event;
    }
    
    public void addEvent(ActionEvent actionEvent) {
        /*teste si c'est uen marge*/
        int res = checkValiditeDate();
        if (res > 0) {
            System.out.println("maaaargggggggggggggg");
        } else {
            //creation du rdv
            selected.setMedecin(medecin);
            selected.setId(rdvFacade.generateId("Rdv", "id"));
            rdvFacade.create(selected);
            //creation du event
            Date d = new Date(selected.getDateDebut().getTime() + medecin.getConfiguration().getPas() * 60 * 1000);
            event = new DefaultScheduleEvent("", selected.getDateDebut(), d, "colorRdv");
            event.setDescription("" + selected.getId());
            event.setTitle(selected.getPatient().getNom());
            eventModel.addEvent(event);
        }
    }
    
    public String message() {
        System.out.println("haa le messa" + text);
        if (text.equals("")) {
            setText("");
            return "";
        } else {
            setText("");
            return "Date invalide";
        }
    }
    
    public int checkValiditeDate() {
        boolean isMarge = margeBloquanteFacade.testMarge(selected.getDateDebut(), medecin);
        if (isMarge) {
            System.out.println("c'est une marge");
            setText("ouii");
            return 1;
        } else {
            text = "";
            return -1;
        }
    }
    
    public void onDateSelect(SelectEvent selectEvent) {
        //mettre la date selectionne dans le input
        Date dateNew = new Date();
        selected = new Rdv((Date) selectEvent.getObject());
        System.out.println("haa selected =>" + selected.getDateDebut());
        dateChoisi = new Date();
        dateChoisi.setTime(selected.getDateDebut().getTime());
        System.out.println("dateChoisi--->" + dateChoisi);
        String dat1 = util.DateUtil.convertdateToString(dateChoisi);
        String dat2 = util.DateUtil.convertdateToString(dateNew);
        
        if (dateChoisi.getTime() >= dateNew.getTime()) {
            searchRdv();
        } else {
            events = new ArrayList();
        }
//        System.out.println("hanii date du rdv " + selected.getDateDebut());
//        //calcul de date fin
//        System.out.println("ha la confir" + medecin.getConfiguration().getPas() * 60 * 1000);
//        Date d = new Date(selected.getDateDebut().getTime() + medecin.getConfiguration().getPas() * 60 * 1000);
//        /*sinon null pointer exception */
//        event = new DefaultScheduleEvent("", selected.getDateDebut(), d);
        //test la marge
//        boolean isExiste = margeBloquanteFacade.testMarge(selected.getDateDebut(), medecin);
//        if (isExiste) {
//            RequestContext.getCurrentInstance().execute("PF('eventDialog').hide()");
//        } else {
//            RequestContext.getCurrentInstance().execute("PF('eventDialog').show()");
//        }
    }
    
    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
        
        addMessage(message);
    }
    
    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
        
        addMessage(message);
    }
    
    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public Medecin getMedecin() {
        if (medecin == null) {
            medecin = new Medecin();
        }
        return medecin;
    }
    
    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }
    
    public List<String> getDateDeMarge() {
        
        System.out.println("medecinChoisis--->" + medecin);
        dateDeMarge = margeBloquanteFacade.dateMarge(medecin);
        System.out.println("dateDeMarge --->" + dateDeMarge);
        
        return dateDeMarge;
    }
    
    public void setDateDeMarge(List<String> dateDeMarge) {
        this.dateDeMarge = dateDeMarge;
    }
    
    public Rdv getSelected() {
        if (selected == null) {
            selected = new Rdv();
        }
        return selected;
    }
    
    public void setSelected(Rdv selected) {
        this.selected = selected;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Date getDateChoisi() {
        if (dateChoisi == null) {
            dateChoisi = new Date();
        }
        return dateChoisi;
    }
    
    public void setDateChoisi(Date dateChoisi) {
        this.dateChoisi = dateChoisi;
    }
    
    public List<DefaultScheduleEvent> getEvents() {
        System.out.println("events--->" + events);
        if (events == null) {
            events = new ArrayList<>();
        }
        return events;
    }
    
    public void setEvents(List<DefaultScheduleEvent> events) {
        this.events = events;
    }
    
    public void demander(DefaultScheduleEvent event) {
        Patient patient = (Patient) util.Session.getAttribut("UserPatient");
        Rdv rdv = new Rdv();
        if (patient != null) {
            Date date = new Date(event.getStartDate().getTime() + dateChoisi.getTime());
            rdv.setDateDebut(date);
            rdv.setMedecin(medecin);
            rdv.setPatient(patient);
            rdvFacade.create(rdv);
            events.remove(event);
            eventModel.addEvent(event);
            FacesContext context = FacesContext.getCurrentInstance();
         
        context.addMessage(null, new FacesMessage("Successful",  "Rendez-vous est creer avec succes" ) );
        }
    }
    
    public void searchRdv() {
        System.out.println("inside searchRdv");
        System.out.println("dateChoisi" + dateChoisi);
        if (medecin != null && dateChoisi != null) {
            events = rdvFacade.listDateRdv(medecin, dateChoisi);
        }
        
    }
    
}
