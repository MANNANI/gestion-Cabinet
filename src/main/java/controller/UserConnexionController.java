package controller;

import bean.Demande;
import bean.Device;
import bean.Employe;
import bean.Patient;
import bean.UserConnexion;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.UserConnexionFacade;

import java.io.Serializable;
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
import javax.mail.MessagingException;
import service.DemandeFacade;
import service.DeviceFacade;
import service.EmployeFacade;

@Named("userConnexionController")
@SessionScoped
public class UserConnexionController implements Serializable {

    @EJB
    private service.UserConnexionFacade ejbFacade;
    @EJB
    private DemandeFacade demandeFacade;
    @EJB
    private DeviceFacade deviceFacade;
    @EJB
    private EmployeFacade employeFacade;
    private List<UserConnexion> items = null;
    private List<Device> devices = null;
    private UserConnexion selected;
    private Patient selectedPat;
    private String nvPW;
    private String login;
    private Demande demande;
    private String mail;

    public UserConnexionController() {
    }

    //somaya
    public void modifier2(){
        ejbFacade.ModifierMp2(selected, nvPW);
    }
    public String connectionUser() {
        selected = getFacade().connecter(selected);
        if (selected != null) {
            String type = employeFacade.findUserFonction(selected.getEmploye());
            if (type.equals("Admin")) {
               deviceFacade.creerDevice(selected, "Employe"); 
                util.Session.setAttribut("UserEmploye", selected.getEmploye());
                return "/acceuilAdmin/Profile.xhtml";
            }
            if (type.equals("AdminSecretaire")) {
               deviceFacade.creerDevice(selected, "Employe"); 
                util.Session.setAttribut("UserEmploye", selected.getEmploye());
                return "/acceuilAdminSec/Profile.xhtml";
            }
            if (type.equals("Medecin")) {
               deviceFacade.creerDevice(selected, "Employe"); 
                util.Session.setAttribut("UserEmploye", selected.getEmploye());
                return "/acceuilMedecin/Profile.xhtml";
            }
            if (type.equals("MedecinSecretaire")) {
               deviceFacade.creerDevice(selected, "Employe"); 
                util.Session.setAttribut("UserEmploye", selected.getEmploye());
                return "/acceuilMedecinSec/Profile.xhtml";
            }
            if (type.equals("Patient")) {
               deviceFacade.creerDevice(selected, "Patient"); 
                util.Session.setAttribut("UserPatient", selected.getPatient());
                return "/acceuilPatient/Profile.xhtml";
            }
        }
        return null;

    }
       public void demandeCreation() {
        System.out.println("haa la demande" + demande);
        System.out.println("haa la fonction dyaloi " + demande.getFonction());
        System.out.println("haa le mail " + demande.getEmail());
        demandeFacade.create(demande);
        demande = new Demande();
    }

    public void setLogin(String loginNew) {
        System.out.println("ha le login mne lview " + loginNew + " ha dyal controller avant " + login);
        this.login = loginNew;
        System.out.println("haa le login apres" + login);
    }

    public String inscri() {
        return "/userConnexion/Connexion.xhtml";
    }

     public String gestionMenu() {
        Employe emp = (Employe) util.Session.getAttribut("UserEmploye");
        if (emp != null) {
            return employeFacade.findUserFonction(emp);
        } else {
            Patient p = (Patient) util.Session.getAttribut("UserPatient");
            return "Patient";
        }
    }

    public String find() {
        Employe emp = (Employe) util.Session.getAttribut("UserEmploye");
        if (emp != null) {
            return emp.getNom();
        } else {
            Patient p = (Patient) util.Session.getAttribut("UserPatient");
            return p.getNom();
        }
    }

    public String findPrenom() {
        Employe emp = (Employe) util.Session.getAttribut("UserEmploye");
        if (emp != null) {
            return emp.getPrenom();
        } else {
            Patient p = (Patient) util.Session.getAttribut("UserPatient");
            return p.getPrenom();
        }
    }

    public String fonction() {
        if (selected.getEmploye() != null) {
            return employeFacade.findUserFonction(selected.getEmploye());
        } else {
            return "Patient";
        }

    }

    public Date findDateInscription() {
        Employe emp = (Employe) util.Session.getAttribut("UserEmploye");
        if (emp != null) {
            return deviceFacade.findDateInscription(emp.getUserConnexion());
        } else {
            Patient p = (Patient) util.Session.getAttribut("UserPatient");
            return deviceFacade.findDateInscription(p.getUserConnexion());
        }

    }
//    public void mdpOublie(){
//        /*envoyer un mail*/
//     RequestContext.getCurrentInstance().execute("PF('MdpOublie').show()"); 
//    }

    public void mdpOublieFerme() throws MessagingException {
        /*envoyer un mail*/
        System.out.println("haa le mail" + mail);
        util.EmailUtil.sendMail("projetcabinet28@gmail.com", "test123456/", "votre nouveau mdp est ", mail, "mdp");
        System.out.println("hani f mdp envoyer au mail du client");
    }

    public Patient findPatient() {
        return (Patient) util.SessionUtil.getAttribute("UserPatient");
    }

    public String deconnection() {
        Device device = (Device) util.Session.getAttribut("Device");
        Date system = new Date();
        device.setDateSortie(system);
        deviceFacade.edit(device);
        util.Session.clear();
        selected = new UserConnexion();
        System.out.println("Etat deconnection");
        return "/userConnexion/Connexion.xhtml";
    }
       public UserConnexion prepareCreate() {
        selected = new UserConnexion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UserConnexionCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UserConnexionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UserConnexionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<UserConnexion> getItems() {
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

    public UserConnexion getUserConnexion(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<UserConnexion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<UserConnexion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = UserConnexion.class)
    public static class UserConnexionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UserConnexionController controller = (UserConnexionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "userConnexionController");
            return controller.getUserConnexion(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.String value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof UserConnexion) {
                UserConnexion o = (UserConnexion) object;
                return getStringKey(o.getLogin());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), UserConnexion.class.getName()});
                return null;
            }
        }

    }
     public List<Device> getDevices() {
        if (selected != null) {
            devices = deviceFacade.findByUser(selected);
        }
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Demande getDemande() {
        if (demande == null) {
            demande = new Demande();
        }
        return demande;
    }

    public void setDemande(Demande demande) {
        this.demande = demande;
    }

    public String getLogin() {
        return login;
    }
    public String getNvPW() {
        return nvPW;
    }

    public void setNvPW(String nvPW) {
        this.nvPW = nvPW;
    }

    public UserConnexion getSelected() {
        if (selected == null) {
            selected = new UserConnexion();
        }
        return selected;
    }

    public void setSelected(UserConnexion selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UserConnexionFacade getFacade() {
        return ejbFacade;
    }

    
    //    public void choixTableau() {
//        String type = fonction();
//        if (type.equals("Patient")) {
//            System.out.println("ana an3ayate l set Patient");
//            setPatients();
//        } else {
//            System.out.println("ana an3ayate l set employe");
//
//            setEmployes();
//        }
//
//    }

//     @Override
//    public UserConnexion find(Object id) {
//
//        try {
//            UserConnexion user = (UserConnexion) em.createQuery("select u from UserConnexion u where u.login = '" + id + "'").getSingleResult();
//            if (user != null) {
//                return user;
//            }
//        } catch (Exception e) {
//            JsfUtil.addErrorMessage("Login incorrect");
//        }
//        return null;
//    }
//    public List<Patient> getPatients() {
//        if (selectedPat != null) {
//            System.out.println("hanii f getPatient");
//            List<Patient> pat = ejbFacade.findPatient(selected);
//        }
//        return patients;
//    }

//    public void setPatients() {
//        System.out.println("haa selected" + selected);
//        List<Patient> pa = ejbFacade.findPatient(selected);
//        this.patients = pa;
//        System.out.println("haa pat" + pa);
//    }
//
//    public List<Employe> getEmployes() {
//        if (selected != null) {
//            System.out.println("hanii f getEmploye");
//            List<Employe> emp = ejbFacade.findEmploye(selected);
//            return emp;
//        }
//        return employes;
//    }
//
//    public void setEmployes() {
//        System.out.println("haa selected" + selected);
//        List<Employe> emp = ejbFacade.findEmploye(selected);
//        this.employes = emp;
//        System.out.println("haa emp" + emp);
//    }

//      public void creerDevice(UserConnexion user, String type) {
//        Device device = new Device();
//        Date system = new Date();
//        device.setId(deviceFacade.generateId("Device", "id"));
//        device.setDateEntree(system);
//        if (type.equals("Employe")) {
//            device.setNom(user.getEmploye().getNom());
//        } else {
//            device.setNom(user.getPatient().getNom());
//        }
//        device.setUserConnexion(user);
//        device = deviceFacade.findMac(device);
//        deviceFacade.create(device);
//        util.Session.setAttribut("Device", device);
//    }
}
