/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import bean.Employe;
import bean.Medecin;
import bean.Patient;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ACER
 */
public class ImageServlet extends HttpServlet {

    private static final long serialVersionUID = -6449908958106497977L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requette = "SELECT img FROM ";
        Employe employe = (Employe) util.Session.getAttribut("UserEmploye");
        if (employe != null) {
            requette += " Employe WHERE id='" + employe.getId() + "'";
        } else {
            Patient p = (Patient) util.Session.getAttribut("UserPatient");
            requette += " Patient WHERE id='" + p.getId() + "'";
        }
        // Get last uploaded image
        try {
            // Image bytes
            byte[] imageBytes = null;

            // Load driver
            Class.forName("com.mysql.jdbc.Driver");

            // Connect to the database
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ProjetAgenda?zeroDateTimeBehavior=convertToNull", "root", "");

            // Create the statement
            // This query is specific to MySQL, it returns only one row (using 'LIMIT 1') - the last uploaded file
            PreparedStatement statement = connection.prepareStatement(requette); // icii tu modifie ta requtte 
            ResultSet rs = statement.executeQuery();
            while (rs.next()) { // This will run only once
                imageBytes = rs.getBytes("img");
            }

            // Close the connection
            connection.close();
           
            resp.getOutputStream().write(imageBytes);//mettre limage dans le view /image
            resp.getOutputStream().close();

        } catch (IOException | ClassNotFoundException | SQLException e) {
            // Display error message
            resp.getWriter().write(e.getMessage());
            resp.getWriter().close();
        }

    }
}
