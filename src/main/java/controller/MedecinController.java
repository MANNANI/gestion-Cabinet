package controller;

import bean.Configuration;
import bean.Medecin;
import bean.MedecinSecretaire;
import bean.NoteService;
import bean.Patient;
import bean.UserConnexion;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.MedecinFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("medecinController1")
@SessionScoped
public class MedecinController implements Serializable {

     @EJB
    private service.MedecinFacade ejbFacade;
    @EJB
    private service.NoteServiceFacade noteServiceFacade;
    @EJB
    private service.UserConnexionFacade connexionFacade;
    @EJB
    private service.ConfigurationFacade configurationFacade;
    @EJB
    private service.MedecinSecretaireFacade medecinSecretaireFacade;
   
    private List<Medecin> items = null;
    private Medecin selected;

    // declaration pour les password
    private boolean show;
    private String anciennePassword;
    private String nouveauPassword;
    private String confirmationDeNouveauPassword;
    private Configuration configuration;
    private MedecinSecretaire medecinSecretaire;
    private List<MedecinSecretaire> medecinSecretaireItems;
    private String noteDesc;
    private UserConnexion userConnexion;

     private List<Patient> patient;

    public List<Patient> getPatient() {
        if(patient==null){
            patient = new ArrayList<>();
        }
        return patient;
    }

    public void setPatient(List<Patient> patients) {
        this.patient = patients;
    }
    
    public void patientsDetail(Medecin m){
        patient = getFacade().patientByMedecin(m);
    }
    
    
    public MedecinController() {
    }

    public Medecin getSelected() {

        selected = (Medecin) util.Session.getAttribut("UserEmploye");
        System.out.println("selected value ---> " + selected);
       
        return selected;
    }

    public void setSelected(Medecin selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MedecinFacade getFacade() {
        return ejbFacade;
    }

    public String choixMedecin() {
        util.SessionUtil.setAttribute("medecinChoisis", selected);
        return "/calendar/Agenda.xhtml";
    }

    public void recherche() {
        items.clear();
        items = getFacade().recherche(selected.getSpecialite(), selected.getResidence(), selected.getNom(), selected.getPrenom(), selected.getTel(), selected.getEmail());
    }

    public Medecin prepareCreate() {
        selected = new Medecin();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MedecinCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MedecinUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MedecinDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Medecin> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Medecin getMedecin(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Medecin> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Medecin> getItemsAvailableSelectOne() {
       
        return getFacade().findAll();
    }
    //getter & setter

    public String getAnciennePassword() {
        return anciennePassword;
    }

    public void setAnciennePassword(String anciennePassword) {
        this.anciennePassword = anciennePassword;
    }

    public String getNouveauPassword() {
        return nouveauPassword;
    }

    public void setNouveauPassword(String nouveauPassword) {
        this.nouveauPassword = nouveauPassword;
    }

    public String getConfirmationDeNouveauPassword() {
        return confirmationDeNouveauPassword;
    }

    public void setConfirmationDeNouveauPassword(String confirmationDeNouveauPassword) {
        this.confirmationDeNouveauPassword = confirmationDeNouveauPassword;
    }

    public Configuration getConfiguration() {
        if (configuration == null) {
          
            configuration = configurationFacade.find(selected.getConfiguration().getId());
            
              System.out.println("haa la Config "+configuration);
              
        }
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public MedecinSecretaire getMedecinSecretaire() {
        if (medecinSecretaire == null) {
            medecinSecretaire = new MedecinSecretaire();
        }
        return medecinSecretaire;
    }

    public void setMedecinSecretaire(MedecinSecretaire medecinSecretaire) {
        this.medecinSecretaire = medecinSecretaire;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getNoteDesc() {
        if (noteDesc == null) {
            noteDesc = "";
        }
        return noteDesc;
    }

    public void setNoteDesc(String noteDesc) {
        this.noteDesc = noteDesc;
    }
    
    public List<MedecinSecretaire> getMedecinSecretaireItems() {
        medecinSecretaireItems = medecinSecretaireFacade.medecinSecretaireByMedecin(selected);
        return medecinSecretaireItems;
    }

    public void setMedecinSecretaireItems(List<MedecinSecretaire> medecinSecretaireItems) {
        this.medecinSecretaireItems = medecinSecretaireItems;
    }

    public UserConnexion getUserConnexion() {
        if(userConnexion == null){
            userConnexion = new UserConnexion();
        }
        return userConnexion;
    }

    public void setUserConnexion(UserConnexion userConnexion) {
        this.userConnexion = userConnexion;
    }
    
    //methode
    public void changePasseWord() {
        if (selected != null) {
            if (selected.getUserConnexion() != null) {
                if (selected.getUserConnexion().getPassword().equals(anciennePassword) && nouveauPassword.equals(confirmationDeNouveauPassword)) {
                    UserConnexion user = selected.getUserConnexion();
                    user.setPassword(nouveauPassword);
                    user.setMdpChanged(true);
                    System.out.println(" ha le user.is changed"+user.isMdpChanged());
                    connexionFacade.edit(user);
                    System.out.println("it works");
                    setShow(true);
                } else {
                    System.out.println("error");
                    setShow(false);
                }
            }
        }
    }

    public void editMedecin() {
        getFacade().edit(selected);
    }

    public void creationNote() {
        selected=(Medecin) util.Session.getAttribut("UserEmploye");
        NoteService note = new NoteService();
        if (!noteDesc.equals("")) {
            note.setExp(new Date());
            note.setNote(noteDesc);
            note.setMedecin(selected);
            noteServiceFacade.create(note);
            noteDesc = "";
        }
    }

    public void createConfiguration() {
        configuration.setMedecin(selected);
        configurationFacade.create(configuration);
    }
    
//    public void ctreateMedecinSecrutariant() {
//        System.out.println("i am here in ctreateMedecinSecrutariant methode");
//
//        medecinSecretaire.setMedecin(selected);
//        medecinSecretaire.setUserConnexion(userConnexion);
//        medecinSecretaire.setId(medecinSecretaireFacade.generateId("MedecinSecretaire", "id"));
////        System.out.println("medecinSecretaire ----> "+medecinSecretaire);
////        System.out.println("medecinSecretaireMedecin ----> "+medecinSecretaire.getMedecin());
////        System.out.println("medecinSecretaire ----> user"+userConnexion);
//        medecinSecretaireFacade.create(medecinSecretaire);
//        userConnexion.setEmploye(medecinSecretaire);
//        connexionFacade.create(userConnexion);
//        medecinSecretaire = new MedecinSecretaire();
//        userConnexion = new UserConnexion();
//    }
    
    public void ctreateMedecinSecrutariant() {
        System.out.println("i am here in ctreateMedecinSecrutariant methode");
        medecinSecretaire.setMedecin(selected);
        medecinSecretaire.setUserConnexion(userConnexion);
        medecinSecretaire.setId(medecinSecretaireFacade.generateId("MedecinSecretaire", "id"));
//        System.out.println("medecinSecretaire ----> "+medecinSecretaire);
//        System.out.println("medecinSecretaireMedecin ----> "+medecinSecretaire.getMedecin());
//        System.out.println("medecinSecretaire ----> user"+userConnexion);
        userConnexion.setEmploye(medecinSecretaire);
        medecinSecretaireFacade.create(medecinSecretaire);
//        connexionFacade.create(userConnexion);
        medecinSecretaire = new MedecinSecretaire();
        userConnexion = new UserConnexion();
    }

    public String signOut() {
        medecinSecretaireItems = null;
        return "/userConnexion/connexion.xhtml";
    }

    @FacesConverter("medecinControllerConverter")
    public static class MedecinControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MedecinController controller = (MedecinController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "medecinController1");
            return controller.getMedecin(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            System.out.println("string key =" + value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);

            System.out.println("Long key = " + value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Medecin) {
                Medecin o = (Medecin) object;
                System.out.println("Object=" + o);
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Medecin.class.getName()});
                return null;
            }
        }

    }


}
