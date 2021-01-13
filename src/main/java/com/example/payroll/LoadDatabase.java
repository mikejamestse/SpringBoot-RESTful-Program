package com.example.payroll;

import com.example.payroll.bean.Employee;
import com.example.payroll.bean.Order;
import com.example.payroll.bean.Status;
import com.example.payroll.repository.EmployeeRepository;
import com.example.payroll.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {
        return args -> {
            log.info("Preloading " + employeeRepository
                    .save(new Employee("Jonathan", "Grey", "Cook")));
            log.info("Preloading " + employeeRepository
                    .save(new Employee("Samantha", "Greenwood", "Maid")));

            log.info("Preloading " + orderRepository
            .save(new Order("Knife Set", Status.IN_PROGRESS)));

            log.info("Preloading " + orderRepository
                    .save(new Order("Latex Gloves SM", Status.IN_PROGRESS)));

        };
    }


}
