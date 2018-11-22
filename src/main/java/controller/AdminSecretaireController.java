package controller;

import bean.Admin;
import bean.AdminSecretaire;
import bean.Device;
import bean.Employe;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.AdminSecretaireFacade;

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
import service.DeviceFacade;
import service.EmployeFacade;
import service.UserConnexionFacade;

@Named("adminSecretaireController")
@SessionScoped
public class AdminSecretaireController implements Serializable {

    @EJB
    private service.AdminSecretaireFacade ejbFacade;
    @EJB
    private DeviceFacade deviceFacade;
    @EJB
    private UserConnexionFacade userConnexionFacade;
    @EJB
    private EmployeFacade employeFacade;
    private List<AdminSecretaire> items = null;
    private List<Device> devices = null;
    private AdminSecretaire selected;
    private Date dateMin;
    private Date dateMax;
    private String typeBloquage;
    private String etatMdp;

    public AdminSecretaireController() {
    }

    public List<Device> getDevices() {
        if (selected != null) {
            devices = deviceFacade.findByUser(selected.getUserConnexion());
        }
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public String getEtatMdp() {
        return etatMdp;
    }

    public void setEtatMdp(String etatMdp) {
        this.etatMdp = etatMdp;
    }

    public String getTypeBloquage() {
        return typeBloquage;
    }

    public void setTypeBloquage(String typeBloquage) {
        this.typeBloquage = typeBloquage;
    }

    public Date getDateMin() {
        return dateMin;
    }

    public void setDateMin(Date dateMin) {
        this.dateMin = dateMin;
    }

    public Date getDateMax() {
        System.out.println("haa la date Max=>" + dateMax);
        return dateMax;
    }

    public void setDateMax(Date dateMax) {
        this.dateMax = dateMax;
    }

    public AdminSecretaire getSelected() {
        Employe employe=(Employe) util.Session.getAttribut("UserEmploye");
        String type = employeFacade.findUserFonction(employe);
        if(type.equals("AdminSecretaire")){
            selected =(AdminSecretaire) employe;
        }
        
        if (selected == null) {
            selected = new AdminSecretaire();
        }
       
        return selected;
    }

    public void setSelected(AdminSecretaire selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private AdminSecretaireFacade getFacade() {
        return ejbFacade;
    }

    public AdminSecretaire prepareCreate() {
        selected = new AdminSecretaire();
        initializeEmbeddableKey();
        return selected;
    }
    //    methodeeee

    /*reinitisaliser les attribut de recherche ( clone )*/
    public void clearRechercheAttribut() {
        selected = new AdminSecretaire();
        dateMax = null;
        dateMin = null;
        etatMdp = null;
        typeBloquage = null;
    }

    /* bloquer un user*/
    public void bloquer() {
        if (selected.getUserConnexion() != null) {
            selected.getUserConnexion().setBlocked(1);
            userConnexionFacade.edit(selected.getUserConnexion());
        } else {
            /*afficher un message*/
            System.out.println("po de user connexion ");
        }
    }

    /*debloquer un user*/
    public void debloquer() throws MessagingException {
        if (selected.getUserConnexion() != null) {
            selected.getUserConnexion().setBlocked(0);
            selected.getUserConnexion().setPassword(util.HashageUtil.genererMdp());
            selected.getUserConnexion().setMdpChanged(false);
            userConnexionFacade.edit(selected.getUserConnexion());
            util.EmailUtil.sendMail("projetcabinet28@gmail.com", "test123456/", util.EmailUtil.recupererMdp(selected.getUserConnexion().getPassword(), selected.getNom()), selected.getEmail(), "Changement de mot de passe");
        } else {
            /*afficher un message*/
            System.out.println("po de user connexion");
        }
    }

    /* profil de secreatire medecin */
    public String detailSec() {
        util.Session.setAttribut("adminSec", selected);
        return "/profileSec.xhtml";
    }

    public void recherche() {
        System.out.println("hani f rech");
        selected.setAdmin((Admin) util.Session.getAttribut("UserEmploye"));
        items = ejbFacade.recherche(selected, dateMin, dateMax, typeBloquage, etatMdp);
        clearRechercheAttribut();
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AdminSecretaireCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AdminSecretaireUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("AdminSecretaireDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<AdminSecretaire> getItems() {
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

    public AdminSecretaire getAdminSecretaire(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<AdminSecretaire> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<AdminSecretaire> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = AdminSecretaire.class)
    public static class AdminSecretaireControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AdminSecretaireController controller = (AdminSecretaireController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "adminSecretaireController");
            return controller.getAdminSecretaire(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof AdminSecretaire) {
                AdminSecretaire o = (AdminSecretaire) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), AdminSecretaire.class.getName()});
                return null;
            }
        }

    }

}
