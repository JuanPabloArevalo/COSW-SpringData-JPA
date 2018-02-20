package edu.eci.cosw.samples;


import edu.eci.cosw.jpa.sample.model.Consulta;
import edu.eci.cosw.jpa.sample.model.Paciente;
import edu.eci.cosw.jpa.sample.model.PacienteId;
import edu.eci.cosw.samples.persistence.PatientsRepository;
import edu.eci.cosw.samples.services.PatientServices;
import edu.eci.cosw.samples.services.ServicesException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.Transactional;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringDataRestApiApplication.class)
@WebAppConfiguration
@ActiveProfiles("**test**")
public class SpringDataRestApiApplicationTests {
    
    @Autowired
    PatientServices services = null;
    @Autowired
    PatientsRepository patiensRepository = null;
        
    @Test
    public void contextLoads() {
    }

    @Test
    @Transactional
    public void patientExistTest(){
//Prueba 1 Consulta a paciente que existe  
//Registrar un paciente, consultarlo a través de los servcios, y rectificar que sea el mismo
        PacienteId pacienteID = new PacienteId(2087559, "cc");
        Paciente paciente = new Paciente(pacienteID, "Juan Pablo Arévalo Merchan", new Date());
        patiensRepository.save(paciente);
        
        try {
            Paciente pacienteConsulta = services.getPatient(2087559, "cc");
            Assert.assertEquals(pacienteConsulta.getId().getId()+pacienteConsulta.getId().getTipoId(),paciente.getId().getId()+paciente.getId().getTipoId());
        } catch (ServicesException ex) {
            Logger.getLogger(SpringDataRestApiApplicationTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    @Transactional
    public void patientNotExistTest(){
//Prueba 2 Consulta a paciente que no existe
//Consultar a través de los servicios un paciente no registrado, y esperar que se produzca el error | 
        try {
            Paciente pacienteConsulta = services.getPatient(2087558, "cd");
            System.out.println("Existe: "+pacienteConsulta);
            Assert.assertNull(pacienteConsulta);
        } catch (ServicesException ex) {
            Logger.getLogger(SpringDataRestApiApplicationTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
