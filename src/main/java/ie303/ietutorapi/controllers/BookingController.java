package ie303.ietutorapi.controllers;

import ie303.ietutorapi.models.Booking;
import ie303.ietutorapi.models.Notification;
import ie303.ietutorapi.models.User;
import ie303.ietutorapi.repositories.BookingRepository;
import ie303.ietutorapi.repositories.NotificationRepository;
import ie303.ietutorapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BookingController {
    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private NotificationRepository notificationRepo;

    @Autowired
    private UserRepository userRepo;

    // Hàm kiểm tra số ngày cách nhau giữa 2 java.util.Date
    //check number of days between 2 dates
    public static long daysBetween(Date one, Date two) {
        long difference = (one.getTime() - two.getTime()) / 86400000;
        return Math.abs(difference);
    }


    // Get all bookings
    @GetMapping("/bookings")
    public ResponseEntity<?> getAllBookings() {
        // Get all bookings from MongoDB database
        List<Booking> bookings = bookingRepo.findAll();

        if (bookings.isEmpty()) {
            // return json object with error message
            return ResponseEntity.badRequest().body("No bookings found");
        }
        //return json object with bookings
        return ResponseEntity.ok(bookings);
    }

// Booking must be waiting for approval, and if after 24 hours, the booking is still waiting for approval, it will be automatically rejected
    @GetMapping("/bookings/check")
    public ResponseEntity<?> checkBooking() {
        // Get all bookings from MongoDB database
        List<Booking> bookings = bookingRepo.findAll();

        if (bookings.isEmpty()) {
            // return json object with error message
            return ResponseEntity.badRequest().body("No bookings found");
        }
        //return json object with bookings
        for (Booking booking : bookings) {
            if (booking.getStatus().equals("waiting for approval")) {
                Date date = new Date();
                long days = daysBetween(date, booking.getCreatedAt());
                if (days >= 1) {
                    booking.setStatus("canceled");
                    bookingRepo.save(booking);
                    notificationRepo.save(new Notification(booking.getStudentId(), String.format("Your booking with id %s has been rejected due to timeout", booking.getId())));
                }
            }
        }
        return ResponseEntity.ok("Check booking success");
    }


    // Approve a booking
    @PutMapping("/bookings/{id}")
    public ResponseEntity<?> approveBooking(@PathVariable String id) {
        // Get booking by id from MongoDB database
        Booking booking = bookingRepo.findById(id).orElse(null);

        if (booking == null) {
            // return json object with error message
            return ResponseEntity.badRequest().body("No booking found");
        }


        // Update booking status to "approved"
        booking.setStatus("approved");

        // Save updated booking to MongoDB database
        bookingRepo.save(booking);

        // send notification to student
        notificationRepo.save(new Notification(booking.getStudentId(), String.format("Your booking with id %s has been approved", booking.getId())));



        // return json object with success message
        return ResponseEntity.ok("Booking approved");
    }

    // Reject a booking
    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> rejectBooking(@PathVariable String id) {
        System.out.println(id);
        // Get booking by id from MongoDB database
        Booking booking = bookingRepo.findById(id).orElse(null);

        if (booking == null) {
            // return json object with error message
            return ResponseEntity.badRequest().body("No booking found");
        }

        // Update booking status to "rejected"
        booking.setStatus("canceled");

        // Save updated booking to MongoDB database
        bookingRepo.save(booking);

        // send notification to student
        notificationRepo.save(new Notification(booking.getStudentId(), String.format("Your booking with id %s has been rejected", booking.getId())));

        // return json object with success message
        return ResponseEntity.ok("Booking rejected");
    }











    /*
    @Id
    private String id;

    @Field("instructor_id")
    private String instructorId;

    @Field("student_id")
    private String studentDd;

    @Field("subject_id")
    private String subjectId;

    @Field("startTime")
    private String startTime;

    @Field("endTime")
    private String endTime;

    @Field("booking_status")
    private String bookingStatus;

    @Field("Created_at")
    private Date createdAt; // date of the booking
    private Recurrence recurrence;

    public static class Recurrence {

        @Field("frequency")
        private String frequency;
        @Field("interval")
        private int interval;
        @Field("end_date")
        private String endDate;

        // ...
    }
    * */

    // add new booking

    // Get all bookings for an instructor
    @GetMapping("/bookings/{id}")
    public ResponseEntity<?> getBookingsByUserId(@PathVariable("id") String id) {
        // Get all bookings from MongoDB database that belong to the user with the given id
        List<Booking> bookings = bookingRepo.findByInstructorId(id);

        return ResponseEntity.ok(bookings);
    }

    public boolean isConflict(Booking newBooking) {
        List<Booking> bookings = bookingRepo.findByInstructorId(newBooking.getInstructorId());
        for (Booking b : bookings) {
            Date newBookingStart = newBooking.getStartDate();
            Date newBookingEnd = newBooking.getEndDate();
            Date bStart = b.getStartDate();
            Date bEnd = b.getEndDate();

            // newBookingStart >= bEnd or newBookingEnd <= bStart, no conflict
            if (!(newBookingStart.compareTo(bEnd) >= 0 || newBookingEnd.compareTo(bStart) <= 0))
                return true;
        }
        return false;
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        // Kiểm tra xem có bị conflict thời gian với các booking đã có của instructor không
        if (isConflict(booking))
            return ResponseEntity.badRequest().body("Booking time conflict");

        booking.setCreatedAt(new Date());
        bookingRepo.save(booking);

        // send notification to instructor who has been booked
        notificationRepo.save(new Notification(booking.getInstructorId(), String.format("You have a new booking with id %s", booking.getId())));

        return ResponseEntity.ok(booking);
    }

    // Get all approved bookings for an instructor
    @GetMapping("/bookings/approved/{id}")
    public ResponseEntity<?> getApproveBooking(@PathVariable String id) {
        // find out if the id is instructor or student
        User user = userRepo.findById(id).orElse(null);

        // Get all bookings from MongoDB database that belong to the user with the given id
        List<Booking> bookings;

        if (user == null)
            return ResponseEntity.badRequest().body("No user found");

        if (user.getRole() == 0)
            bookings = bookingRepo.findByStudentIdAndStatus(id, "approved");
        else
            bookings = bookingRepo.findByInstructorIdAndStatus(id, "approved");


        return ResponseEntity.ok(bookings);
    }

    // Get all approved bookings for a student
    @GetMapping("/bookings/approved/student/{id}")
    public ResponseEntity<?> getApproveBookingByStudentId(@PathVariable String id) {
        // Get all bookings from MongoDB database that belong to the user with the given id
        List<Booking> bookings = bookingRepo.findByStudentIdAndStatus(id, "approved");
        return ResponseEntity.ok(bookings);
    }

    // Get all booking of a student
    @GetMapping("/bookings/student/{id}")
    public ResponseEntity<?> getBookingsByStudentId(@PathVariable String id) {
        // Get all bookings from MongoDB database that belong to the user with the given id
        List<Booking> bookings = bookingRepo.findByStudentId(id);

        return ResponseEntity.ok(bookings);
    }

    // Get all booking of an instructor
    @GetMapping("/bookings/instructor/{id}")
    public ResponseEntity<?> getBookingsByInstructorId(@PathVariable String id) {
        // Get all bookings from MongoDB database that belong to the user with the given id
        List<Booking> bookings = bookingRepo.findByInstructorId(id);

        return ResponseEntity.ok(bookings);
    }



}