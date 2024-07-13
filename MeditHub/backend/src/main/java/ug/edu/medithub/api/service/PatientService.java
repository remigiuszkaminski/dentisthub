package ug.edu.medithub.api.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ug.edu.medithub.api.models.Order;
import ug.edu.medithub.api.models.Patient;
import ug.edu.medithub.api.repository.PatientRepository;

import java.security.Key;
import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final EmailSenderService emailSenderService;
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);




    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository, EmailSenderService emailSenderService) {
        this.patientRepository = patientRepository;
        this.emailSenderService = emailSenderService;
    }

    public List<Patient> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients;
    }

    public ResponseEntity<String> addPatient(Patient patient) {
        try {
            List<Patient> patientOptional = patientRepository.findByEmail(patient.getEmail());
            if (patientOptional.size() > 0) {
                return new ResponseEntity<>("PATIENT ARLEADY EXISTS", HttpStatus.CONFLICT);
            }
            patientRepository.save(patient);
            return new ResponseEntity<>("Patient registered succesfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Patient registration failed", HttpStatus.INTERNAL_SERVER_ERROR);



        }
    }

    public ResponseEntity<String> addOrderForPatient(String patientId, Order order, String authorizationHeader) {
        try {
            Optional<Patient> patientOptional = patientRepository.findById(patientId);
            if (patientOptional.isEmpty()) {
                return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
            }
            Patient patient = patientOptional.get();
            patient.getOrders().add(order);
            patientRepository.save(patient);

            String description = order.getDescription();
            String doctorName = order.getDoctorName();
            String date = order.getDate();
            String time = order.getTime();


            String subject = "Termin wizyty u doktora";
            String text = "Witaj, " + patient.getName() + "!\n\n" +
                    "Przypominamy o nadchodzącej wizycie u doktora " + doctorName + ".\n" +
                    "Data: " + date + "\n" +
                    "Godzina: " + time + "\n" +
                    "Opis: " + description + "\n\n" +
                    "Pamiętaj o punktualności!\n\n" +
                    "Pozdrawiamy,\n" +
                    "Zespół MeditHub";

//            emailSenderService.sendEmail(patient.getEmail(), subject, text);

            return new ResponseEntity<>("Order added succesfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Order addition failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> deleteOrderFromPatient(String orderId, String patientId, String authorizationHeader) {
        try {
            Optional<Patient> patientOptional = patientRepository.findById(patientId);
            if (patientOptional.isEmpty()) {
                return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
            }
            Patient patient = patientOptional.get();
            List<Order> orders = patient.getOrders();
            Order myorder = orders.stream().filter(order -> order.getId().equals(orderId)).findFirst().orElse(null);
            orders.removeIf(order -> order.getId().equals(orderId));
            patientRepository.save(patient);


            String subject = "Odwołanie wizyty u doktora";
            String doctorName = myorder.getDoctorName();
            String date = myorder.getDate();
            String time = myorder.getTime();

            String text = "Witaj, " + patient.getName() + "!\n\n" +
                    "Informujemy, że wizyta u doktora " + doctorName + " w dniu " + date + " o godzinie " + time + " została odwołana.\n\n" +
                    "Pozdrawiamy,\n" +
                    "Zespół MeditHub";

//            emailSenderService.sendEmail(patient.getEmail(), subject, text);

            return new ResponseEntity<>("Order deleted succesfully", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Order deletion failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
