package ie303.ietutorapi.controllers;

import ie303.ietutorapi.models.Plan;
import ie303.ietutorapi.repositories.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class PlanController {
    @Autowired
    private PlanRepository planRepo;

    // get all plans
    @GetMapping("/plans")
    public ResponseEntity<List<Plan>> getAllPlans() {
        // Get all plans from MongoDB database
        return ResponseEntity.ok(planRepo.findAll());
    }
    // add a new plan
    @PostMapping("/addplan")
    public Plan addPlan(@RequestBody Plan plan) {
        return planRepo.save(plan);
    }
    // find plan by id
    @GetMapping("/planbyID/{id}")
    public ResponseEntity<Plan> getPlanById(@PathVariable String id) {
        Optional<Plan> plan = planRepo.findById(id);
        if (plan.isPresent()) {
            return ResponseEntity.ok(plan.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // update plan by id
    @PutMapping("/editplan/{id}")
    public ResponseEntity<Plan> updatePlanById(@PathVariable String id, @RequestBody Plan updatedPlan) {
        Optional<Plan> existingPlan = planRepo.findById(id);
        if (existingPlan.isPresent()) {
            Plan plan = existingPlan.get();
            plan.setType(updatedPlan.getType());
            plan.setPrice(updatedPlan.getPrice());
            plan.setDuration(updatedPlan.getDuration());
            plan.setBgColor(updatedPlan.getBgColor());

            // save updated plan to database
            Plan updatedPlanID = planRepo.save(plan);
            return ResponseEntity.ok(updatedPlanID);
        } else {
            // return 404 not found if plan is not found
            return ResponseEntity.notFound().build();
        }
    }

    // delete plan by id
    @DeleteMapping("/deleteplan/{id}")
    public ResponseEntity<String> deletePlanById(@PathVariable String id) {
        Optional<Plan> plan = planRepo.findById(id);
        if (plan.isPresent()) {
            planRepo.deleteById(id);
            return ResponseEntity.ok("Plan has been deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
