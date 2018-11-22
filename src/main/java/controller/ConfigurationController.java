package controller;

import bean.Configuration;
import bean.Medecin;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.ConfigurationFacade;

import java.io.Serializable;
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
import service.MedecinFacade;

@Named("configurationController")
@SessionScoped
public class ConfigurationController implements Serializable {

    @EJB
    private service.ConfigurationFacade ejbFacade;
    @EJB
    private MedecinFacade medecinFacade;
    private List<Configuration> items = null;
    private Configuration selected;

    public ConfigurationController() {
    }

    public Configuration getSelected() {
        if(selected==null){
             Medecin medecin =(Medecin) util.Session.getAttribut("UserEmploye");
            selected=getFacade().findByMedecin(medecin);
        }
        return selected;
    }

    public void setSelected(Configuration selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ConfigurationFacade getFacade() {
        return ejbFacade;
    }

    public Configuration prepareCreate() {
        selected = new Configuration();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ConfigurationCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ConfigurationUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ConfigurationDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Configuration> getItems() {
        if (items == null) {
           items=ejbFacade.findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().create(selected);
                    System.out.println("haa la confi "+selected+" ha le med "+selected.getMedecin());
                    selected.getMedecin().setConfiguration(selected);
                           System.out.println("ha le med ars mod"+selected.getMedecin().getConfiguration());
                   medecinFacade.edit(selected.getMedecin());
                           
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

    public Configuration getConfiguration(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Configuration> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Configuration> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Configuration.class)
    public static class ConfigurationControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ConfigurationController controller = (ConfigurationController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "configurationController");
            return controller.getConfiguration(getKey(value));
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
            if (object instanceof Configuration) {
                Configuration o = (Configuration) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Configuration.class.getName()});
                return null;
            }
        }

    }

}
