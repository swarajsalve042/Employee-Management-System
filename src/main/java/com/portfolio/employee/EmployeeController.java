package com.portfolio.employee;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$");

    private final EmployeeRepository repo;

    public EmployeeController(EmployeeRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Employee> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Employee get(@PathVariable Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@RequestBody Employee employee) {
        validate(employee);
        normalize(employee);
        return repo.save(employee);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable Long id, @RequestBody Employee input) {
        Employee employee = get(id);
        validate(input);
        employee.setName(input.getName().trim());
        employee.setDepartment(cleanDepartment(input.getDepartment()));
        employee.setEmail(input.getEmail().trim().toLowerCase());
        return repo.save(employee);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repo.deleteById(id);
    }

    private void validate(Employee employee) {
        if (employee.getName() == null || employee.getName().isBlank()
                || employee.getName().trim().length() > 100
                || employee.getEmail() == null || employee.getEmail().isBlank()
                || employee.getEmail().trim().length() > 254
                || !EMAIL_PATTERN.matcher(employee.getEmail().trim()).matches()
                || (employee.getDepartment() != null
                    && employee.getDepartment().trim().length() > 80)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "name (1-100 characters) and a valid email are required; department must be at most 80 characters");
        }
    }

    private void normalize(Employee employee) {
        employee.setName(employee.getName().trim());
        employee.setEmail(employee.getEmail().trim().toLowerCase());
        employee.setDepartment(cleanDepartment(employee.getDepartment()));
    }

    private String cleanDepartment(String department) {
        return department == null || department.isBlank() ? null : department.trim();
    }
}
