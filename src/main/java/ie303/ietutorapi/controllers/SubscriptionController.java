package ie303.ietutorapi.controllers;

import ie303.ietutorapi.models.Subscription;
import ie303.ietutorapi.models.User;
import ie303.ietutorapi.repositories.SubscriptionRepository;
import ie303.ietutorapi.repositories.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class SubscriptionController {

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    @Autowired
    private UserRepository userRepo;

    // get all plans
    @GetMapping("/subscriptions")
    public List<Subscription> getAllSubscriptionsWithEmail() {
        List<Subscription> subscriptions = subscriptionRepo.findAll();

        for (Subscription subscription : subscriptions) {
            String userId = subscription.getUserId();
            Optional<User> user = userRepo.findById(userId);
            if (user.isPresent()) {
                String email = user.get().getEmail();
                subscription.setEmail(email);
            }
        }

        return subscriptions;
    }


    // save a subscription
    @PostMapping("/subscriptions")
    public ResponseEntity<?> saveSubscription(@RequestBody SubscriptionController.Json requestBody) {
        // find the user and set is_activated field of user to true
        userRepo.findUsersById(requestBody.userId).ifPresent(user -> {
            user.setIsActivated(true);
            userRepo.save(user);
        });

        // Save the subscription to the database
        Subscription subscription = new Subscription();
        subscription.setUserId(requestBody.userId);
        subscription.setPlanId(requestBody.planId);
        subscription.setDuration(requestBody.duration);
        subscription.setPaymentMethodId(requestBody.paymentMethodId);
        subscription.setTotal(requestBody.total);
        subscription.setType(requestBody.type);
        subscription.setStatus("success");

        // created_at is set automatically by MongoDB
        subscription.setCreatedAt(new java.util.Date());
        subscription.setStartDate(new java.util.Date());

        // set end date, which is start date + duration * 30 days
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(subscription.getStartDate());
        cal.add(java.util.Calendar.DAY_OF_MONTH, subscription.getDuration() * 30);
        subscription.setEndDate(cal.getTime());

        subscriptionRepo.save(subscription);

        return ResponseEntity.ok("Successfully saved Subscription");
    }

    @GetMapping("/revenue-month")
    public ResponseEntity<Double> getTotalRevenueForCurrentMonth() {
        // Get the current month and year
        YearMonth currentMonth = YearMonth.now();
        LocalDate startDate = currentMonth.atDay(1);
        LocalDate endDate = currentMonth.atEndOfMonth();

        // Get all subscriptions within the current month
        List<Subscription> subscriptions = subscriptionRepo.findByStartDateBetween(startDate, endDate);

        // Calculate the total revenue
        double totalRevenue = subscriptions.stream()
                .mapToDouble(Subscription::getTotal)
                .sum();

        return ResponseEntity.ok(totalRevenue);
    }

    @GetMapping("/revenue/{month}/{year}")
    public List<RevenueData> getRevenueByMonthAndYear(@PathVariable int month, @PathVariable int year) {
        List<RevenueData> revenueList = new ArrayList<>();

        // Lấy tháng và năm hiện tại
        YearMonth currentYearMonth = YearMonth.now();
        int currentYear = currentYearMonth.getYear();
        int currentMonth = currentYearMonth.getMonthValue();

        // Lặp qua 12 tháng trước từ tháng, năm đã nhập
        for (int i = 0; i < 12; i++) {
            int targetYear = year;
            int targetMonth = month;

            // Nếu năm hiện tại và tháng hiện tại đã trước tháng, năm đã nhập, giảm năm đi 1
            /*if (currentMonth < targetMonth || currentYear < targetYear) {
                targetYear--;
            }*/

            // Tạo LocalDate từ tháng và năm đã xác định
            YearMonth targetYearMonth = YearMonth.of(targetYear, targetMonth);
            LocalDate startDate = targetYearMonth.atDay(1);
            LocalDate endDate = targetYearMonth.atEndOfMonth();

            // Lấy danh sách Subscription trong khoảng thời gian đã xác định
            List<Subscription> subscriptions = subscriptionRepo.findByStartDateBetween(startDate, endDate);

            // Tính tổng doanh thu
            double totalRevenue = 0;
            for (Subscription subscription : subscriptions) {
                totalRevenue += subscription.getTotal();
            }

            // Tạo đối tượng RevenueData và thêm vào danh sách
            RevenueData revenueData = new RevenueData();
            revenueData.setMonth(targetYearMonth.format(DateTimeFormatter.ofPattern("MMMM")));
            revenueData.setYear(targetYear);
            revenueData.setTotalRevenue(totalRevenue);
            revenueList.add(revenueData);

            // Giảm tháng đi 1 để lặp lại cho tháng trước
            month--;
            if (month == 0) {
                month = 12;
                year--;
            }
        }
        Collections.reverse(revenueList);

        return revenueList;
    }

    @GetMapping("/subscriptions/count-by-plan")
    public ResponseEntity<List<PlanCount>> getCountByPlan(
            @RequestParam int startMonth, @RequestParam int startYear,
            @RequestParam int endMonth, @RequestParam int endYear) {

        LocalDate startDate = YearMonth.of(startYear, startMonth).atDay(1);
        LocalDate endDate = YearMonth.of(endYear, endMonth).atEndOfMonth();

        List<Subscription> subscriptions = subscriptionRepo.findByCreatedAtBetween(startDate, endDate);

        Map<String, Long> countByPlan = new HashMap<>();

        for (Subscription subscription : subscriptions) {
            String type = subscription.getType();
            countByPlan.put(type, countByPlan.getOrDefault(type, 0L) + 1);
        }

        List<PlanCount> result = new ArrayList<>();
        for (Map.Entry<String, Long> entry : countByPlan.entrySet()) {
            String type = entry.getKey();
            Long count = entry.getValue();
            result.add(new PlanCount(type, count));
        }

        return ResponseEntity.ok(result);
    }

    @Getter
    @Setter
    public static class Json {
        public String userId;
        public String planId;
        public Integer duration;
        public String paymentMethodId;
        public Double total;
        public String type;
    }

    //Chart revenue every month
    @Getter
    @Setter
    public class RevenueData {
        private String month;
        private int year;
        private double totalRevenue;
    }

    // Chart plan
    @Getter
    @Setter
    public class PlanCount {
        private String type;
        private long count;

        public PlanCount(String type, Long count) {
            this.type = type;
            this.count = count;
        }
    }

}
