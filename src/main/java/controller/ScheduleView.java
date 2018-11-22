/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import bean.Employe;
import bean.Medecin;
import bean.MedecinSecretaire;
import bean.NoteService;
import bean.Patient;
import bean.Rdv;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.event.ActionEvent;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ScheduleEntryMoveEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;
import service.EmployeFacade;
import service.MargeBloquanteFacade;
import service.NoteServiceFacade;
import service.PatientFacade;
import service.RdvFacade;

@Named("scheduleView")
@SessionScoped
public class ScheduleView implements Serializable {

    @EJB
    private RdvFacade rdvFacade;
    @EJB
    private EmployeFacade employeFacade;
    @EJB
    private MargeBloquanteFacade margeBloquanteFacade;
    @EJB
    private NoteServiceFacade noteServiceFacade;
    @EJB
    private PatientFacade patientFacade;
    private ScheduleModel eventModel;
    private Rdv selected;
    private Patient patient;
//  private Patient patientRech;
    private List<NoteService> noteServices;
    private DefaultScheduleEvent event = new DefaultScheduleEvent();
    private Medecin medecinChoisis;
    private int validite;
    private String text = "";
    private List<Patient> patients;
    private List<Rdv> rdvs;
    private List<String> dateDeMarge;
    private String dateString = "";
    private NoteService noteService;

    @PostConstruct
    public void init() {
        eventModel = new DefaultScheduleModel();
        
        Employe employe = (Employe) util.Session.getAttribut("UserEmploye");
        String type = employeFacade.findUserFonction(employe);
        if (type.equals("MedecinSecretaire")) {
            MedecinSecretaire ms = (MedecinSecretaire) util.Session.getAttribut("UserEmploye");
            medecinChoisis = (Medecin) employeFacade.find(ms.getMedecin().getId());
        } else {
            medecinChoisis = (Medecin) util.Session.getAttribut("UserEmploye");
        }
       
        addEvents();
        patients = rdvFacade.findPatientByMedecin(medecinChoisis);
        System.out.println("haa les ptainet " + patients);
        noteServices = noteServiceFacade.findNoteByMedecin(medecinChoisis);
        rdvs = rdvFacade.findRdvByMedecin(medecinChoisis);
    }

    //telecharger les rdv de la base de donnee
    public void addEvents() {
        eventModel.clear();
        List<DefaultScheduleEvent> events = rdvFacade.convertir(medecinChoisis);
        for (DefaultScheduleEvent event1 : events) {
            eventModel.addEvent(event1);
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

//    public String pageConvertirNoteToMarge() {
//        if (noteService != null) {
//            util.Session.setAttribut("note", noteService);
//            System.out.println("hani hna");
//            return "/acceuilMedecin/CreerMargeBl?faces-redirect=true";
//        }
//        return null;
//    }
    public int checkValiditeDate() {
        boolean isMarge = margeBloquanteFacade.testMarge(selected.getDateDebut(), medecinChoisis);
        if (isMarge) {
            System.out.println("c'est une marge");
            setText("ouii");
            return 1;
        } else {
            System.out.println("ce nest pas uen marg");
            text = "";
            return -1;
        }
    }

    public void setEventDateFin() {
        Date d = new Date(event.getStartDate().getTime() + medecinChoisis.getConfiguration().getPas() * 60 * 1000);
        event.setEndDate(d);

    }
//    public void recherchePatient() {
//        RequestContext.getCurrentInstance().execute("PF('RecherchePatient').show()");
//    }
//
//    public Patient getPatientRech() {
//        if(patientRech==null){
//            patientRech=new Patient();
//        }
//        return patientRech;
//    }
//
//    public void setPatientRech(Patient patientRech) {
//        this.patientRech = patientRech;
//    }
//    public void recherche() {
//        System.out.println("hanii dkhalte l recherche");
//        System.out.println("ha le patient" + patientRech);
//        eventModel.clear();
//        List<DefaultScheduleEvent> events = rdvFacade.findRdvByPatient(patientRech, medecinChoisis);
//        for (DefaultScheduleEvent event1 : events) {
//            eventModel.addEvent(event1);
//        }
//
//    }

    public void addEvent(ActionEvent actionEvent) {
        /*teste si c'est uen marge*/
        int res = checkValiditeDate();
        if (res > 0) {
            System.out.println("maaaargggggggggggggg");
        } else {
            //creation du rdv
            selected.setPatient(patientFacade.find(patient.getId()));
            selected.setMedecin(medecinChoisis);
            selected.setId(rdvFacade.generateId("Rdv", "id"));
            rdvFacade.create(selected);
            //creation du event
            Date d = new Date(selected.getDateDebut().getTime() + medecinChoisis.getConfiguration().getPas() * 60 * 1000);
            event = new DefaultScheduleEvent("", selected.getDateDebut(), d, "colorRdv");
            event.setDescription("" + selected.getId());
            event.setTitle(selected.getPatient().getNom());
            eventModel.addEvent(event);
        }
    }

    public void modifierEvent(ActionEvent actionEvent) {
        if (event.getId() != null) {
            Rdv loaded = rdvFacade.findByEvent(event, medecinChoisis);
            rdvFacade.getPram(event, selected, loaded);
            eventModel.updateEvent(event);
            selected = new Rdv();
            event = new DefaultScheduleEvent();
        }
    }

    public void suppEvent(ActionEvent actionEvent) {
        if (event.getId() != null) {
            selected = rdvFacade.findByEvent(event, medecinChoisis);
            rdvFacade.remove(selected);
            eventModel.deleteEvent(event);
            selected = new Rdv();
            event = new DefaultScheduleEvent();
        }
    }

    public void onEventSelect(SelectEvent selectEvent) {
        event = (DefaultScheduleEvent) selectEvent.getObject();
        selected = rdvFacade.findByEvent(event, medecinChoisis);
        System.out.println("haa selected" + selected);
        boolean isExiste = margeBloquanteFacade.testMarge(selected.getDateDebut(), medecinChoisis);
        if (isExiste) {
            RequestContext.getCurrentInstance().execute("PF('eventInfo').hide()");
        } else {
            RequestContext.getCurrentInstance().execute("PF('eventInfo').show()");
        }
    }

    public void onDateSelect(SelectEvent selectEvent) {
        //mettre la date selectionne dans le input
        selected = new Rdv((Date) selectEvent.getObject());
        System.out.println("hanii date du rdv " + selected.getDateDebut());
        //calcul de date fin
        System.out.println("ha la confir" + medecinChoisis.getConfiguration().getPas() * 60 * 1000);
        Date d = new Date(selected.getDateDebut().getTime() + medecinChoisis.getConfiguration().getPas() * 60 * 1000);
        /*sinon null pointer exception */
        event = new DefaultScheduleEvent("", selected.getDateDebut(), d);
        //test la marge
        boolean isExiste = margeBloquanteFacade.testMarge(selected.getDateDebut(), medecinChoisis);
        if (isExiste) {
            RequestContext.getCurrentInstance().execute("PF('eventDialog').hide()");
        } else {
            RequestContext.getCurrentInstance().execute("PF('eventDialog').show()");
        }
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        selected = rdvFacade.findByEvent(event.getScheduleEvent(), medecinChoisis);
        System.out.println("haa le selected" + selected);

        Rdv s = selected;
        rdvFacade.remove(selected);
        s.setId(rdvFacade.generateId("Rdv", "id"));
        System.out.println("haaa le rdv jdide" + s);
        rdvFacade.edit(s);
        eventModel.deleteEvent(event.getScheduleEvent());
//        Date d=new Date(event.getScheduleEvent().getStartDate().);
        Date f = new Date(+medecinChoisis.getConfiguration().getPas() * 60 * 1000);
        DefaultScheduleEvent ev = new DefaultScheduleEvent(selected.getPatient().getNom(), event.getScheduleEvent().getStartDate(), event.getScheduleEvent().getEndDate(), "colorRdv");

        ev.setDescription("" + s.getId());
        eventModel.addEvent(ev);
        System.out.println("ha levent" + ev);
        FacesMessage message1 = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
        addMessage(message1);
    }
//
//    public void onEventResize(ScheduleEntryResizeEvent event) {
//        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());
//
//        addMessage(message);
//    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    /*getter and setter*/
    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public void setEventModel(ScheduleModel eventModel) {
        this.eventModel = eventModel;
    }

    public DefaultScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(DefaultScheduleEvent event) {
        this.event = event;
    }

    public Patient getPatient() {
        if (patient == null) {
            patient = new Patient();
        }
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
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

    public Medecin getMedecinChoisis() {
 Employe employe = (Employe) util.Session.getAttribut("UserEmploye");
        String type = employeFacade.findUserFonction(employe);
        if (type.equals("MedecinSecretaire")) {
            MedecinSecretaire ms = (MedecinSecretaire) util.Session.getAttribut("UserEmploye");
            medecinChoisis = ms.getMedecin();
        } else {
            medecinChoisis = (Medecin) util.Session.getAttribut("UserEmploye");
        }
        System.out.println("schedule.medecinChoisis------------------->"+medecinChoisis);
        return medecinChoisis;
    }

    public void setMedecinChoisis(Medecin medecinChoisis) {
        this.medecinChoisis = medecinChoisis;
    }

    public List<NoteService> getNoteServices() {
        noteServices = noteServiceFacade.findNoteByMedecin(medecinChoisis);
        return noteServices;
    }

    public void setNoteServices(List<NoteService> noteServices) {
        this.noteServices = noteServices;
    }

    public int getValidite() {
        return validite;
    }

    public void setValidite(int validite) {
        this.validite = validite;
    }

    public List<String> getDateDeMarge() {

        System.out.println("medecinChoisis--->" + medecinChoisis);
        dateDeMarge = margeBloquanteFacade.dateMarge(medecinChoisis);
        System.out.println("dateDeMarge --->" + dateDeMarge);

        return dateDeMarge;
    }

    public void setDateDeMarge(List<String> dateDeMarge) {
        this.dateDeMarge = dateDeMarge;
    }

    public String getDateString() {

        for (int i = 0; i < getDateDeMarge().size(); i++) {

            dateString += dateDeMarge.get(i);
            dateString += ",";

        }
        System.out.println("dateString---->" + dateString);
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String dateMargeString() {
        String str = "";
        dateDeMarge = margeBloquanteFacade.dateMarge(medecinChoisis);
        if (dateDeMarge != null) {
            for (int i = 0; i < getDateDeMarge().size(); i++) {

                str += dateDeMarge.get(i);
                str += ",";

            }
        }
        System.out.println("dateString---->" + dateString);
        return str;
    }

    public List<Rdv> getRdvs() {
        return rdvs;
    }

    public void setRdvs(List<Rdv> rdvs) {
        this.rdvs = rdvs;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NoteService getNoteService() {
        if (noteService != null) {
            noteService = new NoteService();
        }
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

}
