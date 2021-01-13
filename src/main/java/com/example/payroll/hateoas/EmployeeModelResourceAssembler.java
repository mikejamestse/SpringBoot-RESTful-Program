package com.example.payroll.hateoas;

import com.example.payroll.bean.Employee;
import com.example.payroll.controller.EmployeeController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class EmployeeModelResourceAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {
    @Override
    public EntityModel<Employee> toModel(Employee employee) {
        return EntityModel.of(employee,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.
                        methodOn(EmployeeController.class).
                        one(employee.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.
                        methodOn(EmployeeController.class).
                        all()).withRel("employees"));
    }
}
