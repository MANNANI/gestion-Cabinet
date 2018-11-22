package controller;

import bean.Medecin;
import bean.NoteService;
import controller.util.JsfUtil;
import controller.util.JsfUtil.PersistAction;
import service.NoteServiceFacade;

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

@Named("noteServiceController")
@SessionScoped
public class NoteServiceController implements Serializable {

    @EJB
    private MedecinFacade medecinFacade;
    @EJB
    private service.NoteServiceFacade ejbFacade;
    private List<NoteService> items = null;
    private NoteService selected;

    public NoteServiceController() {
    }

    public List<Medecin> comboMedecin() {
        System.out.println("ha les med " + medecinFacade.findAll());
        return medecinFacade.findAll();
    }

    public NoteService getSelected() {
        return selected;
    }

    public void setSelected(NoteService selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private NoteServiceFacade getFacade() {
        return ejbFacade;
    }

    public NoteService prepareCreate() {
        selected = new NoteService();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("NoteServiceCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("NoteServiceUpdated"));
    }

    public void destroy(NoteService noteService) {
        ejbFacade.remove(noteService);
    }

    public List<NoteService> getItems() {
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

    public NoteService getNoteService(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<NoteService> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<NoteService> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = NoteService.class)
    public static class NoteServiceControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            NoteServiceController controller = (NoteServiceController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "noteServiceController");
            return controller.getNoteService(getKey(value));
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
            if (object instanceof NoteService) {
                NoteService o = (NoteService) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), NoteService.class.getName()});
                return null;
            }
        }

    }

}
