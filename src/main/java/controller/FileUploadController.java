package controller;

import bean.Employe;
import bean.Patient;
import java.io.IOException;
import java.io.InputStream;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.poi.util.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import service.EmployeFacade;
import service.PatientFacade;

@Named("fileUploadController")
@SessionScoped
public class FileUploadController implements Serializable {

    @EJB
    private EmployeFacade employeFacade;
    @EJB
    private PatientFacade patientFacade;
    private UploadedFile file;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }


    public void handleFileUpload(FileUploadEvent event) throws IOException {
        if (event.getFile() != null) {
            byte[] photo = IOUtils.toByteArray(event.getFile().getInputstream());
            Employe employe = (Employe) util.Session.getAttribut("UserEmploye");
            if (employe != null) {
                employe.setImg(photo);
                employeFacade.edit(employe);
            } else {
                Patient p = (Patient) util.Session.getAttribut("UserPatient");
                p.setImg(photo);
                patientFacade.edit(p);
            }
        }
    }

}
