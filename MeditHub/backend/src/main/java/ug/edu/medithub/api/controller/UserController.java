package ug.edu.medithub.api.controller;


import ug.edu.medithub.api.models.FoundUser;
import ug.edu.medithub.api.models.Order;
import ug.edu.medithub.api.models.Patient;
import ug.edu.medithub.api.models.User;
import ug.edu.medithub.api.service.EmailSenderService;
import ug.edu.medithub.api.service.PatientService;
import ug.edu.medithub.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final PatientService patientService;

    private final EmailSenderService emailSenderService;


    @Autowired
    public UserController(UserService userService, PatientService patientService, EmailSenderService emailSenderService) {
        this.userService = userService;
        this.patientService = patientService;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/list")
    public List<FoundUser> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        return userService.loginUser(user);
    }

    @PostMapping("/test")
    public String test(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        String token = userService.extractToken(authorizationHeader);

        return userService.test(token);
    }


    @GetMapping("/get/{id}")
    public FoundUser getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable String id, @RequestBody FoundUser user, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return userService.updateUser(id, user, authorizationHeader);
    }

    @GetMapping("/getPatients")
    public List<Patient> getPatients() {
        return patientService.getAllPatients();
    }

    @PostMapping("/addPatient")
    public ResponseEntity<String> addPatient(@RequestBody Patient patient) {
        return patientService.addPatient(patient);
    }

    @PostMapping("/addOrder/{patientId}")
    public ResponseEntity<String> addOrderForPatient(@PathVariable String patientId, @RequestBody Order order, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return patientService.addOrderForPatient(patientId, order, authorizationHeader);
    }

    @DeleteMapping("/deleteOrder/{orderId}/{patientName}")
    public ResponseEntity<String> deleteOrderFromPatient(@PathVariable String orderId, @PathVariable String patientName, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return patientService.deleteOrderFromPatient(orderId, patientName, authorizationHeader);
    }

}
