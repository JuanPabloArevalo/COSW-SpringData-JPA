package edu.eci.cosw.samples;


import edu.eci.cosw.jpa.sample.model.Consulta;
import edu.eci.cosw.jpa.sample.model.Paciente;
import edu.eci.cosw.jpa.sample.model.PacienteId;
import edu.eci.cosw.samples.persistence.PatientsRepository;
import edu.eci.cosw.samples.services.PatientServices;
import edu.eci.cosw.samples.services.ServicesException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
@ActiveProfiles("test")
public class SpringDataRestApiApplicationTests {
    
    @Autowired
    PatientServices services = null;
    @Autowired
    PatientsRepository patiensRepository = null;
        
    @Test
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
            Assert.fail("Error patientExistTest");
        }
    }
    
    @Test
    public void patientNotExistTest(){
        try {
//Prueba 2 Consulta a paciente que no existe
//Consultar a través de los servicios un paciente no registrado, y esperar que se produzca el error
            Paciente pacienteConsulta = services.getPatient(1124, "CC");
            Assert.assertNull(pacienteConsulta);
        } catch (ServicesException ex) {
            Logger.getLogger(SpringDataRestApiApplicationTests.class.getName()).log(Level.SEVERE, null, ex);
            Assert.fail("Error patientNotExistTest");

        }
    }
    
    
    @Test
    public void notExistPatientWithNConsultsTest(){
//No existen pacientes con N o más consultas
//Registrar un paciente con sólo 1 consulta. Probar usando N=2 como parámetro y esperar una lista vacía.
        
    // Creando las consultas
        Consulta c = new Consulta(new Date(), "Examen de los ojos");
        Set<Consulta> consultas = new HashSet<>(0);
        consultas.add(c);

    // Creando el paciente
        PacienteId pid = new PacienteId(2087559, "cc");
        Paciente p = new Paciente(pid, "Juan Pablo Arévalo Merchán", new Date(), consultas);
        patiensRepository.save(p);

        // Obteniendo pacientes con al menos N = 2 consultas.
        int n = 2;
        try {
            List<Paciente> pacientes = services.topPatients(n);
            Assert.assertTrue(pacientes.isEmpty());
        } catch (ServicesException ex) {
            Assert.fail("Error notExistPatientWithNConsultsTest");
        }
    }    
    
    @Test
    public void validateTopPatientTest(){
//Registrar 3 pacientes. Uno sin consultas, otro con una, y el último con dos consultas. 
//Probar usando N=1  y esperar una lista con los dos pacientes correspondientes
        
    // Creando las consultas
        Consulta c = new Consulta(new Date(), "Examen de los ojos");
        Consulta c2 = new Consulta(new Date(), "Examen de la cabeza");

    // Creando el paciente Sin consultas
        PacienteId pid = new PacienteId(2087559, "cc");
        Paciente p = new Paciente(pid, "Juan Pablo Arévalo Merchán", new Date(), null);
        
       
        Set<Consulta> consultas1 = new HashSet<>(0);
        consultas1.add(c);
        
    // Creando el paciente con 1 consulta
        PacienteId pid2 = new PacienteId(2087558, "cd");
        Paciente p2 = new Paciente(pid2, "Paciente 1 Consulta", new Date(), consultas1);
        
        
        Set<Consulta> consultas2 = new HashSet<>(0);
        consultas2.add(c);
        consultas2.add(c2);
        
    // Creando el paciente con 2 consultas
        PacienteId pid3 = new PacienteId(2087557, "c7");
        Paciente p3 = new Paciente(pid3, "Paciente 2 Consultas", new Date(), consultas2);
        
        
        patiensRepository.save(p);  
        patiensRepository.save(p2);
        patiensRepository.save(p3);
        patiensRepository.flush();
       
        // Obteniendo pacientes con al menos N = 1 consultas.
        int n = 1;
        try {
            List<Paciente> pacientes = services.topPatients(n);
             System.err.println("TAMAÑO: "+pacientes.size());
            Assert.assertTrue(pacientes.size()==2);
        } catch (ServicesException ex) {
            Assert.fail("Error validateTopPatientTest");
        }
    }  

}
