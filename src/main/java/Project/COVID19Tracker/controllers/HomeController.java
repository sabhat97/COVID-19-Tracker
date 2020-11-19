package Project.COVID19Tracker.controllers;

import Project.COVID19Tracker.models.locationStats;
import Project.COVID19Tracker.services.COVID19DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    COVID19DataService covid19DataService;

    // whenever there is get mapping to the home url call this
    @GetMapping("/")
    // We can put anything inside the model which can be accessed in the HTML page
    public String home(Model model){
       List<locationStats> allStats = covid19DataService.getAllStats();
        int totalCases = allStats.stream().mapToInt(stat -> stat.getTotalNewCases()).sum();
        int newCasesFromPrev = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalCases);
        model.addAttribute("newCaseFromPrevDay", newCasesFromPrev);
        //returning a template value (A name which maps to the template)
        // whatever we put in the home.html it will return this
        return "home";
    }
}

