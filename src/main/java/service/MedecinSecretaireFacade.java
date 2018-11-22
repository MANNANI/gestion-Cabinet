/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import bean.Employe;
import bean.Medecin;
import bean.MedecinSecretaire;
import bean.UserConnexion;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author houda
 */
@Stateless
public class MedecinSecretaireFacade extends AbstractFacade<MedecinSecretaire> {

    @PersistenceContext(unitName = "com.mycompany_ProjetAgenda_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @EJB
    private UserConnexionFacade userConnexionFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MedecinSecretaireFacade() {
        super(MedecinSecretaire.class);
    }

    public List<MedecinSecretaire> medecinSecretaireByMedecin(Medecin medecin) {
        String requer = "";
        if (medecin != null) {
            requer = "SELECT ms FROM MedecinSecretaire ms WHERE 1=1";
            requer += util.DateUtil.addConstraint("ms", "medecin.id", "=", medecin.getId());
            return getEntityManager().createQuery(requer).getResultList();
        }
        return null;
    }

    public void deleteSec(Medecin get) {
        List<MedecinSecretaire> notes = medecinSecretaireByMedecin(get);
        for (MedecinSecretaire note : notes) {
            //supprimer le compte de medecin sec
            List<UserConnexion> user = em.createQuery("select n.userConnexion FROM MedecinSecretaire n WHERE n.id='" + note.getId() + "'").getResultList();
            userConnexionFacade.remove(user.get(0));
            //remove medSec
            remove(note);
        }
    }

    public void deleteSecByEmpl(Employe employe) {
        List<MedecinSecretaire> sec = em.createQuery("select n FROM MedecinSecretaire n WHERE n.id='" + employe.getId() + "'").getResultList();
        UserConnexion user = userConnexionFacade.find(sec.get(0).getUserConnexion().getLogin());
        sec.get(0).setUserConnexion(null);
        userConnexionFacade.remove(user);
        remove(sec.get(0));
    }

}
