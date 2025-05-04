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
import java.util.ArrayList;
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
        return patientRepository.findAll();
    }

    public ResponseEntity<String> addPatient(Patient patient) {
        try {
            List<Patient> patientOptional = patientRepository.findByEmail(patient.getEmail());
            if (!patientOptional.isEmpty()) {
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

    public ResponseEntity<String> deletePatient (String patientId) {
        try {
            Optional<Patient> patientOptional = patientRepository.findById(patientId);

            if (patientOptional.isEmpty()) {
                return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
            }

            Patient patient = patientOptional.get();

            patientRepository.deleteById(patient.getId());
            return new ResponseEntity<>("Patient Deleted", HttpStatus.OK);




        } catch (Exception e) {
            return new ResponseEntity<>("Deletion failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> updatePatient (String patientId, Patient patient) {
        try {
            Optional<Patient> optionalPatient = patientRepository.findById(patientId);

            if(optionalPatient.isEmpty()) {
                return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
            }

            Patient patientToUpdate = optionalPatient.get();

            patientToUpdate.setEmail(patient.getEmail());
            patientToUpdate.setName(patient.getName());
            patientToUpdate.setEmail(patient.getEmail());
            patientToUpdate.setDateOfBirth(patient.getDateOfBirth());
            patientToUpdate.setDateOfBirth(patient.getDateOfBirth());

            patientRepository.save(patientToUpdate);

            return new ResponseEntity<>("Patient updated sucessfully", HttpStatus.OK);




        } catch (Exception e) {
            return new ResponseEntity<>("Update failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<String> editOrder(String patientId, Patient patient, String orderId, Order orderTo) {
        try {
            Optional<Patient> optionalPatient = patientRepository.findById(patientId);

            if (optionalPatient.isEmpty()) {
                return new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND);
            }
            Patient patientToUpdate = optionalPatient.get();
            List<Order> orders = patientToUpdate.getOrders();
            Optional<Order> optionalOrder = orders.stream().filter(order -> order.getId().equals(orderId)).findFirst();
            if (optionalOrder.isEmpty()) {
                return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
            }

            Order orderToUpdate = optionalOrder.get();

            orderToUpdate.setDate(orderTo.getDate());
            orderToUpdate.setDescription(orderTo.getDescription());
            orderToUpdate.setTime(orderTo.getTime());
            orderToUpdate.setDoctorName(orderTo.getDoctorName());
            orderToUpdate.setToothArray(orderTo.getToothArray());

            patientToUpdate.editOrder(orderToUpdate);

            patientRepository.save(patientToUpdate);

            String subject = "Zmiana terminu wizyty u doktora";
            String text = getString(orderToUpdate, patientToUpdate);

            emailSenderService.sendEmail(patientToUpdate.getEmail(), subject, text);

            return new ResponseEntity<>("Order edited successfully", HttpStatus.OK);




        } catch (Exception e) {
            return new ResponseEntity<>("Order edition failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static String getString(Order orderToUpdate, Patient patientToUpdate) {
        String doctorName = orderToUpdate.getDoctorName();
        String date = orderToUpdate.getDate();
        String time = orderToUpdate.getTime();
        String description = orderToUpdate.getDescription();

        return "Witaj, " + patientToUpdate.getName() + "!\n\n" +
                "Informujemy, że wizyta u doktora " + doctorName + " została zmieniona.\n" +
                "Nowa data: " + date + "\n" +
                "Nowa godzina: " + time + "\n" +
                "Nowy opis: " + description + "\n\n" +
                "Pamiętaj o punktualności!\n\n" +
                "Pozdrawiamy,\n" +
                "Zespół MeditHub";
    }



}
