package controller;

import bean.Demande;
import bean.Medecin;
import bean.MedecinSecretaire;
import bean.UserConnexion;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import java.io.File;
import java.io.IOException;
import service.MedecinSecretaireFacade;

import java.io.Serializable;
import java.nio.file.Files;
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

@Named("medecinSecretaireController")
@SessionScoped
public class MedecinSecretaireController implements Serializable {

    @EJB
    private service.UserConnexionFacade connexionFacade;
    @EJB
    private service.DemandeFacade demandeFacade;
    @EJB
    private service.MedecinSecretaireFacade ejbFacade;
    private List<MedecinSecretaire> items = null;
    private MedecinSecretaire selected;
    private MedecinSecretaire selectedSec;
    private String anciennePassword;
    private String nouveauPassword;
    private String confirmationDeNouveauPassword;
   

    public MedecinSecretaireController() {
    }

    public MedecinSecretaire getSelectedSec() {
        if(selectedSec==null){
            selectedSec=new MedecinSecretaire();
        }
        return selectedSec;
    }

    public void setSelectedSec(MedecinSecretaire selectedSec) {
        this.selectedSec = selectedSec;
    }

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

    public MedecinSecretaire getSelected() {
        selected = (MedecinSecretaire) util.Session.getAttribut("UserEmploye");
        if (selected == null) {
            selected = new MedecinSecretaire();
        }
        return selected;
    }

    public void setSelected(MedecinSecretaire selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MedecinSecretaireFacade getFacade() {
        return ejbFacade;
    }

    public void changePasseWord() {
        if (selected != null) {
            if (selected.getUserConnexion() != null) {
                System.out.println("anciennePassword----->" + anciennePassword);
                System.out.println("nouveauPassword----->" + nouveauPassword);
                if (selected.getUserConnexion().getPassword().equals(anciennePassword) && nouveauPassword.equals(confirmationDeNouveauPassword)) {
                    UserConnexion user = selected.getUserConnexion();
                    user.setPassword(nouveauPassword);
                    user.setMdpChanged(true);
                    connexionFacade.edit(user);
                    System.out.println("it works");
                    anciennePassword = "";
                    nouveauPassword = "";
                    confirmationDeNouveauPassword = "";
                } else {
                    System.out.println("error");
                }
            }
        }
    }

    public MedecinSecretaire prepareCreate() {
        selected = new MedecinSecretaire();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() throws IOException, MessagingException {
        Medecin med = (Medecin) util.Session.getAttribut("UserEmploye");
        selectedSec.setMedecin(med);
        Demande demande = new Demande();
        demande.setId(demandeFacade.generateId("Demande","id"));
        demande.setEmail(selectedSec.getEmail());
        demande.setFonction("MedecinSecretaire");
        demande.setNom(selectedSec.getNom());
        demande.setPrenom(selectedSec.getPrenom());
       demandeFacade.creerCompteHachage(demande);
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MedecinSecretaireUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MedecinSecretaireDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<MedecinSecretaire> getItems() {
        if (items == null) {
            Medecin medecin = (Medecin) util.Session.getAttribut("UserEmploye");
            items = getFacade().medecinSecretaireByMedecin(medecin);
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                Medecin med = (Medecin) util.Session.getAttribut("UserEmploye");
                selected.setMedecin(med);
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

    public MedecinSecretaire getMedecinSecretaire(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<MedecinSecretaire> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<MedecinSecretaire> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = MedecinSecretaire.class)
    public static class MedecinSecretaireControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MedecinSecretaireController controller = (MedecinSecretaireController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "medecinSecretaireController");
            return controller.getMedecinSecretaire(getKey(value));
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
            if (object instanceof MedecinSecretaire) {
                MedecinSecretaire o = (MedecinSecretaire) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), MedecinSecretaire.class.getName()});
                return null;
            }
        }

    }

}
