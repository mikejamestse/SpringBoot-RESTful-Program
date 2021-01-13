package com.example.payroll.controller;

import com.example.payroll.bean.Order;
import com.example.payroll.bean.Status;
import com.example.payroll.exception.OrderNotFoundException;
import com.example.payroll.hateoas.OrderModelResourceAssembler;
import com.example.payroll.repository.OrderRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrderController {
    private final OrderRepository repository;
    private final OrderModelResourceAssembler assembler;

    OrderController(OrderRepository repository, OrderModelResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    /* Get all orders */
    @GetMapping("/orders")
    public CollectionModel<EntityModel<Order>> all() {
        List<EntityModel<Order>> orders = repository.findAll()
                .stream().map(assembler :: toModel).collect(Collectors.toList());

        return CollectionModel.of(orders,
                WebMvcLinkBuilder
                        .linkTo(WebMvcLinkBuilder
                                .methodOn(OrderController.class).all()).withSelfRel());
    }

    /* Get a specific order by id */
    @GetMapping("/orders/{id}")
    public EntityModel<Order> one(@PathVariable Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

        return assembler.toModel(order);
    }

    /* Create a new order */
    @PostMapping("/orders")
    ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {
        // New orders have a IN_PROGRESS status
        order.setStatus(Status.IN_PROGRESS);

        Order newOrder = repository.save(order);

        return ResponseEntity.created(WebMvcLinkBuilder
                .linkTo(WebMvcLinkBuilder.methodOn(OrderController.class).one(newOrder.getId()))
                .toUri())
                .body(assembler.toModel(newOrder));
    }

    /* Cancel an order (delete) */
    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        ResponseEntity<?> response;

        // An order can be canceled ONLY is the status is in progress
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.CANCELLED);
            response = ResponseEntity.ok(assembler.toModel(repository.save(order)));
        } else {
            response = ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                    .body(Problem.create()
                            .withTitle("Method not allowed")
                            .withDetail("Cannot cancel an order that is in the " + order.getStatus() + " status"));
        }

        return response;
    }

    /* Complete an order */
    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {
        Order order = repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        ResponseEntity<?> response;

        // An order is completed ONLY is the status is in progress
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.COMPLETED);
            response = ResponseEntity.ok(assembler.toModel(repository.save(order)));
        } else {
            response = ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                    .body(Problem.create()
                            .withTitle("Method not allowed")
                            .withDetail("Cannot complete an order that is in the " + order.getStatus() + " status"));
        }

        return response;
    }

}
